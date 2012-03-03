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

package org.schwiebert.abl4j.data.impl.abl;

import java.io.Serializable;
import java.util.List;

import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.util.WordMapping;

/**
 * 
 * 
 * This class is used to store a "word". 
 * In Abl4J, a word can be any java object. <strong>Note:</strong> Internally, ABL uses
 * {@link Word#index} to determine the equality of two words. The index is generated 
 * with the help of {@link WordMapping} and requires that {@link Object#equals(Object)} 
 * and {@link Object#hashCode()} are overwritten properly.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
@SuppressWarnings("unchecked")
public class Word<T> implements Serializable, IWord<T> {

	private static final long serialVersionUID = 5795945638213012406L;

	public static final int UNDEF = -1;
		
	// storage
	/**
	 * index of current word
	 */
	private int index;
	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#hashCode()
	 */
	@Override
	public int hashCode() {
		return index;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			return ((Word) obj).index == index;
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	public Word() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#init(int)
	 */
	public void init(int index) {
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#getWord()
	 */
	public T getWord() {
		return (T) DataFactory.getMapping().getWord(index);
		//return (T) DataFactory.getMapping().indexToWord.get(index);
	}

	/**
	 * This method registers the use of the current word in a sentence
	 * specified with parameter "sentenceId".
	 */
	public void addSentenceToWord(int sentenceId) {
		DataFactory.getMapping().addIndexToSentence(index, sentenceId);
	}

//	// Definitions supporting the iteration over the registered sentences
//	// for the current word.

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#getSentenceOccurrences()
	 */
	public List<Integer> getSentenceOccurrences() {
		return (List<Integer>) DataFactory.getMapping().getSentencesOfIndex(index);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IWord#toString()
	 */
	@Override
	public String toString() {
		return "W[" + index + "]";
	}
	
	


}
