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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;


/**
 * This class is used to store a hypothesis or a nonterminal. A hypothesis is
 * defined by a begin and end index (in a {@link ISentence}) and one or more
 * {@link NonTerminal}s.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 */
public class Constituent<T> extends ArrayList<NonTerminal> implements IConstituent<T> {

	private static final long serialVersionUID = -2954053640070134123L;

	// data members
	/**
	 * The boundaries of the constituent.
	 */
	private int begin;

	private int end;

	/**
	 * ABL4J extension: If an alignment or selection algorithm supports
	 * local alignments, the probability of the alignment is stored in 
	 * this map. <br>
	 * <strong>TODO:</strong>
	 * Currently, only one probability per sentence can be
	 * stored - if a constituent can be aligned twice against a sentence,
	 * only the last probability will be stored.
	 */
	private Map<Integer, Double> sentenceToScore = null;

	/**
	 * The sentence this constituent belongs to.
	 */
	private ISentence<T> sentence;
	
	/**
	 * Constructs a new Constituent.
	 */
	public Constituent() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#init(org.schwiebert.abl4j.data.ISentence, int, int)
	 */
	public void init(ISentence<T> sentence, int begin, int end) {
		this.sentence = sentence;
		this.begin = begin;
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getBegin()
	 */
	public int getBeginIndex() {
		return begin;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getEnd()
	 */
	public int getEndIndex() {
		return end;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#empty()
	 */
	public boolean empty() {
		return begin == end;
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o instanceof IConstituent) {
			IConstituent<T> c = (IConstituent<T>) o;
			boolean result = ((c.getBeginIndex() == begin) && (c.getEndIndex() == end));
			return result;
		}
		return false;
	}
	
	

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#hashCode()
	 */
	@Override
	/**
	 * TODO: Check - is this correct? Constituents have the same hashcode
	 * even if they are in different sequences... sequenceId should be
	 * contained!
	 */
	public int hashCode() {
		String s = begin + "_" + end;
		return s.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#valid()
	 */
	public boolean valid() {
		return begin <= end;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#mergeNonterminals(org.schwiebert.abl4j.data.NonTerminal)
	 */
	public void mergeNonterminals(NonTerminal n) {
		clear();
		add(n);
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#setLocalScore(int, double)
	 */
	public void setLocalScore(int sentenceId, double score) {
		if(sentenceToScore == null) {
			sentenceToScore = new HashMap<Integer, Double>();
		}
		if (this.sentenceToScore.containsKey(sentenceId) && sentenceToScore.get(sentenceId) != score) {
			//logger.warn("Overriding probability " + this.sentenceToProbability.get(sentenceId) + " with " + probability);
		}
		this.sentenceToScore.put(sentenceId, score);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getLocalScoreMap()
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> getLocalScoreMap() {
		if(sentenceToScore == null) return Collections.EMPTY_MAP;
		return sentenceToScore;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getAverageLocalScore()
	 */
	public double getAverageLocalScore() {
		if(sentenceToScore == null) return 1;
		double toReturn = 0;
		for (Double d : sentenceToScore.values()) {
			toReturn += d;
		}
		return toReturn / sentenceToScore.size();
	}

	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getHighestLocalScore()
	 */
	public double getHighestLocalScore() {
		if(sentenceToScore == null) return 1;
		double toReturn = 0;
		for (Double d : sentenceToScore.values()) {
			if(d > toReturn) {
				toReturn = d;
			}
		}
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getWords()
	 */
	public List<IWord<T>> getWords() {
		return sentence.subList(begin, end);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getSentence()
	 */
	public ISentence<T> getSentence() {
		return sentence;
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#indexOf(org.schwiebert.abl4j.data.NonTerminal)
	 */
	public int indexOf(NonTerminal n) {
		return super.indexOf(n);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#addAllNonTerminals(java.util.Collection)
	 */
	public boolean addAllNonTerminals(Collection<NonTerminal> nonTerminals) {
		return super.addAll(nonTerminals);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#containsNonTerminal(org.schwiebert.abl4j.data.NonTerminal)
	 */
	public boolean containsNonTerminal(NonTerminal nonTerminal) {
		return super.contains(nonTerminal);
	}

	
}
