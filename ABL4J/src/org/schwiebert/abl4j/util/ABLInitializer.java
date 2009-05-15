/**********************************************************************
 * Copyright (C) 2007-2009  Stephan Schwiebert
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 **********************************************************************/
package org.schwiebert.abl4j.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.schwiebert.abl4j.align.methods.AllAlignmentsMethod;
import org.schwiebert.abl4j.align.methods.LeftAlignmentMethod;
import org.schwiebert.abl4j.align.methods.LeftAndRightAlignmentMethod;
import org.schwiebert.abl4j.align.methods.RightAlignmentMethod;
import org.schwiebert.abl4j.align.methods.SuffixTreeAlignment;
import org.schwiebert.abl4j.align.methods.WagnerFisherBiasedAlignmentMethod;
import org.schwiebert.abl4j.align.methods.WagnerFisherDefaultAlignmentMethod;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.data.impl.abl.Constituent;
import org.schwiebert.abl4j.data.impl.abl.Sentence;
import org.schwiebert.abl4j.data.impl.abl.Tree;
import org.schwiebert.abl4j.data.impl.abl.TreeBank;
import org.schwiebert.abl4j.data.impl.abl.Word;
import org.schwiebert.abl4j.io.PlainTextSerializationVisitor;
import org.schwiebert.abl4j.io.TreebankReader;
import org.schwiebert.abl4j.io.TreebankWriter;
import org.schwiebert.abl4j.select.methods.ConstituentSelectMethod;
import org.schwiebert.abl4j.select.methods.FirstSelectMethod;
import org.schwiebert.abl4j.select.methods.TermsSelectMethod;

/**
 * The ABLInitializer should be used to configure the ABL4J framework.
 * Configuration of ABL4J is done by setting properties in a {@link PropertiesMap}.
 * The AblInitializer must be initialized by calling {@link ABLInitializer#initialize(String[])}.
 * It configures Abl4J by first setting default properties (see {@link AblProperties} for default values),
 *  than adding (or overriding) properties loaded from the file "abl4j.properties" (if found
 * on the classpath), and finally adding (or overriding) the properties with the values
 * used as parameter on {@link ABLInitializer#initialize(String[])}. Additionally, properties
 * can be added by {@link ABLInitializer#addProperty(String, String)} etc.
 * <br/>
 * Note: Although the configuration of ABL4J is accessed via the static method
 * {@link ABLInitializer#getProperties()}, multiple configurations of ABL can be used
 * within the same virtual machine, as the {@link PropertiesMap} is stored within
 * an {@link InheritableThreadLocal}.
 * 
 * @author sschwieb
 *
 */
public class ABLInitializer {
	
	private InheritableThreadLocal<PropertiesMap> properties = new InheritableThreadLocal<PropertiesMap>();
	
	private Logger logger = Logger.getLogger(ABLInitializer.class);
	
	/**
	 * Log4J should be configured only once
	 */
	private static Boolean log4JConfigured = false;
	
	public void initialize() {
		initialize(null, null);
	}
	
	public void initialize(Properties props) {
		initialize(null, props);
	}
	
	public void initialize(String[] arguments) {
		initialize(arguments, null);
	}
	
	/**
	 * Initializes ABL4J as a 4-step-process:
	 * <ol>
	 * <li>load default values</li>
	 * <li>override (or add) properties found in file abl4j.properties</li>
	 * <li>override (or add) properties of parameter <code>props</code></li>
	 * <li>override (or add) properties of parameter <code>arguments</code></li>
	 * </ol>
	 * @param arguments the properties to add.
	 */
	public void initialize(String[] arguments, Properties props) {
		synchronized (log4JConfigured) {
			if(!log4JConfigured) {
				PropertyConfigurator.configure("log4j.properties");				
				log4JConfigured = true;
			}
		}
		setDefaults();
		loadProperties();
		if(props != null) {
			Set<Object> keySet = props.keySet();
			for (Object object : keySet) {
				Object value = props.get(object);
				if(value != null) {
					properties.get().put(object, value+"");
				} else {
					properties.get().put(object, null);
				}
			}
		}
		overrideProperties(arguments);
		try {
			boolean resetUpperNt = properties.get().getBoolean(AblProperties.RESET_UPPER_NT);
			if(resetUpperNt) {
				NonTerminal.resetUpperNt();
			}
			IWordMapping mapping = (IWordMapping) properties.get().getNewClassInstance(AblProperties.WORD_MAPPING);
			DataFactory.setMapping(mapping);
			DataFactory.initialize(properties.get());
			Tools.configure(properties.get());
			Tools.setSeed(properties.get().getInteger(AblProperties.SEED));
		} catch (Exception e) {
			logger.error("Initialization failed - a required data class couldn't be found");
			logger.error("Error", e);
		}
	}

