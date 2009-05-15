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

	protected final List<EditOperation> operations;

	public List<EditOperation> getOperations() {
		return operations;
	}

	public EditDistance(final List<EditOperation> op) {
		this.operations = op;
		alignment = new Alignment();
	}

	public Pair<Float, EditOperation> minGamma(final Pair<Integer, Integer> p) throws RuntimeException {
		// Evtl. stimmt der Vergleich hier nicht -
		// wenn min in der c++ version anders ist, werden andere
		// Operationen zurückgegeben...
		if (operations.size() == 0) {
			throw new RuntimeException("Bad_operations");
		}
//		final Iterator<EditOperation> opIter = operations.iterator();
		int opCounter = 0;
//		EditOperation op = opIter.next();
		EditOperation op = operations.get(opCounter++);
		Pair<Integer, Integer> prev = op.previousCoordinates(p);
		while ((prev.first < 0) || (prev.second < 0)) {
			//op = opIter.next();
			op = operations.get(opCounter++);
			if (op == operations.get(operations.size() - 1)) {
				throw new RuntimeException("Bad_operations");
			}
			prev = op.previousCoordinates(p);
		}
		Pair<Float, EditOperation> min = op.gamma(p);
		min.first += matrix[prev.first][prev.second];
		//while (opIter.hasNext()) {
		while(opCounter < operations.size()) {
			//op = opIter.next();
			op = operations.get(opCounter++);
			prev = op.previousCoordinates(p);
			if ((prev.first >= 0) && (prev.second >= 0)) {
				Pair<Float, EditOperation> next = op.gamma(p);
				next.first += matrix[prev.first][prev.second];
				if (next.first < min.first) {
					//float f = next.first;
//					if (Math.round(f) == f) {
//					} else {
//						NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
//						nf.setMaximumFractionDigits(5);
//					}

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
