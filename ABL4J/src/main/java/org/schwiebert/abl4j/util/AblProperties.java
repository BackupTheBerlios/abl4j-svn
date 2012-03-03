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

import org.schwiebert.abl4j.align.AlignType;
import org.schwiebert.abl4j.align.PartType;
import org.schwiebert.abl4j.align.methods.AllAlignmentsMethod;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.impl.abl.Constituent;
import org.schwiebert.abl4j.data.impl.abl.Sentence;
import org.schwiebert.abl4j.data.impl.abl.Tree;
import org.schwiebert.abl4j.data.impl.abl.TreeBank;
import org.schwiebert.abl4j.data.impl.abl.Word;
import org.schwiebert.abl4j.io.ITreebankReader;
import org.schwiebert.abl4j.io.ISerializationVisitor;
import org.schwiebert.abl4j.io.ITreebankWriter;
import org.schwiebert.abl4j.io.PlainTextSerializationVisitor;

/**
 * Abl4J is configured by modifying properties stored in
 * {@link PropertiesMap} during the initialization within
 * {@link ABLInitializer}. This class lists all property
 * names used within Abl4J.
 * 
 * @author sschwieb
 * @see ABLInitializer
 *
 */
public class AblProperties {
	
	private static final String PREFIX = "org.schwiebert.abl4j.";
	
	/**
	 * The path to the input file. If this property is not set,
	 * System.in is used as input source.
	 */
	public static final String INPUT_FILE = PREFIX + "input.file";
	
	/**
	 * The path to the output file. If this property is not set,
	 * System.out is used as output destination.
	 */
	public static final String OUTPUT_FILE = PREFIX + "output.file";
		
	/**
	 * Chosen alignment (defaults to {@link AlignType#AA}).
	 */
	public static final String ALIGN_TYPE = PREFIX + "align.method";
	
	/**
	 * Chosen part of the sentences that should be hypotheses. (default to
	 * {@link PartType#UNEQUAL})
	 */
	public static final String PART_TYPE = PREFIX + "part.type";
	
	/**
	 * Exclude empty hypotheses (where B == E). Default is false.
	 */
	public static final String EXCLUDE_EMPTY = PREFIX + "align.exclude.empty";
	
	/**
	 * Nomerge flag. Default is false.
	 */
	public static final String NO_MERGE = PREFIX + "align.nomerge";
	
	/**
	 * If true, the algorithms are forced to produce exactly the same
	 * output as the original C++ version of ABL. Default is false.
	 */
	public static final String COMPATIBILITY_MODE = PREFIX + "compatibility.mode";
	
	/**
	 * All possible selection methods. Descriptions taken from ABL Reference
	 * Guide, version 1.0 alpha, p. 16
	 *
	 * <strong>FIRST</strong>
	 * A non-probablistic method that builds upon the assumption that a
	 * hypothesis that is learned earlier is always correct. This means that
	 * newly learned hypotheses that overlap with older ones are considered
	 * to be incorrect, and thus should be removed.
	 *	<p/>
	 * <strong>TERMS</strong>
	 * The method leaf (or terms) computes the probability of a hypothesis
	 * by counting the number of times the particular words of the
	 * hypothesis have occurred in the learned text as a hypothesis, divided
	 * by the total number of hypotheses.
	 *	<p/>
	 *	<strong>CONST</strong>
	 * In addition to only consider the word of the sentence delimited by
	 * the hypothesis as in the method leaf, this model computes the
	 * probability based on the words of the hypothesis and its type label.
	 * 
	 * Default is CONST.
	 */
	public static final String SELECT_METHOD = PREFIX + "select.type";
	
	/**
	 * The name of a class that implements {@link ITreebankReader}.
	 */
	public static final String READER_CLASS = PREFIX + "reader";
	
	/**
	 * The name of a class that implements {@link ITreebankWriter}.
	 */
	public static final String WRITER_CLASS = PREFIX + "writer";

	/**
	 * Random seed. Default is 0. Don't confuse with 
	 * <a href="http://de.wikipedia.org/wiki/Sozialistische_Einheitspartei_Deutschlands">SED</a> or 
	 * <a href="http://www.seeed.info/">SEEED</a>.
	 */
	public static final String SEED = PREFIX + "seed";

	/**
	 * Print aligning information. Default is false.
	 */
	public static final String VERBOSE = PREFIX + "verbose";

	/**
	 * Do exhaustive comparisons: N(N-1)/2. Default is false.
	 */
	public static final String EXHAUSTIVE = PREFIX + "align.exhaustive";

	/**
	 * The serialization visitor used when serializing a treebank.
	 * Default is {@link PlainTextSerializationVisitor}.
	 * @see implementations of {@link ISerializationVisitor}
	 */
	public static final String SERIALISATION_VISITOR = PREFIX + "serialization.visitor";

	/**
	 * Run Select in preserve mode. Default is false.
	 */
	public static final String PRESERVE = PREFIX + "select.preserve";

	/**
	 * The class that implements ClusterMethod
	 * TODO: No default!
	 */
	public static final String CLUSTER_METHOD = PREFIX + "cluster.method";
	
	/**
	 * The class that implements MergeMethod
	 * TODO: No default!
	 */
	public static final String MERGE_METHOD = PREFIX + "merge.method";

	/**
	 * The encoding of the input stream. Default is "UTF-8".
	 */
	public static final String INPUT_ENCODING = PREFIX + "input.encoding";
	
	/**
	 * The encoding of the output stream. Default is "UTF-8".
	 */
	public static final String OUTPUT_ENCODING = PREFIX + "output.encoding";

	/**
	 * If true, annotations found in the input are ignored (helpful if comparing
	 * results). Default is false.
	 */
	public static final String INPUT_IGNORE_ANNOTATIONS = PREFIX + "input.ignore.annotations";

	/**
	 * The number of threads used in parallel parts of ABL4J. Default is 1.
	 */
	public static final String THREADS = PREFIX + "threads";

	/**
	 * {@link AllAlignmentsMethod}-Flag: If true, LinkLists will be sorted before they are processed.
	 * The sorting should be lexicographically, resulting in a processing order
	 * similar to c++ order.
	 * This is required only to compare this algorithm with c++ version,
	 * therefore, it can be disabled...
	 */
	public static final String COMPARISM_MODE = PREFIX + "comparism.mode";
	
	/**
	 * A class that implements {@link IConstituent}.
	 * Default is {@link Constituent}.
	 */
	public static final String CONSTITUENT_CLASS = PREFIX + "constituent";
	
	/**
	 * A class that implements {@link ISentence}. 
	 * Default is {@link Sentence}.
	 */
	public static final String SENTENCE_CLASS = PREFIX + "sentence";
	
	/**
	 * A class that implements {@link ITreeBank}.
	 * Default is {@link TreeBank}.
	 */
	public static final String TREEBANK_CLASS = PREFIX + "treebank";
	
	/**
	 * A class that implements {@link IWord}.
	 * Default is {@link Word}.
	 */
	public static final String WORD_CLASS = PREFIX + "word";

	/**
	 * A class that implements {@link ITree}.
	 * Default is {@link Tree}.
	 */
	public static final String TREE_CLASS = PREFIX + "tree";

	public static final String WORD_MAPPING = PREFIX + "word.mapping";

	public static final String RESET_UPPER_NT = PREFIX + "reset.upper.nt";

	public static final String INPUT_STREAM = "input.stream";

	public static final String OUTPUT_STREAM = "output.stream";
	

}
