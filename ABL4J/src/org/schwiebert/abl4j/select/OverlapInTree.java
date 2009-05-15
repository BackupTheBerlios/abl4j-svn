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

import org.schwiebert.abl4j.align.Predicate;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;

/**
 * Helper class that represents an overlap in a tree. Two constituents
 * <code>a</code> and <code>b</code> overlap, if their boundaries are related
 * as represented here:
 * <pre>
 * a __|_____________|____________
 * b      |              |
 *       
 * a ______|____________|_________
 * b  |             |
 * </pre>
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class OverlapInTree implements Predicate<IConstituent<?>> {

	private ITree<?> base;

	public OverlapInTree(ITree<?> c) {
		this.base = c;
	}

	public boolean matches(IConstituent<?> current) {
		for (IConstituent<?> i : base.getConstituentStructure()) {
			if (((i.getBeginIndex() < current.getBeginIndex()) && (current.getBeginIndex() < i.getEndIndex()) && (i
					.getEndIndex() < current.getEndIndex()))
					|| ((current.getBeginIndex() < i.getBeginIndex())
							&& (i.getBeginIndex() < current.getEndIndex()) && (current.getEndIndex() < i.getEndIndex()
							))) {
				return true;
			}
		}
		return false;
	}

}
