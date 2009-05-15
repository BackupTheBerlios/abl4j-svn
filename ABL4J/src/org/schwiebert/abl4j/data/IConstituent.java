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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.schwiebert.abl4j.data.impl.abl.Constituent;

/**
 * An IConstituent is used to store a hypothesis or a nonterminal. A hypothesis is
 * defined by a begin and end index (in a {@link ISentence}) and one or more
 * {@link NonTerminal}s. Default implementation: {@link Constituent}.
 * 
 * @author sschwieb
 */
public interface IConstituent<T> {

	/**
	 * Returns the begin index
	 */
	public abstract int getBeginIndex();

	/**
	 * Returns the end index
	 */
	public abstract int getEndIndex();

	/**
	 * Is the constituent empty (i.e. begin==end)?
	 */
	public abstract boolean empty();

	/**
	 * Two {@link IConstituent} are equal, if their boundaries are
	 * equal (<code>this.boundaries.first == o.boundaries.first && this.boundaries.second == o.boundaries.second</code>).
	 */
	public abstract boolean equals(Object o);

	public abstract int hashCode();

	/**
	 * Is the constituent valid (i.e. begin<=end)?
	 */
	public abstract boolean valid();

	/**
	 * Sets the nonterminal of the hypothesis to n
	 */
	public abstract void mergeNonterminals(NonTerminal n);

	/**
	 * Sets the local score of a constituent. The score is local to the sentence
	 * defined by sentenceId, and must be a value between 0 and 1.
	 * @param sentenceId
	 * @param score
	 */
	public abstract void setLocalScore(int sentenceId, double score);

	/**
	 * Returns the map of local scores, or {@link Collections#EMPTY_MAP},
	 * if no probabilities have been set.<br>
	 * <strong>Note:</strong>Only a few algorithms support local scores.
	 * @return
	 */
	public abstract Map<Integer, Double> getLocalScoreMap();

	/**
	 * Returns the average local score of this hypothesis. Experimental...
	 * @return
	 */
	public abstract double getAverageLocalScore();

	/**
	 * Returns the highest local score of this hypothesis. Experimental...
	 * @return
	 */
	public abstract double getHighestLocalScore();

	/**
	 * Returns the list of words the constituent contains.
	 * @return
	 */
	public abstract List<IWord<T>> getWords();

	/**
	 * Returns the sentence to which this constituent belongs to.
	 * @return
	 */
	public abstract ISentence<T> getSentence();

	/**
	 * Returns the NonTerminal at Position index.
	 * @param index
	 * @return
	 */
	public abstract NonTerminal get(int index);

	/**
	 * Returns the number of NonTerminals
	 * @return
	 */
	public abstract int size();

	/**
	 * Returns the index of NonTerminal n.
	 * @param n
	 * @return
	 */
	public abstract int indexOf(NonTerminal n);

	/**
	 * Adds a NonTerminal to the List of NonTerminals.
	 * @param n
	 * @return
	 */
	public abstract boolean add(NonTerminal n);

	/**
	 * Clears the List of NonTerminals
	 */
	public abstract void clear();

	/**
	 * Adds all NonTerminals.
	 * @param nonTerminals
	 * @return
	 */
	public abstract boolean addAllNonTerminals(Collection<NonTerminal> nonTerminals);

	/**
	 * Returns true if the given NonTerminal is already contained in the List of NonTerminals.
	 * @param nonTerminal
	 * @return
	 */
	public abstract boolean containsNonTerminal(NonTerminal nonTerminal);

	/**
	 * Initializes the NonTerminal. Should only be called from {@link DataFactory}.
	 * @param sentence
	 * @param begin
	 * @param end
	 */
	abstract void init(ISentence<T> sentence, int begin, int end);

}