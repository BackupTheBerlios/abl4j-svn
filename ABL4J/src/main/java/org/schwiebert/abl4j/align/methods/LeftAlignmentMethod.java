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
package org.schwiebert.abl4j.align.methods;

import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * Implements the "left"-alignment of ABL
 * @author sschwieb
 *
 */
public class LeftAlignmentMethod implements AlignmentMethod {

	public void configure(PropertiesMap properties) {}

	@SuppressWarnings("unchecked")
	public void handleEditOperationStructure(ITreeBank tb, ITree current) {
		int end = 0;
		final int treeSize = current.size();
		for (int i = 1; i != treeSize; ++i) {
			IConstituent c = DataFactory.newConstituent(current, Math.min(i, end), Math.max(i, end));
			c.add(NonTerminal.newNonTerminal());
			current.addStructure(c);
		}

	}

}
