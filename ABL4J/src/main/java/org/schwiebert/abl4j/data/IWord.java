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

import java.util.List;

import org.schwiebert.abl4j.data.impl.abl.Word;
import org.schwiebert.abl4j.util.WordMapping;

/**
 * This class is used to store a "word". 
 * In Abl4J, a word can be any java object. <strong>Note:</strong> Internally, ABL uses
 * {@link Word#index} to determine the equality of two words. The index is generated 
 * with the help of {@link WordMapping} and requires that {@link Object#equals(Object)} 
 * and {@link Object#hashCode()} are overwritten properly.
 * Default implementation is {@link Word}.
 * 
 * @author sschwieb
 * 
 */
public interface IWord<T> {

	/**
	 * Returns the hashcode of <code>index</code>, which is,
	 * as in {@link Integer}, the value itself.
	 */
	public abstract int hashCode();

	/**
	 * Two words are equal, if their indices are equal.
	 */
	public abstract boolean equals(Object obj);

	/**
	 * Returns the index of the word.
	 * @return
	 */
	public abstract int getIndex();

	/**
	 * Returns the underlying string of the word.
	 * @return
	 */
	public abstract T getWord();

	/**
	 * Returns all sentences which contain this word.
	 */
	public abstract List<Integer> getSentenceOccurrences();

	/**
	 * Returns a {@link String}-representation of this word.
	 * @return
	 */
	public abstract String toString();

	/**
	 * Adds the sequence with id sequenceId to this word.
	 * @param sequenceid the id of the sequence
	 */
	public abstract void addSentenceToWord(int sequenceid);

	/**
	 * Initialize this word with the given index. That is, the
	 * unique represenation of the <strong>type</strong> of this {@link IWord} 
	 * is mapped to index.
	 * @param index the unique identifier of this {@link IWord}
	 */
	public abstract void init(int index);

}