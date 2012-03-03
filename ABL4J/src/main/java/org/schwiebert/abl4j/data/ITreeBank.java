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
import java.util.List;

import org.schwiebert.abl4j.data.impl.abl.TreeBank;

/**
 * An ITreebank is a sequence of {@link ITree} objects.
 * Default implementation: {@link TreeBank}.
 * 
 * @author sschwieb
 * 
 */
public interface ITreeBank<T> {

	/**
	 * This procedure sets whether or not to preserve memory by aligning all
	 * possible sentence pairs.
	 */
	public abstract void setExhaustive(boolean v);

	/**
	 * Returns the exhaustive flag of this Treebank.
	 * @return
	 */
	public abstract boolean isExhaustive();

	/**
	 * TODO: This method is not tested yet, as it's only used by suffix tree
	 * alignment.
	 */
	public abstract void doReverse();

	/**
	 * Returns the current index in the Treebank.
	 */
	public abstract int getCurrentIndex();

	/**
	 * Increases the current index.
	 */
	public abstract void incrementCurrentIndex();

	/**
	 * Sets the current index to current.
	 * @param currentthe current index
	 */
	public abstract void setCurrentIndex(int current);

	/**
	 * Returns the number of {@link IConstituent}s stored in
	 * all trees in this treebank.
	 * @return the number of constituents
	 */
	public abstract int getNumberOfConstituents();
	
	public int getNumberOfUniqueNonTerminals();

	/**
	 * Returns the number of {@link ITree}-objects stored
	 * in this treebank.
	 * @return the number of {@link ITree}s
	 */
	public abstract int size();

	/**
	 * Returns the {@link ITree} at index i, as in {@link List#get(int)}
	 * @param i the index
	 * @return the {@link ITree} at index i
	 */
	public abstract ITree<T> get(int i);

	/**
	 * Adds a comment to this treebank.
	 * @param the comment
	 */
	public abstract void addComment(String comment);

	/**
	 * Returns all comments on this treebank.
	 * @return
	 */
	public abstract List<String> getComments();

	/**
	 * Adds {@link ITree} <code>tree</code> to this
	 * treebank, as in {@link List#add(Object)}
	 * @param tree the tree to add
	 */
	public abstract void addTree(ITree<T> tree);

	/**
	 * Returns the index of {@link ITree} tree. Returns
	 * -1 if the tree is not contained in the treebank.
	 * @see List#indexOf(Object)
	 * @param tree the tree
	 * @return the index of the tree
	 */
	public abstract int indexOf(ITree<T> tree);

	/**
	 * Removes all {@link ITree}s in this treebank, as in
	 * {@link List#clear()}
	 */
	public abstract void clear();

	/**
	 * Returns all {@link ITree}s stored in the treebank.
	 * @return all {@link ITree}-objects
	 */
	public abstract Collection<? extends ITree<T>> getTrees();

	/**
	 * Adds the collection <code>trees</code> to the treebank, as in
	 * {@link List#addAll(Collection)}
	 * @param trees the trees to add.
	 */
	public abstract void addAllTrees(Collection<ITree<T>> trees);

	public abstract void deleteTree(int index);
	
	public abstract void deleteTrees(int from, int to);

}