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

import org.schwiebert.abl4j.data.impl.abl.Sentence;

/**
 * An ISentence is used to store a sequence of {@link IWord}s.
 * Default implementation: {@link Sentence}.
 * 
 * @author sschwieb
 * 
 */
public interface ISentence<T> {

	/**
	 * Sets the commentLine
	 * @param commentLine
	 */
	public abstract void setCommentLine(String commentLine);

	/**
	 * Returns the commentLine
	 * @return
	 */
	public abstract String getCommentLine();

	/**
	 * This procedure sets the sentence ID
	 */
	public abstract void setSequenceId(int id);

	/**
	 * This procedure returns the sentence ID
	 */
	public abstract int getSequenceId();

	/**
	 * This procedure considers each word in the sentence and registers its
	 * appearance in the current sentence to facilitate future decisions on
	 * which sentences to compare with. It should be called while reading in the
	 * full treebank line by line.
	 */
	public abstract void registerWordsInSentence();

	/**
	 * This procedure considers each word in the sentence and fills the set
	 * "similars" with sentences to compare with as these sentences have at
	 * least one word in common.
	 */
	public abstract void buildSimilarSentencesSet();

	/**
	 * This procedure returns true if the current sentence has in its "similars"
	 * set the sentence specified with argument "id", and returns false if it
	 * has not, or if the map of similar sentences has not been created (Because
	 * {@link #buildSimilarSentencesSet()} has not been called).
	 * 
	 * 
	 */
	public abstract boolean isSimilarTo(int id);

	/**
	 * Returns a string representation of this sequence.
	 * @return
	 */
	public abstract String getSequenceText();

	/**
	 * Searches for the first occurence of the given argument, beginning 
	 * the search at <code>index</code>, and testing for equality using 
	 * the <code>equals</code> method. 
	 *
	 * @param   elem    an object.
	 * @param   index   the non-negative index to start searching from.
	 * @return  the index of the first occurrence of the object argument in
	 *          this sequence at position <code>index</code> or later in the
	 *          sequence, that is, the smallest value <tt>k</tt> such that 
	 *          <tt>elem.equals(elementData[k]) && (k &gt;= index)</tt> is 
	 *          <tt>true</tt>; returns <code>-1</code> if the object is not 
	 *          found. (Returns <code>-1</code> if <tt>index</tt> &gt;= the
	 *          current size of this <tt>sequence</tt>.)
	 * @exception  IndexOutOfBoundsException  if <tt>index</tt> is negative.
	 * @see     Object#equals(Object)
	 * @see 	List#indexOf(Object)
	 */
	public abstract int indexOf(IWord<T> elem, int index);

	/**
	 * Returns the subList of {@link IWord} from <code>begin</code> to
	 * <code>end</code>, as defined in {@link List#subList(int, int)}.
	 * @param begin the begin index (included)
	 * @param end the end index (not included)
	 * @return the sublist
	 * @see List#subList(int, int)
	 */
	public abstract List<IWord<T>> subList(int begin, int end);
	
	/**
	 * Returns the list of all {@link IWord}s in this sentence.
	 * @return the list of words
	 */
	public abstract List<IWord<T>> getWords();
	
	/**
	 * Returns the number of {@link IWord}s in this sentence.
	 * @return
	 */
	public abstract int size();

	/**
	 * Returns the {@link IWord} at the given index
	 * @param index the index of the word.
	 * @return the word at index
	 */
	public abstract IWord<T> get(int index);

	/**
	 * Adds all {@link IWord} stored in words to this sentence,
	 * as in {@link List#addAll(java.util.Collection)}.
	 * @param words
	 */
	public abstract void addWords(List<IWord<T>> words);


}