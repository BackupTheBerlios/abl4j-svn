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

import java.util.ArrayList;

import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.util.EditPair;
import org.schwiebert.abl4j.util.Pair;


/**
 * This interface contains the definition of an EditOperation as well as some
 * default implementations of EditOperation.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public interface EditOperation {

	/**
	 * return the gamma value of the operation (the pair of ints are indices in
	 * the sentences and 1 is the first word in the sentence)
	 */
	public EditPair gamma(Pair<Integer, Integer> p);

	/**
	 * return the previous coordinates (when the operation was applied)
	 */
	public Pair<Integer, Integer> previousCoordinates(Pair<Integer, Integer> p);

	/**
	 * return the next coordinates (when the operation is applied)
	 */
	public Pair<Integer, Integer> nextCoordinates(Pair<Integer, Integer> p);
	
	public static final class Delete implements EditOperation {

		protected final short begin1, end1, begin2, end2;

		public Delete(int b1, int e1, int b2, int e2) {
			begin1 = (short) b1;
			end1 = (short) e1;
			begin2 = (short) b2;
			end2 = (short) e2;
		}

		public EditPair gamma(Pair<Integer, Integer> pair) {
			return new EditPair(1F, this);
		}

		public Pair<Integer, Integer> previousCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first, p.second - 1);
		}

		public Pair<Integer, Integer> nextCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first, p.second + 1);
		}
	}

	public static final class Insert implements EditOperation {

		protected final int begin1, end1, begin2, end2;

		public Insert(int b1, int e1, int b2, int e2) {
			begin1 = b1;
			end1 = e1;
			begin2 = b2;
			end2 = e2;
		}

		public EditPair gamma(Pair<Integer, Integer> pair) {
			return new EditPair(1F, this);
		}

		public Pair<Integer, Integer> previousCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first - 1, p.second);
		}

		public Pair<Integer, Integer> nextCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first + 1, p.second);
		}
	}

	public static class Substitute implements EditOperation {

		protected final short begin1, end1, begin2, end2;
		protected final IWord[] sentence1, sentence2;

		public Substitute(IWord[] s1, int b1, int e1, IWord[] s2, int b2, int e2) {
			begin1 = (short) b1;
			end1 = (short) e1;
			sentence1 = s1;
			begin2 = (short) b2;
			end2 = (short) e2;
			sentence2 = s2;
		}

		public EditPair gamma(Pair<Integer, Integer> pair) {
			if ((pair.first <= 0) || (pair.second <= 0))
				return new EditPair(2F, this);
			final IWord w1 = sentence1[begin1 + pair.first - 1];
			final IWord w2 = sentence2[begin2 + pair.second - 1];
			if (w1.equals(w2)) {
				return new EditPair(0F, this);
			}
			return new EditPair(2F, this);
		}

		public Pair<Integer, Integer> previousCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first - 1, p.second - 1);
		}

		public Pair<Integer, Integer> nextCoordinates(Pair<Integer, Integer> p) {
			return new Pair<Integer, Integer>(p.first + 1, p.second + 1);
		}

		boolean match(Pair<Integer, Integer> p) {
			return (sentence1[begin1 + p.first - 1].equals(sentence2[begin2 + p.second - 1]));
		}

	}

	public static final class SubDis extends Substitute implements EditOperation {

		// TODO: How are these values generated in original version???
		// private static int len1 = 134715680, len2 = 32;
		private int len1 = 0, len2 = 0;

		public SubDis(IWord[] s1, int b1, final int e1, IWord[] s2, int b2, final int e2) {
			super(s1, b1, e1, s2, b2, e2);
			for (; b2 != e2; b2++) {
				len2++;
			}
			for (; b1 != e1; b1++) {
				len1++;
			}
		}

		/**
		 * mat=((index_S/|S|)-(index_T/|T|))*mean(|S|,|T|)
		 */
		public EditPair gamma(Pair<Integer, Integer> pair) {
			if ((pair.first <= 0) || (pair.second <= 0))
				return new EditPair(2F, this);
			if (sentence1[begin1 + pair.first - 1].equals(sentence2[begin2 + pair.second - 1])) {
				float f = pair.first;
				float s = pair.second;
				float value = (Math.abs((float) ((f - 1) / len1) - (float) ((s - 1) / len2)) * (len1 + len2) / 2f);
				return new EditPair(value, this);
			}
			return new EditPair(2F, this);
		}

	}

	public final class Default extends ArrayList<EditOperation> {

		private static final long serialVersionUID = -1442952554877131035L;

		public Default(IWord[] s1, int b1, int e1, IWord[] s2, int b2, int e2) {
			super(3);
			add(new Insert(b1, e1, b2, e2));
			add(new Delete(b1, e1, b2, e2));
			add(new Substitute(s1, b1, e1, s2, b2, e2));
		}

	}

	public final class Biased extends ArrayList<EditOperation> {

		private static final long serialVersionUID = -5884292503919273118L;

		public Biased(IWord[] s1, int b1, int e1, IWord[] s2, int b2, int e2) {
			super(3);
			add(new Insert(b1, e1, b2, e2));
			add(new Delete(b1, e1, b2, e2));
			add(new SubDis(s1, b1, e1, s2, b2, e2));
		}

	}

}
