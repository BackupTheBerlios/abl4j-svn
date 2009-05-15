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
 * 
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class WagnerFisher extends EditDistance {

	int len1, len2;
	
	public final void printMatrix() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * This procedure builds the edit distance matrix and the alignment (see
	 * class Edit_distance).
	 */
	public WagnerFisher(final int b1, final int e1, final int b2, final int e2, final List<EditOperation> op) {
		super(op);
		buildMatrix(b1, e1, b2, e2);
		buildAlignment();

	}

	private final void buildMatrix(int b1, final int e1, int b2, final int e2) {
		len1 = len2 = 0;
		for (; b2 != e2; b2++) {
			len2++;
		}
		for (; b1 != e1; b1++) {
			len1++;
		}
		matrix = new float[len1 + 1][len2 + 1];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = 0F;
			}
		}
		final Pair<Integer, Integer> pair = new Pair<Integer, Integer>(0,0);
		for (int i = 0; i <= len1; i++) {
			for (int j = 0; j <= len2; j++) {
				if ((i == 0) && (j == 0)) {
					continue; // init step
				}
				pair.setValues(i,j);
				final Pair<Float, EditOperation> m = minGamma(pair);
				matrix[i][j] = m.first;
			}
		}
	}

	private final void buildAlignment() {
		Pair<Integer, Integer> currentCoordinate = new Pair<Integer, Integer>(len1, len2);
		while (!((currentCoordinate.first == 0) && (currentCoordinate.second == 0))) {
			Pair<Float, EditOperation> m = minGamma(currentCoordinate);
			alignment.add(0, m.second);
			currentCoordinate = m.second.previousCoordinates(currentCoordinate);
		}
	}

	public final Alignment getAlignment() {
		return alignment;
	}

}
