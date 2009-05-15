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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An Iterator that does not check {@link ConcurrentModificationException}.
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class FindIterator<T> implements Iterator<IConstituent<T>> {


	private List list;

	public FindIterator(IConstituent<T> searched, List list) {
		this.searched = searched;
		this.list = list;
	}

	private IConstituent<T> searched;
	/**
	 * Index of element to be returned by subsequent call to next.
	 */
	int cursor = 0;

	/**
	 * Index of element returned by most recent call to next or previous.
	 * Reset to -1 if this element is deleted by a call to remove.
	 */
	int lastRet = -1;


	public boolean hasNext() {
		if (cursor == list.size()) {
			return false;
		}
		return indexOf(searched, cursor) > -1;
	}

	public IConstituent<T> next() {
		// Disabled... not sure is this a good idea...
		//checkForComodification();
		try {
			int index = indexOf(searched, cursor);
			IConstituent next = (IConstituent) list.get(index);
			lastRet = index;
			cursor = index++;
			return next;
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Not supported - Will throw an {@link UnsupportedOperationException} if called.
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported!");
	}

	/**
     * Searches for the first occurence of the given argument, beginning 
     * the search at <code>index</code>, and testing for equality using 
     * the <code>equals</code> method. 
     *
     * @param   elem    an object.
     * @param   index   the non-negative index to start searching from.
     * @return  the index of the first occurrence of the object argument in
     *          this vector at position <code>index</code> or later in the
     *          vector, that is, the smallest value <tt>k</tt> such that 
     *          <tt>elem.equals(elementData[k]) && (k &gt;= index)</tt> is 
     *          <tt>true</tt>; returns <code>-1</code> if the object is not 
     *          found. (Returns <code>-1</code> if <tt>index</tt> &gt;= the
     *          current size of this <tt>Vector</tt>.)
     * @exception  IndexOutOfBoundsException  if <tt>index</tt> is negative.
     * @see     Object#equals(Object)
     */
	private synchronized int indexOf(Object elem, int index) {
    	int elementCount = list.size();
    	if (elem == null) {
	    for (int i = index ; i < elementCount ; i++)
		if (list.get(i)==null)
		    return i;
    	} else {
	    for (int i = index ; i < elementCount ; i++)
		if (elem.equals(list.get(i)))
		    return i;
    	}
	return -1;
    }
	
}