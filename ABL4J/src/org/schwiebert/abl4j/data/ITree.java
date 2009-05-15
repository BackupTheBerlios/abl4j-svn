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

import java.util.Iterator;
import java.util.List;

import org.schwiebert.abl4j.data.impl.abl.Tree;

/**
 * An ITree is an {@link ISentence} which additionally contains 
 * <code>{@link IConstituent}s</code>. 
 * Default implementation: {@link Tree}.
 *
 * @author sschwieb
 * 
 */
public interface ITree<T> extends ISentence<T> {

	/**
	 * Returns a copy of this object.
	 */
	public abstract ITree<T> cloneTree();

	/**
	 * Adds a constituent c to the tree. If a constituent with the same begin
	 * and end indices already exists, the nonterminals of c are added to that
	 * constituent, and <code>false</code> will be returned. 
	 * Otherwise, c is added itself and the method returns <code>true</code>.
	 */
	public abstract boolean addStructure(IConstituent<T> c);

	/**
	 * This procedure clears the entire tree (sentence and structure).
	 */
	public abstract void clear();

	/**
	 * Removes all words currently contained in this Tree, and
	 * adds all Word objects in <code>words</code>.
	 * @param words
	 */
	public abstract void changeSentence(List<IWord<T>> words);

	/**
	 * This procedure returns the number of hypotheses the tree contains
	 */
	public abstract int getNumberOfConstituents();

	/**
	 * This procedure returns an iterator to c in the tree.
	 */
	public abstract Iterator<IConstituent<T>> findStructure(IConstituent<T> c);

	/**
	 * Returns all Constituents stored in this Tree.
	 * @return
	 */
	public abstract List<IConstituent<T>> getConstituentStructure();

	public abstract ISentence<T> createSubStructure(int begin, int end);

	/**
	 * Sets the probability of the sentence to the given value.
	 * @param score
	 */
	public abstract void setSentenceScore(double score);

	/**
	 * Returns the probability of the sentence.
	 * @return
	 */
	public abstract double getSentenceScore();

	/**
	 * Adds {@link IWord} w to the list of words, as in
	 * {@link List#add(Object)}
	 * @param w
	 */
	public abstract void addWord(IWord<T> w);

	/**
	 * Adds the given list of words to the list of words already
	 * contained in the {@link ITree}, as in {@link List#addAll(java.util.Collection)}
	 * @param otherWords
	 */
	public abstract void addAllWords(List<IWord<T>> otherWords);
	


}