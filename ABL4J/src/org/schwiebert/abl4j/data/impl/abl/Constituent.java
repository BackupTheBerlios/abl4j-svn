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
import org.schwiebert.abl4j.util.AblProperties;


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
public class Constituent<T> implements IConstituent<T>, Serializable {

	private static final long serialVersionUID = -2954053640070134123L;
	
	//private final Set<NonTerminal> nts = new GoogleHashSet<NonTerminal>();
	
	private final List<NonTerminal> nts = new ArrayList<NonTerminal>(4);

	// data members
	/**
	 * The boundaries of the constituent.
	 */
	private short begin;

	private short end;

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
		this.begin = (short) begin;
		this.end = (short) end;
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
			if(o == this) return true;
			Constituent<T> c = (Constituent<T>) o;
			return ((c.begin == begin) && (c.end == end) && c.sentence.getSequenceId() == sentence.getSequenceId());
		}
		return false;
	}
	
	

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((begin << 13) *31+end)*31+sentence.getSequenceId();
		
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
			sentenceToScore = new HashMap<Integer, Double>(2);
		}
		if (this.sentenceToScore.containsKey(sentenceId) && sentenceToScore.get(sentenceId) != score) {
			//logger.warn("Overriding probability " + this.sentenceToProbability.get(sentenceId) + " with " + probability);
		}
		this.sentenceToScore.put(sentenceId, score);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.IConstituent#getLocalScoreMap()
	 */
	public Map<Integer, Double> getLocalScoreMap() {
		if(sentenceToScore == null) return Collections.emptyMap();
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
	public IWord<T>[] getWords() {
		return sentence.subArray(begin, end);
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
	public boolean contains(NonTerminal n) {
		return Collections.binarySearch(nts, n) > 0;
		//return nts.contains(n);
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.schwiebert.abl4j.data.IConstituent#addAllNonTerminals(java.util.Collection)
//	 */
//	public boolean addAllNonTerminals(Collection<NonTerminal> nonTerminals) {
//		return nts.addAll(nonTerminals);
//	}

	public int size() {
		return nts.size();
	}

	public boolean add(NonTerminal n) {
		// Inefficient, but required for compatibility with WF
		if(nts.indexOf(n) == -1) {
			nts.add(n);
			return true;
		} 
		return false;
//		int position = Collections.binarySearch(nts, n);			
//		if(position >= 0) return false;
//		nts.add(n);
//		nts.add(-position-1, n);
//		return true;
	}

	public void clear() {
		nts.clear();
	}

	public NonTerminal getFirst() {
		return nts.get(0);
		// FIXME: This does not return the first, but any!
	//	return nts.iterator().next();
	}

	public Collection<NonTerminal> getNonTerminals() {
		return nts;
	}

	
}