	private void setDefaults() {
		properties.set(new PropertiesMap(this));
		addProperty(AblProperties.PART_TYPE, "unequal");
		addProperty(AblProperties.SELECT_METHOD, "const");
		addProperty(AblProperties.ALIGN_TYPE, AllAlignmentsMethod.class.getName());
		addProperty(AblProperties.EXCLUDE_EMPTY, false);
		addProperty(AblProperties.VERBOSE, false);
		addProperty(AblProperties.EXHAUSTIVE, false);
		addProperty(AblProperties.COMPATIBILITY_MODE, false);
		addProperty(AblProperties.NO_MERGE, false);
		addProperty(AblProperties.PRESERVE, false);
		addProperty(AblProperties.INPUT_IGNORE_ANNOTATIONS, false);
		addProperty(AblProperties.INPUT_ENCODING, "UTF-8");
		addProperty(AblProperties.READER_CLASS, TreebankReader.class.getName());
		addProperty(AblProperties.WRITER_CLASS, TreebankWriter.class.getName());
		addProperty(AblProperties.SERIALISATION_VISITOR, PlainTextSerializationVisitor.class.getName());
		addProperty(AblProperties.OUTPUT_ENCODING, "UTF-8");
		addProperty(AblProperties.THREADS, 1);
		addProperty(AblProperties.SEED, 0);
		addProperty(AblProperties.COMPARISM_MODE, true);
		addProperty(AblProperties.CONSTITUENT_CLASS, Constituent.class.getName());
		addProperty(AblProperties.SENTENCE_CLASS, Sentence.class.getName());
		addProperty(AblProperties.TREEBANK_CLASS, TreeBank.class.getName());
		addProperty(AblProperties.TREE_CLASS, Tree.class.getName());
		addProperty(AblProperties.WORD_CLASS, Word.class.getName());
		addProperty(AblProperties.WORD_MAPPING, StringMapping.class.getName());
		addProperty(AblProperties.RESET_UPPER_NT, true);
	}

	private void addProperty(String key, boolean value) {
		addProperty(key, value+"");
	}

	private void addProperty(String key, int value) {
		addProperty(key, value + "");
	}

	private static int getOption(String string) {
		if (string.length() < 2 || string.charAt(0) != '-') {
			return -1;
		}
		return string.charAt(1);
	}

	private void loadProperties() {
		File file = new File("abl4j.properties");
		if(file.exists()) {
			logger.debug("Loading configuration from " + file.getAbsolutePath());
			try {
				loadProperties(new FileInputStream(file));
			} catch (IOException e) {
				logger.error("Couldn't load configuration", e);
			}
		} else {
			URL url = ClassLoader.getSystemResource("abl4j.properties");
			if(url != null) {
				logger.debug("Loading configuration from " + url);
				try {
					loadProperties(url.openStream());
				} catch (IOException e) {
					logger.error("Couldn't load configuration", e);
				}
			} else {
				logger.warn("No abl4j.properties found.");
			}
		}
	}

