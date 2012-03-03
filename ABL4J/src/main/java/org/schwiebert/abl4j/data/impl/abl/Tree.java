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
import java.util.Iterator;
import java.util.List;

import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.FindIterator;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;

/**
 * A Tree is an {@link ISentence} which additionally contains a Vector of
 * <code>{@link IConstituent}s</code>.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class Tree<T> extends Sentence<T> implements ITree<T> {

	private static final long serialVersionUID = 8341177889686401359L;

	private List<IConstituent<T>> structure = new ArrayList<IConstituent<T>>();

	/**
	 * The probability of the sentence. Unless modified, the default value is
	 * 1.0d.
	 */
	private double score = 1.0d;
	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#cloneTree()
	 */
	public synchronized ITree<T> cloneTree() {
		Tree<T> copy = new Tree<T>();
		copy.setWords(words);
		copy.structure.addAll(structure);
		copy.score = score;
		return copy;
	}


	/**
	 * Creates a new, empty Tree.
	 */
	public Tree() {
		super();
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#addStructure(org.schwiebert.abl4j.data.IConstituent)
	 */
	public synchronized boolean addStructure(IConstituent<T> c) {
		int index = structure.indexOf(c);
		if (index == -1) {
			structure.add(c);
			return true;
		} else {
			for (NonTerminal n : c.getNonTerminals()) {
				final IConstituent<T> x = structure.get(index);
				if (!x.contains(n)) {
					x.add(n);
				}	
			}
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#clearTree()
	 */
	public void clearTree() {
		structure.clear();
		setCommentLine("");
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#changeSentence(java.util.List)
	 */
	public void changeSentence(IWord[] words) {
		setWords(words);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#getNumberOfConstituents()
	 */
	public int getNumberOfConstituents() {
		return structure.size();
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#findStructure(org.schwiebert.abl4j.data.IConstituent)
	 */
	public Iterator<IConstituent<T>> findStructure(IConstituent<T> c) {
		return new FindIterator<T>(c, structure);
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#getConstituentStructure()
	 */
	public List<IConstituent<T>> getConstituentStructure() {
		return structure;
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#createSubStructure(int, int)
	 */
	@SuppressWarnings("unchecked")
	public ISentence<T> createSubStructure(int begin, int end) {
		ISentence<T> tree = DataFactory.newSentence();
		IWord[] subList = this.subArray(begin, end);
		tree.setWords(subList);
		return tree;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#setSentenceScore(double)
	 */
	public void setSentenceScore(double score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITree#getSentenceScore()
	 */
	public double getSentenceScore() {
		return score;
	}

	
	

	
}
