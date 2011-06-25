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

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * A helper class which provides word mappings. To map {@link String} objects, 
 * the default implementation {@link StringMapping} can be used. 
 * @author sschwieb
 *
 */
public class WordMapping<T> implements Serializable, IWordMapping<T> {

	private static final long serialVersionUID = 5399361222040499324L;

	/**
	 * mapping word string -> word index
	 */
	private final Map<T, Integer> wordToIndex;

	/**
	 * mapping word index -> word string
	 */
	private final Map<Integer, T> indexToWord = new HashMap<Integer, T>();

	/**
	 * mapping word index -> sentence index
	 */
	private final Map<Integer, Vector<Integer>> indexToSentence = new HashMap<Integer, Vector<Integer>>();
	
	public WordMapping(Comparator<T> comparator) {
		// TODO: Why TreeMap?
		wordToIndex = new HashMap<T, Integer>();
//		wordToIndex = new TreeMap<T, Integer>(comparator);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.util.IWordMapping#addIndexToSentence(int, int)
	 */
	public synchronized void addIndexToSentence(int index, int sentenceId) {
		Vector<Integer> v = (Vector<Integer>) indexToSentence.get(index);
		if (v == null) {
			v = new Vector<Integer>();
			indexToSentence.put((int) index, v);
		}
		((Vector<Integer>) indexToSentence.get(index)).add(sentenceId);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.util.IWordMapping#getSentencesOfIndex(int)
	 */
	public Vector<Integer> getSentencesOfIndex(int index) {
		return indexToSentence.get((int)index);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.util.IWordMapping#getWord(int)
	 */
	public T getWord(int index) {
		return indexToWord.get((int)index);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.util.IWordMapping#getWordIndexByObject(T)
	 */
	public synchronized int getWordIndexByObject(T data) {
		int index = -1;
		if (!wordToIndex.containsKey(data)) {
			index = wordToIndex.size() + 1;
			wordToIndex.put(data, index);
			indexToWord.put(index, data);
		} else {
			index = (Integer) wordToIndex.get(data);
		}
		return index;
	}

}
