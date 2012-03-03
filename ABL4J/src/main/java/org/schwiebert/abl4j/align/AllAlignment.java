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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.distance.EditOperation;
import org.schwiebert.abl4j.util.Tools;


/**
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 */
public final class AllAlignment {

	

	private final EditOperation[] operations;

	private final Vector<Alignment> alignments;

	private final int begin1, end1, begin2, end2;
	
	private final ITree b, e;

	final class Link implements Comparable<Link> {

		private static final long serialVersionUID = -2080676139181086441L;
		
		private final int first, second;

		public Link(final int a, final int b) {
			this.first = a;
			this.second = b;
		}

		public String toString() {
			return "(" + first + " -> " + second + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Link) {
				return ((Link) obj).first == (first) && ((Link) obj).second == (second);
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
		
		@Override
		public int hashCode() {
			return first * 569 + second;
		}

	}

	final static class LinkList extends TreeSet<Link> {

		private static final long serialVersionUID = -6239132255101204160L;


	}

	final static class SetLinkList extends ArrayList<LinkList> {

		private static final long serialVersionUID = 6995244181986057703L;

	}

	final static class In<E> implements Predicate<E> {

		final Set<E> elem;

		public In(Set<E> e) {
			this.elem = e;
		}

		public boolean matches(E e) {
			return elem.contains(e);
		}

	}

	final static class Overlap implements Predicate<Link> {

		private final Link link;

		public Overlap(Link l) {
			this.link = l;
		}

		public boolean matches(Link e) {
			boolean result = (((e.first <= link.first) && (e.second >= link.second)) || ((e.first >= link.first) && (e.second <= link.second)));
			return result;
		}

	}

	final static class Subset implements Predicate<LinkList> {

		private final LinkList link;

		public Subset(LinkList l) {
			this.link = l;
		}

		public boolean matches(LinkList e) {
			if(link.size() < e.size()) return false;
				HashSet<Link> s = new HashSet<Link>(e);
			s.removeAll(link);
			return s.isEmpty();
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
		operations = new EditOperation[3];
		// Trivial operations: Insert, Delete, Substitute whole sequences
		operations[0] = new EditOperation.Insert(begin1, end1, begin2, end2);
		operations[1] = new EditOperation.Delete(begin1, end1, begin2, end2);
		operations[2] = new EditOperation.Substitute(b.getWordArray(), begin1, end1, e.getWordArray(), begin2, end2);
		//Logger.getLogger(getClass()).info("Aligning trees " + b.getSequenceId() + " and " + e.getSequenceId());
		final SetLinkList all = findAllLinks();
//		Logger.getLogger(getClass()).info("LinkLists: " + all.size());
//		for (LinkList l : all) {
//			Logger.getLogger(getClass()).info("Links: " + l.size());
//		}
		alignments = new Vector<Alignment>();
		for (int i = 0; i < all.size(); i++) {
			int currentI = 0, currentJ = 0;
			int ran_i = begin1, ran_j = begin2;
			Alignment newAlignment = new Alignment();
			LinkList linkList = all.get(i);
			for (Link ai : linkList) {
				while (currentI != ai.first) {
					//System.out.println("ADDFIRST " + currentI + ", " + ai.first);
					newAlignment.add(operations[0]);
					currentI++;
					ran_i++;
				}
				while (currentJ != ai.second) {
					//System.err.println("ADDSECOND");
					newAlignment.add(operations[1]);
					currentJ++;
					ran_j++;
				}
				newAlignment.add(operations[2]);
				currentI++;
				currentJ++;
				ran_i++;
				ran_j++;
			}
			while (ran_i != end1) {
				newAlignment.add(operations[0]);
				ran_i++;
			}
			while (ran_j != end2) {
				newAlignment.add(operations[1]);
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
		for (Link link : m) {
		//for (int i = 0; i < m_size; i++) {
			SetLinkList p_old = new SetLinkList();
			swap(p, p_old);
			SetLinkList p_align = new SetLinkList();
			for (LinkList j : p_old) {
				LinkList o = new LinkList();
				o.clear();
				List<Link> list = Tools.copyIf(j, new Overlap(link));
					o.addAll(list);
				if (o.isEmpty()) {
					LinkList j_new = new LinkList();
					j_new.addAll(j);
					j_new.add(link);
					LinkList copy = new LinkList();
					copy.addAll(j_new);
					p.add(copy);
				} else {
					LinkList j_new = new LinkList();
					j_new.addAll(j);
					LinkList copy = new LinkList();
					copy.addAll(j_new);
					p.add(copy);
					
					List new_end = Tools.removeIf(j_new, new In<Link>(o)); // -o
					j_new.removeAll(new_end);
					j_new.add(link); // +i
					copy = new LinkList();
					copy.addAll(j_new);
					if (!p_align.contains(copy)) {
						p_align.add(copy);
					}
				}
			}
			SetLinkList insert_true = new SetLinkList();
			for (int x = 0; x < p_align.size(); x++) {
				final Subset set = new Subset(p_align.get(x));
				if ((Tools.findIf(p, 0, p.size(), set) == -1)
						&& (Tools.findIf(p_align, 0, x, set) == -1)
						&& (Tools.findIf(p_align, x + 1, p_align.size(), set) == -1)) {
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
		final IWord[] bwords = b.getWordArray();
		final IWord[] ewords = e.getWordArray();
		for (int i_i = begin1; i_i != end1; i_i++, i++) {
			int j = 0;
			for (int j_i = begin2; j_i != end2; j_i++, j++) {
				if(bwords[i_i].equals(ewords[j_i])) {
					res.add(new Link(i, j));
				}
//				if (b.get(i_i).equals(e.get(j_i))) {
//					res.add(new Link(i, j));
//				}
			}
		}
		return res;
	}

	public final Vector<Alignment> getAlignments() {
	//	alignments.trimToSize();
		return alignments;
	}

}
