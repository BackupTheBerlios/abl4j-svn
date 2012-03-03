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
package org.schwiebert.abl4j.data;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.util.ABLInitializer;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.IWordMapping;
import org.schwiebert.abl4j.util.PropertiesMap;
import org.schwiebert.abl4j.util.WordMapping;

/**
 * The DataFactory defines which implementations of {@link IWord},
 * {@link ISentence} etc are used - see
 * {@link DataFactory#initialize(PropertiesMap)} for details.
 * 
 * @author sschwieb
 * @see AblProperties
 * @see ABLInitializer
 * 
 */
@SuppressWarnings("unchecked")
public class DataFactory {

	private static Logger logger = Logger.getLogger(DataFactory.class);

	private static InheritableThreadLocal<IWordMapping> threadLocalMapping = new InheritableThreadLocal<IWordMapping>();

	private static InheritableThreadLocal<Class> constituentClass = new InheritableThreadLocal<Class>();
	private static InheritableThreadLocal<Class> sentenceClass = new InheritableThreadLocal<Class>();
	private static InheritableThreadLocal<Class> wordClass = new InheritableThreadLocal<Class>();
	private static InheritableThreadLocal<Class> treeClass = new InheritableThreadLocal<Class>();
	private static InheritableThreadLocal<Class> treeBank = new InheritableThreadLocal<Class>();

	/**
	 * Initializes the DataFactory with the given {@link PropertiesMap}. The
	 * classes that will be used within ABL4J (implementations of
	 * {@link IConstituent}, {@link ISentence}, {@link IWord}, {@link ITree} and
	 * {@link ITreeBank}) are loaded here. These classes can be set by modifying
	 * the Properties {@link AblProperties#CONSTITUENT_CLASS},
	 * {@link AblProperties#SENTENCE_CLASS}, {@link AblProperties#WORD_CLASS},
	 * {@link AblProperties#TREE_CLASS} and {@link AblProperties#TREEBANK_CLASS}
	 * . The ABL4J-Implementation (found in org.schwiebert.abl4j.data) will be
	 * used by default.
	 * 
	 * @param propertiesMap
	 *            the properties map
	 * @throws ClassNotFoundException
	 *             if one of the data classes cannot be loaded
	 */
	public static void initialize(PropertiesMap propertiesMap) throws ClassNotFoundException {
		constituentClass.set(propertiesMap.getClass(AblProperties.CONSTITUENT_CLASS));
		logger.info("Using constituent " + constituentClass.get());
		sentenceClass.set(propertiesMap.getClass(AblProperties.SENTENCE_CLASS));
		logger.info("Using sentence " + sentenceClass.get());
		wordClass.set(propertiesMap.getClass(AblProperties.WORD_CLASS));
		logger.info("Using word " + wordClass.get());
		treeClass.set(propertiesMap.getClass(AblProperties.TREE_CLASS));
		logger.info("Using tree " + treeClass.get());
		treeBank.set(propertiesMap.getClass(AblProperties.TREEBANK_CLASS));
		logger.info("Using treebank " + treeBank.get());
	}

	public static IWordMapping getMapping() {
		return threadLocalMapping.get();
	}

	/**
	 * This procedure associates to each unique space and newline delimited
	 * input string, a word, a unique number for internal representation and
	 * stores this in the Word class.
	 */
	public static IWord getWord(Object data) {
		int index = -1;
		IWordMapping mapping = threadLocalMapping.get();
		index = (int) mapping.getWordIndexByObject(data);
		return newWord(index);
	}

	public static IConstituent newConstituent() {
		try {
			IConstituent instance = (IConstituent) constituentClass.get().newInstance();
			return instance;
		} catch (Exception e) {
			logger.error("Failed to instantiate constituent class", e);
			throw new RuntimeException();
		}
	}

	public static IConstituent newConstituent(ISentence sentence, int begin, int end) {
		IConstituent constituent = newConstituent();
		constituent.init(sentence, begin, end);
		return constituent;
	}

	public static ITree newTree() {
		try {
			ITree instance = (ITree) treeClass.get().newInstance();
			return instance;
		} catch (Exception e) {
			logger.error("Failed to instantiate tree class", e);
			throw new RuntimeException();
		}
	}

	public static ISentence newSentence() {
		try {
			ISentence instance = (ISentence) sentenceClass.get().newInstance();
			return instance;
		} catch (Exception e) {
			logger.error("Failed to instantiate sentence class", e);
			throw new RuntimeException();
		}
	}

	/**
	 * Creates a new word which will be registered within a {@link WordMapping}.
	 * 
	 * @param index
	 * @return
	 */
	public static IWord newWord(int index) {
		try {
			IWord instance = (IWord) wordClass.get().newInstance();
			instance.init(index);
			return instance;
		} catch (Exception e) {
			logger.error("Failed to instantiate word class", e);
			throw new RuntimeException();
		}
	}

	public static ITreeBank newTreeBank() {
		try {
			ITreeBank instance = (ITreeBank) treeBank.get().newInstance();
			return instance;
		} catch (Exception e) {
			logger.error("Failed to instantiate word class", e);
			throw new RuntimeException();
		}
	}

	public static void setMapping(IWordMapping wordMapping) {
		threadLocalMapping.set(wordMapping);
	}

}
