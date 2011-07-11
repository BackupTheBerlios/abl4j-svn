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
public class IntegerPair extends Pair<Integer,Integer> {

	private static final long serialVersionUID = 6722692557830611787L;


	public IntegerPair(final Integer a, final Integer b) {
		this.first = a;
		this.second = b;
	}

	@Override
	public Integer getFirst() {
		return first;
	}
	@Override
	public Integer getSecond() {
		return second;
	}

	private final int first;
	private final int second;


}
