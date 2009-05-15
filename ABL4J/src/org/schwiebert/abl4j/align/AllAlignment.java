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
package org.schwiebert.abl4j.align;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.distance.EditOperation;
import org.schwiebert.abl4j.util.Pair;
import org.schwiebert.abl4j.util.Tools;


/**
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 */
public final class AllAlignment {

	

	private final Vector<EditOperation> operations;

	private final Vector<Alignment> alignments;

	private final int begin1, end1, begin2, end2;
	
	@SuppressWarnings("unchecked")
	private final ITree b, e;

	final class Link extends Pair<Integer, Integer> {

		private static final long serialVersionUID = -2080676139181086441L;

		public Link(final int a, final int b) {
			super(a, b);
		}

		public String toString() {
			return "(" + first + " -> " + second + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Link) {
				return ((Link) obj).first.equals(first) && ((Link) obj).second.equals(second);
			}
			return false;
		}

		public int compareTo(Link link) {
			int x = first - link.first;
			if(x == 0) {
				return second - link.second;
			}
			return x;
			
		}

	}

	final class LinkList extends Vector<Link> {

		private static final long serialVersionUID = -6239132255101204160L;


	}

	final class SetLinkList extends Vector<LinkList> {

		private static final long serialVersionUID = 6995244181986057703L;

	}

	final class In<E> implements Predicate<E> {

		final Vector<E> elem;

		public In(Vector<E> e) {
			this.elem = e;
		}

		public boolean matches(E e) {
			return elem.contains(e);
		}

	}

	final class Overlap implements Predicate<Link> {

		private final Link link;

		public Overlap(Link l) {
			this.link = l;
		}

		public boolean matches(Link e) {
			boolean result = (((e.first <= link.first) && (e.second >= link.second)) || ((e.first >= link.first) && (e.second <= link.second)));
			return result;
		}

	}

	final class Subset implements Predicate<LinkList> {

		private final LinkList link;

		public Subset(LinkList l) {
			this.link = l;
		}

		public boolean matches(LinkList e) {
			boolean result = true;
			for (int i = 0; i < link.size(); i++) {
				if (!e.contains(link.get(i))) {
					result = false;
				}
			}
			return result;
		}
	}

	/**
	 * Test to simulate order in a c++ set
	 * @author stephan
	 *
	 */
	final class LexiComparator implements Comparator<LinkList>{

		public int compare(LinkList o1, LinkList o2) {
			for(int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
				int result = o1.get(i).compareTo(o2.get(i));
				if(result != 0) {
					return result;
				}
			}
			if(o1.size() == o2.size()) return 0;
			return o1.size() - o2.size();
		}
	}
	
	@SuppressWarnings("unchecked")
	public AllAlignment(final ITree b, final ITree e, final boolean comparismMode) {
		this.b = b;
		this.e = e;
		this.begin1 = 0;
		this.end1 = b.size();
		this.begin2 = 0;
		this.end2 = e.size();
		operations = new Vector<EditOperation>();
		alignments = new Vector<Alignment>();
		// Trivial operations: Insert, Delete, Substitute whole sequences
		operations.add(new EditOperation.Insert(begin1, end1, begin2, end2));
		operations.add(new EditOperation.Delete(begin1, end1, begin2, end2));
		operations.add(new EditOperation.Substitute(b, begin1, end1, e, begin2, end2));
		final SetLinkList all = findAllLinks();
		if(comparismMode) {
			Collections.sort(all, new LexiComparator());
		}
		for (int i = 0; i < all.size(); i++) {
			int currentI = 0, currentJ = 0;
			int ran_i = begin1, ran_j = begin2;
			Alignment newAlignment = new Alignment();
			for (Link ai : all.get(i)) {
				while (currentI != ai.first) {
					newAlignment.add(operations.get(0));
					currentI++;
					ran_i++;
				}
				while (currentJ != ai.second) {
					newAlignment.add(operations.get(1));
					currentJ++;
					ran_j++;
				}
				newAlignment.add(operations.get(2));
				currentI++;
				currentJ++;
				ran_i++;
				ran_j++;
			}
			while (ran_i != end1) {
				newAlignment.add(operations.get(0));
				ran_i++;
			}
			while (ran_j != end2) {
				newAlignment.add(operations.get(1));
				ran_j++;
			}
			alignments.add(newAlignment);
		}
	}

	private void swap(SetLinkList a, SetLinkList b) {
		SetLinkList temp = new SetLinkList();
		temp.addAll(a);
		a.clear();
		a.addAll(b);
		b.clear();
		b.addAll(temp);
	}

	@SuppressWarnings("unchecked")
	public SetLinkList findAllLinks() {
		final LinkList m = findAllMatchingTerminals();
		final SetLinkList p = new SetLinkList();
		p.add(new LinkList());
		for (int i = 0; i < m.size(); i++) {
			SetLinkList p_old = new SetLinkList();
			swap(p, p_old);
			SetLinkList p_align = new SetLinkList();
			for (LinkList j : p_old) {
				LinkList o = new LinkList();
				o.clear();
				List<Link> list = Tools.copyIf(j, 0, j.size(), new Overlap(m.get(i)));
				o.addAll(list);
				if (o.isEmpty()) {
					LinkList j_new = new LinkList();
					j_new.addAll(j);
					j_new.add(m.get(i));
					LinkList copy = new LinkList();
					copy.addAll(j_new);
					p.add(copy);
				} else {
					LinkList j_new = new LinkList();
					j_new.addAll(j);
					LinkList copy = new LinkList();
					copy.addAll(j_new);
					p.add(copy);
					List new_end = Tools.removeIf(j_new, 0, j_new.size(), new In<Link>(o)); // -o
					j_new.removeAll(new_end);
					j_new.add(m.get(i)); // +i
					copy = new LinkList();
					copy.addAll(j_new);
					if (!p_align.contains(copy)) {
						p_align.add(copy);
					}
				}
			}
			SetLinkList insert_true = new SetLinkList();
			for (int x = 0; x < p_align.size(); x++) {
				if ((Tools.findIf(p, 0, p.size(), new Subset(p_align.get(x))) == -1)
						&& (Tools.findIf(p_align, 0, x, new Subset(p_align.get(x))) == -1)
						&& (Tools.findIf(p_align, x + 1, p_align.size(), new Subset(p_align.get(x))) == -1)) {
					if (!insert_true.contains(p_align.get(x))) {
						LinkList copy = new LinkList();
						copy.addAll(p_align.get(x));
						insert_true.add(copy);
					}
				}
			}

			for (LinkList linkList : insert_true) {
				if (!p.contains(linkList)) {
					p.add(linkList);
				}
			}
		}
		return p;
	}

	public LinkList findAllMatchingTerminals() {
		final LinkList res = new LinkList();
		int i = 0;
		for (int i_i = begin1; i_i != end1; i_i++, i++) {
			int j = 0;
			for (int j_i = begin2; j_i != end2; j_i++, j++) {
				if (b.get(i_i).equals(e.get(j_i))) {
					res.add(new Link(i, j));
				}
			}
		}
		return res;
	}

	public final Vector<Alignment> getAlignments() {
		return alignments;
	}

}
