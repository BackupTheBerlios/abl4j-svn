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
package org.schwiebert.abl4j.select;

import org.schwiebert.abl4j.data.IConstituent;

/**
 * Helper class used to calculate overlaps.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class Overlap {

	/**
	 * Returns true if the ranges defined in {@link IConstituent} a and {@link IConstituent} b
	 * do overlap. 
	 */
	@SuppressWarnings("unchecked")
	public static boolean overlap(IConstituent a, IConstituent b) {
		boolean res = (((a.getBeginIndex() < b.getBeginIndex()) && (b.getBeginIndex() < a.getEndIndex()) && (a
				.getEndIndex() < b.getEndIndex())
				|| ((b.getBeginIndex() < a.getBeginIndex()) && (a.getBeginIndex() < b.getEndIndex()) && (b
						.getEndIndex() < a.getEndIndex()))));
		return res;
	}
}