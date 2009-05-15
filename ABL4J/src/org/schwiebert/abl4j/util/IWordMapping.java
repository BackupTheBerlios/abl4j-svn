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

import java.util.Comparator;
import java.util.Vector;

/**
 * Different implementations of {@link IWordMapping} allow users to align
 * different kind of data. The default word mapping used in ABL4J, {@link WordMapping}, 
 * is used to map any Object to an int value, so you'll probably only have to provide
 * {@link WordMapping} with a custom {@link Comparator} to make ABL4J aware of a new
 * class.
 * 
 * @author sschwieb
 *
 * @param <T>
 */
public interface IWordMapping<T> {

	/**
	 * This method is called to register the use of the word <code>wordId</code> in sentence <code>sentenceId</code>.
	 */
	public abstract void addIndexToSentence(int wordId, int sentenceId);

	/**
	 * Returns a list of all sequences which contain the word with id <code>wordId</code>.
	 * @param wordId
	 * @return
	 */
	public abstract Vector<Integer> getSentencesOfIndex(int wordId);

	/**
	 * Returns the word with id <code>wordId</code>
	 * @param wordId
	 * @return
	 */
	public abstract T getWord(int wordId);

	/**
	 * Returns the word id of <code>data</code>
	 * @param data
	 * @return
	 */
	public abstract int getWordIndexByObject(T data);

}