	private void loadProperties(InputStream in) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		properties.get().load(bis);
		bis.close();
	}
	

	private void overrideProperties(String[] args) {
		if(args != null) {
			logger.trace("Loading additional configuration");
			int opt;
			StringBuffer options = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
				opt = getOption(args[i]);
				options.append(args[i] + " ");
				switch (opt) {
				case 'a':
					String optarg = args[++i];
					if("R".equalsIgnoreCase(optarg) || "RIGHT".equalsIgnoreCase(optarg)) optarg = RightAlignmentMethod.class.getName();
					if("L".equalsIgnoreCase(optarg) || "LEFT".equalsIgnoreCase(optarg)) optarg = LeftAlignmentMethod.class.getName();
					if("B".equalsIgnoreCase(optarg) || "BOTH".equalsIgnoreCase(optarg)) optarg = LeftAndRightAlignmentMethod.class.getName();
					if("AA".equalsIgnoreCase(optarg) || "A".equalsIgnoreCase(optarg) || "ALL".equalsIgnoreCase(optarg)) optarg = AllAlignmentsMethod.class.getName();
					if("WB".equalsIgnoreCase(optarg) || "WAGNER_BIASED".equalsIgnoreCase(optarg)) optarg = WagnerFisherBiasedAlignmentMethod.class.getName();
					if("WM".equalsIgnoreCase(optarg) || "WAGNER_MIN".equalsIgnoreCase(optarg)) optarg = WagnerFisherDefaultAlignmentMethod.class.getName();
					if("ST1".equalsIgnoreCase(optarg) || "SUFFIX_TREE_1".equalsIgnoreCase(optarg)) optarg = SuffixTreeAlignment.class.getName();
					if("ST2".equalsIgnoreCase(optarg) || "SUFFIX_TREE_2".equalsIgnoreCase(optarg)) optarg = SuffixTreeAlignment.class.getName();
					if("ST3".equalsIgnoreCase(optarg) || "SUFFIX_TREE_3".equalsIgnoreCase(optarg)) optarg = SuffixTreeAlignment.class.getName();
					if("ST4".equalsIgnoreCase(optarg) || "SUFFIX_TREE_4".equalsIgnoreCase(optarg)) optarg = SuffixTreeAlignment.class.getName();
					addProperty(AblProperties.ALIGN_TYPE, optarg);
					break;
				case 'i':
					optarg = args[++i];
					addProperty(AblProperties.INPUT_FILE, optarg);
					break;
				case 'n':
					addProperty(AblProperties.NO_MERGE, "true");
					break;
				case 'o':
					optarg = args[++i];
					addProperty(AblProperties.OUTPUT_FILE, optarg);
					break;
				case 'p':
					optarg = args[++i];
					if("e".equalsIgnoreCase(optarg) || "equal".equalsIgnoreCase(optarg)) {
						optarg = "equal";
					}
					if("u".equalsIgnoreCase(optarg) || "unequal".equalsIgnoreCase(optarg)) {
						optarg = "unequal";
					}
					if("b".equalsIgnoreCase(optarg) || "both".equalsIgnoreCase(optarg)) {
						optarg = "both";
					}
					addProperty(AblProperties.PART_TYPE, optarg);
					break;
				case 'm':
					addProperty(AblProperties.PRESERVE, "true");
				case 'r':
					optarg = args[++i];
					addProperty(AblProperties.SEED, optarg);
					break;
				case 'e':
					addProperty(AblProperties.EXCLUDE_EMPTY, "true");
					break;
				case 'v':
					addProperty(AblProperties.VERBOSE, "true");
					break;
				case 'x':
					addProperty(AblProperties.EXHAUSTIVE, "true");
					break;
				case 'j':
					addProperty(AblProperties.COMPATIBILITY_MODE, "true");
					break;
				case 's':
					optarg = args[++i];
					/**
					 * 
		System.out.println("                       - first, f:");
		System.out.println("                           earlier learned constituents are " + "                           correct");
		System.out.println("                       - terms, t, leaf, l:");
		System.out.println("                           terms selection method");
		System.out.println("                       - const, c, branch, b:");
		System.out.println("                           const selection method");
					 */
					if("f".equalsIgnoreCase(optarg) || "first".equalsIgnoreCase(optarg)) {
						optarg = FirstSelectMethod.class.getName();
					}
					if("t".equalsIgnoreCase(optarg) || "terms".equalsIgnoreCase(optarg) || "leaf".equalsIgnoreCase(optarg) || "l".equalsIgnoreCase(optarg)) {
						optarg = TermsSelectMethod.class.getName();
					}
					if("c".equalsIgnoreCase(optarg) || "const".equalsIgnoreCase(optarg) || "branch".equalsIgnoreCase(optarg) || "b".equalsIgnoreCase(optarg)) {
						optarg = ConstituentSelectMethod.class.getName();
					}
					addProperty(AblProperties.SELECT_METHOD, optarg);
					break;
				default:
					logger.error("Unrecognized parameter: " + opt);
				}
			}
		}
	}

	private void addProperty(String key, String value) {
		Object oldKey = properties.get().put(key, value);
		if(oldKey != null) {
			logger.debug("Overriding option " + key + " (old: " + oldKey + ", new: " + value);
		}
	}

	public PropertiesMap getProperties() {
		return properties.get();
	}

	public Object getNewClassInstance(String identifier) throws Exception {
		return properties.get().getNewClassInstance(identifier);
	}

	
}
