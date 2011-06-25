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

package org.schwiebert.abl4j.distance;

import java.util.List;

import org.schwiebert.abl4j.util.EditPair;
import org.schwiebert.abl4j.util.Pair;


/**
 * This file contains the base class of Edit Distance Algorithms. Specific instances
 * of the edit distance algorithm may be derived from this class.
 * 
 * @see WagnerFisher
 * @see WFBiased
 * @see WFDefault
 * 
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class EditDistance {

	protected final Alignment alignment;

	protected float[][] matrix;

	protected final EditOperation[] operations;

	public EditOperation[] getOperations() {
		return operations;
	}

	public EditDistance(final List<EditOperation> op) {
		this.operations = new EditOperation[op.size()];
		op.toArray(operations);
		alignment = new Alignment();
	}

	public EditPair minGamma(final Pair<Integer, Integer> p) throws RuntimeException {
		if (operations.length == 0) {
			throw new RuntimeException("Bad_operations");
		}
		int opCounter = 0;
		EditOperation op = operations[opCounter++];
		Pair<Integer, Integer> prev = op.previousCoordinates(p);
		while ((prev.first < 0) || (prev.second < 0)) {
			op = operations[opCounter++];
			if (op == operations[operations.length-1]) {
				throw new RuntimeException("Bad_operations");
			}
			prev = op.previousCoordinates(p);
		}
		EditPair min = op.gamma(p);
		min.first += matrix[prev.first][prev.second];
		while(opCounter < operations.length) {
			op = operations[opCounter++];
			prev = op.previousCoordinates(p);
			if ((prev.first >= 0) && (prev.second >= 0)) {
				EditPair next = op.gamma(p);
				next.first += matrix[prev.first][prev.second];
				if (next.first < min.first) {
					min = next;
				}
			}
		}
		return min;
	}

	/**
	 * Return the edit cost between the two sentences.
	 */
	public final float getCost(final int i, final int j) {
		if ((i < 0) || (j < 0)) {
			return 0;
		}
		return matrix[i][j];
	}


}
