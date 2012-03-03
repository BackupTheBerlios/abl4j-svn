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



/**
 * Helper class that represents a pair of two {@link Object}s.
 * 
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 */
public class ObjectPair<A, B> extends Pair<A,B> {

	private static final long serialVersionUID = 6722692557830611787L;

	public ObjectPair(final A a, final B b) {
		this.first = a;
		this.second = b;
	}

	@Override
	public A getFirst() {
		return first;
	}
	@Override
	public B getSecond() {
		return second;
	}

	private final A first;
	private final B second;


}
