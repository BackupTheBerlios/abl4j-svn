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

import java.io.Serializable;

/**
 * Helper class that represents a pair of two {@link Object}s.
 * 
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 */
public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 6722692557830611787L;


	public Pair(final A a, final B b) {
		this.first = a;
		this.second = b;
	}

	public A first;
	public B second;

	
	/**
	 * Returns <code>true</code> if <code>this.first</code> equals <code>obj.first</code> and 
	 * <code>this.second</code> equals <code>obj.second</code>.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			return first.equals(((Pair) obj).first) && second.equals(((Pair) obj).second);
		}
		return false;
	}

	/**
	 * Returns a hashCode generated from first and second
	 */
	@Override
	public int hashCode() {
		return ("" + first.hashCode() + "_" + second.hashCode()).hashCode();
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + first + ", " + second + "]";
	}

	public void setValues(A a, B b) {
		this.first = a;
		this.second = b;
	}

}
