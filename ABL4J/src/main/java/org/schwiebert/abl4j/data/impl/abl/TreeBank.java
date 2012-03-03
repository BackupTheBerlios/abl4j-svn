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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;

/**
 * A Treebank is a {@link List} of {@link ITree} objects.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class TreeBank<T> extends ArrayList<ITree<T>> implements ITreeBank<T> {

	private static final long serialVersionUID = -1447021341770660520L;

	private Vector<String> comments = new Vector<String>();
	
	private boolean exhaustive;

	// Will be used with suffixtree
	@SuppressWarnings("unused")
	private int current;

	
	
	//private int nterm;

	public TreeBank() {
		exhaustive = false;
		comments.clear();
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#setExhaustive(boolean)
	 */
	public void setExhaustive(boolean v) {
		exhaustive = v;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#isExhaustive()
	 */
	public boolean isExhaustive() {
		return exhaustive;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#doReverse()
	 */
	public void doReverse() {
		for (ITree<T> i : this) {
			IWord[] temp = i.getWordArray();
			List<IWord<T>> reverse = new ArrayList<IWord<T>>();
			for (int x = temp.length - 1; x >= 0; x--) {
				reverse.add(temp[x]);
			}
			IWord[] words = new IWord[reverse.size()];
			reverse.toArray(words);
			i.changeSentence(words);
		}
	}

	// Definitions supporting iteration over the treebank.
	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#getCurrentIndex()
	 */
	public int getCurrentIndex() {
		throw new RuntimeException("This method is not implemented!");
		//return current;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#incrementCurrentIndex()
	 */
	public void incrementCurrentIndex() {
		throw new RuntimeException("This method is not implemented!");
		//current++;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#setCurrentIndex(int)
	 */
	public void setCurrentIndex(int current) {
		this.current = current;
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#getNumberOfConstituents()
	 */
	public int getNumberOfConstituents() {
		int count = 0;
		for (ITree<T> t : this) {
			count += t.getNumberOfConstituents();
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#getNumberOfUniqueNonTerminals()
	 */
	public int getNumberOfUniqueNonTerminals() {
		int count = 0;
		Set<NonTerminal> nts = new HashSet<NonTerminal>();
		for (ITree<T> t : this) {
			List<IConstituent<T>> constituentStructure = t.getConstituentStructure();
			for (IConstituent<T> constituent : constituentStructure) {
				nts.addAll(constituent.getNonTerminals());
			}
		}
		count = nts.size();
		nts.clear();
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#addComment(java.lang.String)
	 */
	public void addComment(String string) {
		this.comments.add(string);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#getComments()
	 */
	public Vector<String> getComments() {
		return comments;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#addTree(org.schwiebert.abl4j.data.ITree)
	 */
	public void addTree(ITree<T> tree) {
		super.add(tree);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#indexOf(org.schwiebert.abl4j.data.ITree)
	 */
	public int indexOf(ITree<T> t) {
		return super.indexOf(t);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#addAllTrees(java.util.Collection)
	 */
	public void addAllTrees(Collection<ITree<T>> trees) {
		super.addAll(trees);
	}

	/*
	 * (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ITreeBank#getTrees()
	 */
	public Collection<ITree<T>> getTrees() {
		return this;
	}

	public void deleteTree(int index) {
		super.remove(index);
	}
	
	public void deleteTrees(int from, int to) {
		super.removeRange(from, to);
	}
	
}
