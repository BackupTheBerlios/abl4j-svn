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
package org.schwiebert.abl4j.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;
import java.util.Properties;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;

/**
 * Used to serialize an    {@link ITreeBank}   . The output format can be defined by using the properties    {@link AblProperties#OUTPUT_FILE}    and    {@link AblProperties#OUTPUT_ENCODING}   . This implementations requires an    {@link ISerializationVisitor}    which can be defined via    {@link AblProperties#SERIALISATION_VISITOR}    and which probably simplifies the serialization of a treebank.
 * @author    sschwieb
 * @uml.dependency   supplier="org.schwiebert.abl4j.io.ISerializationVisitor"
 */
public class TreebankWriter implements ITreebankWriter {

	private Writer writer;
	private boolean doClose = true;

	/**
	 * @uml.property  name="properties"
	 * @uml.associationEnd  
	 */
	private PropertiesMap properties;
	
	/**
	 * Configures the output by setting output file and encoding.
	 */
	public void configure(PropertiesMap pm) throws InvalidConfigurationException {
		String outputFile = pm.getProperty(AblProperties.OUTPUT_FILE);
		String encoding = pm.getProperty(AblProperties.OUTPUT_ENCODING);
		try {
			if(outputFile == null) {
				OutputStream out = (OutputStream) pm.get(AblProperties.OUTPUT_STREAM);
				if(out != null) {
					writer = new OutputStreamWriter(out, encoding);					
				} else {
					writer = new OutputStreamWriter(System.out, encoding);
					doClose = false;
				}
			} else {
				writer = new OutputStreamWriter(new FileOutputStream(outputFile), encoding);
			}
			this.properties = pm;
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}
	
	/**
	 * Uses an {@link ISerializationVisitor} to store the treebank.
	 */
	@SuppressWarnings("unchecked")
	public void writeTreebank(ITreeBank tb) throws IOException{
		ISerializationVisitor visitor = null;
		String className = properties.getProperty(AblProperties.SERIALISATION_VISITOR);
		try {
			Class visitorClass = Thread.currentThread().getContextClassLoader().loadClass(className);
			visitor = (ISerializationVisitor) visitorClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		visitor.initialize(writer, properties);
		visitor.visitTreeBank(tb);
		final int size = tb.size();
		for(int j = 0; j < size; j++) {
			ITree<?> tree = tb.get(j);
			visitor.visitSentence(tree);
			for(int i = 0; i < tree.size(); i++) {
				visitor.visitWord(tree.get(i), i < tree.size()-1);
			}
			visitor.visitTree(tree, true);
			for(IConstituent<?> constituent: tree.getConstituentStructure()) {
				visitor.visitConstituent(constituent, true);
				for(int i = 0; i < constituent.size(); i++) {
					visitor.visitNonTerminal(constituent.get(i), i < constituent.size()-1);
				}
				visitor.visitConstituent(constituent, false);
			}
			visitor.visitTree(tree, false);
		}
		visitor.close();
		if(doClose) {
			writer.close();
		}
		Logger.getLogger(TreebankReader.class).info("Treebank with " + tb.size() + " trees written");
	}

	/**
	 * @uml.property  name="iSerializationVisitor"
	 * @uml.associationEnd  inverse="treebankWriter:org.schwiebert.abl4j.io.ISerializationVisitor"
	 * @uml.association  name="uses"
	 */
	private ISerializationVisitor serializationVisitor;

	/**
	 * Getter of the property <tt>iSerializationVisitor</tt>
	 * @return  Returns the serializationVisitor.
	 * @uml.property  name="iSerializationVisitor"
	 */
	public ISerializationVisitor getISerializationVisitor() {
		return serializationVisitor;
	}

	/**
	 * Setter of the property <tt>iSerializationVisitor</tt>
	 * @param iSerializationVisitor  The serializationVisitor to set.
	 * @uml.property  name="iSerializationVisitor"
	 */
	public void setISerializationVisitor(ISerializationVisitor serializationVisitor) {
		this.serializationVisitor = serializationVisitor;
	}

	/**
	 * @uml.property  name="iSerializationVisitor1"
	 * @uml.associationEnd  inverse="treebankWriter1:org.schwiebert.abl4j.io.ISerializationVisitor"
	 * @uml.association  name="uses"
	 */
	private ISerializationVisitor serializationVisitor1;

	/**
	 * Getter of the property <tt>iSerializationVisitor1</tt>
	 * @return  Returns the serializationVisitor1.
	 * @uml.property  name="iSerializationVisitor1"
	 */
	public ISerializationVisitor getISerializationVisitor1() {
		return serializationVisitor1;
	}

	/**
	 * Setter of the property <tt>iSerializationVisitor1</tt>
	 * @param iSerializationVisitor1  The serializationVisitor1 to set.
	 * @uml.property  name="iSerializationVisitor1"
	 */
	public void setISerializationVisitor1(ISerializationVisitor serializationVisitor1) {
		this.serializationVisitor1 = serializationVisitor1;
	}

}
