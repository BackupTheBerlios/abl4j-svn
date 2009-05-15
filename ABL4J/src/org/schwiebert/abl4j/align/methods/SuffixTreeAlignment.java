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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.align.AlignType;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.suffixtree.Ftree;
import org.schwiebert.abl4j.suffixtree.Suffixtree;
import org.schwiebert.abl4j.suffixtree.ctfactory;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * Will probably implement the "suffix tree"-alignment of ABL some day.
 * Currently, this alignment does not work!
 * @author sschwieb
 *
 */
public class SuffixTreeAlignment implements AlignmentMethod {

	private AlignType alignType;
	private Logger logger = Logger.getLogger(SuffixTreeAlignment.class);
	private boolean excludeEmpty;

	public void configure(PropertiesMap properties) {
		String at = properties.getProperty(AblProperties.ALIGN_TYPE);
		if("st1".equalsIgnoreCase(at)) {
			alignType = AlignType.ST1;
		}
		if("st2".equalsIgnoreCase(at)) {
			alignType = AlignType.ST1;
		}
		if("st3".equalsIgnoreCase(at)) {
			alignType = AlignType.ST1;
		}
		if("st4".equalsIgnoreCase(at)) {
			alignType = AlignType.ST1;
		}
		excludeEmpty = Boolean.parseBoolean(properties.getProperty(AblProperties.EXCLUDE_EMPTY));
	}
	
	public SuffixTreeAlignment() {
	}
	
	@SuppressWarnings("unchecked")
	public void handleEditOperationStructure(ITreeBank tb, ITree current) {
		// declare St objects in the case of methods ST*
		Suffixtree st = new Suffixtree(tb, false);
		Suffixtree pt = new Suffixtree(tb, true);

		if (alignType == AlignType.ST2) {
			st.fix = 1;
			tb.doReverse();
		}

		
		logger.info("Finding structure");
		for (int i = 0; i < tb.size(); i++) {
			ITree s = tb.get(i);
			// suffix tree alignment
			logger.info("Aligning sentence " + tb.getCurrentIndex());
			//if ((ALIGN_TYPE == AlignType.ST1) || (ALIGN_TYPE == AlignType.ST2) || (ALIGN_TYPE == AlignType.ST3)
			//		|| (ALIGN_TYPE == AlignType.ST4)) {
				
				handleSuffixTreeStructure(tb, s, st);
			//} 
			tb.incrementCurrentIndex();
		}

		if ((alignType == AlignType.ST3) || (alignType == AlignType.ST4)) {
			// for ST3 and ST4 also construct a prefix tree
			tb.setCurrentIndex(0);
			tb.doReverse();
			for (int i = 0; i < tb.size(); i++) {
				ITree s = tb.get(i);
				handleSuffixTreeStructure(tb, s, pt);
				tb.incrementCurrentIndex();
			}
		}

		// structure to store suffixes and prefixes
		Ftree[] ftrees;
		ftrees = new Ftree[tb.size()];

		st.align(ftrees);

		if ((alignType == AlignType.ST3) || (alignType == AlignType.ST4)) {
			tb.doReverse();
			pt.align(ftrees);
			tb.doReverse();
		}

		if ((alignType == AlignType.ST2) || (alignType == AlignType.ST3) || (alignType == AlignType.ST4)) {
			// reverse back
			tb.doReverse();
		}

		for (int i = 0; i < tb.size(); i++) {
			if ((alignType == AlignType.ST1) || (alignType == AlignType.ST3)) {
				int old = ftrees[i].sufs.get(0).get(0);
				old++;
				ftrees[i].sufs.get(0).put(0, old);
				Iterator<Integer> sufit = ftrees[i].sufs.keySet().iterator();
				while (sufit.hasNext()) {
					Integer sufit1 = sufit.next();
					Iterator<Integer> sufitB = ftrees[i].sufs.get(sufit1).keySet().iterator();
					while (sufitB.hasNext()) {
						Integer sufit2 = sufitB.next();
						int b = sufit1;
						int e = tb.get(i).size();
						insertConstituentRaw(tb.get(i), b, e, sufit2);
					}
				}
			}

			if ((alignType == AlignType.ST2) || (alignType == AlignType.ST3)) {
				int old = ftrees[i].prefs.get(0).get(0);
				old++;
				ftrees[i].prefs.get(0).put(0, old);
				Iterator<Integer> prefitA = ftrees[i].prefs.keySet().iterator();
				while (prefitA.hasNext()) {
					Integer prefit1 = prefitA.next();
					Iterator<Integer> prefitB = ftrees[i].prefs.get(prefit1).keySet().iterator();
					while (prefitB.hasNext()) {
						Integer prefit2 = prefitA.next();
						int b = 0;
						int e = tb.get(i).size() - prefit1;
						insertConstituentRaw(tb.get(i), b, e, prefit2);
					}
				}
			}

			if (alignType == AlignType.ST4) {
				ctfactory hypos = new ctfactory(0);
				int v = ftrees[i].sufs.get(0).get(0) + 1;
				ftrees[i].sufs.get(0).put(0, v);
				v = ftrees[i].prefs.get(0).get(0) + 1;
				ftrees[i].prefs.get(0).put(0, v);
				Iterator<Integer> sufitA = ftrees[i].sufs.keySet().iterator();
				while (sufitA.hasNext()) {
					Integer sufit1 = sufitA.next();
					Iterator<Integer> sufitB = ftrees[i].sufs.get(sufit1).keySet().iterator();
					while (sufitB.hasNext()) {
						sufitB.next();
						int b = sufit1;
						Iterator<Integer> prefitA = ftrees[i].prefs.keySet().iterator();
						while (prefitA.hasNext()) {
							Integer prefit1 = prefitA.next();
							Iterator<Integer> prefitB = ftrees[i].prefs.get(prefit1).keySet().iterator();
							while (prefitB.hasNext()) {
								prefitB.next();
								int e = tb.get(i).size() - prefit1;
								insertConstituentRaw(tb.get(i), b, e, hypos.get(b, e));
							}
						}
					}
				}
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void insertConstituentRaw(ITree t, int b, int e, int nt) {
		IConstituent c = DataFactory.newConstituent(t, b, e);
		c.add(new NonTerminal(nt));
		insertConstituent(t, c);
	}

	@SuppressWarnings("unchecked")
	private void handleSuffixTreeStructure(ITreeBank tb, ITree current, Suffixtree st) {
		int i = tb.getCurrentIndex();
		st.construct(i);
	}


	@SuppressWarnings("unchecked")
	private void insertConstituent(ITree t, IConstituent c) {
		if (!(excludeEmpty && c.empty()) && c.valid()) {
			t.addStructure(c);
		}
	}

	
}
