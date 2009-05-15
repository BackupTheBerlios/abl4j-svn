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
package org.schwiebert.abl4j.select.methods;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.select.Overlap;
import org.schwiebert.abl4j.select.Phrase;
import org.schwiebert.abl4j.select.ProbabilityStore;

/**
 * A non-probablistic method that builds upon the assumption that a
 * hypothesis that is learned earlier is always correct. This means that
 * newly learned hypotheses that overlap with older ones are considered
 * to be incorrect, and thus should be removed.
 */
@SuppressWarnings("unchecked")
public class FirstSelectMethod extends AbstractSelectMethod {
	
	private Logger logger = Logger.getLogger(FirstSelectMethod.class);
	private boolean preserve;
	
	@Override
	public double computeCombinedProbability(ITree<?> t, Vector<IConstituent<?>> vc, ProbabilityStore prob) {
		// No need to implement, as it is not used in this class.
		throw new RuntimeException("This method is not implemented!");
	}



	public Phrase createPhrase(ITree t, IConstituent cons, int c) {
		int begin = cons.getBeginIndex();
		int end = cons.getEndIndex();
		ISentence s = t.createSubStructure(begin, end);
		return new Phrase(s, NonTerminal.ZERO_NON_TERMINAL);
	}

	public void select(ITreeBank tb) {
		selectFirst(tb);
	}

	private void selectFirst(ITreeBank tb) {
		if (!preserve) {
			for (int i = 0; i < tb.size(); i++) {
				ITree tree = tb.get(i);
				selectFirstInTree(tree);
			}
		} else {
			logger.error("Preserve not supported!");
		}
	}

	private void selectFirstInTree(ITree t) {
		List<IConstituent> structure = t.getConstituentStructure();
		List<IConstituent> reducedList = new Vector<IConstituent>();
		for (int i = 0; i < structure.size(); i++) {
			IConstituent base = structure.get(i);
			for (int j = i + 1; j < structure.size(); j++) {
				if (Overlap.overlap(base, structure.get(j))) {
					reducedList.add(structure.get(j));
				}
			}
			for (int j = i + 1; j < structure.size(); j++) {
				if (reducedList.contains(structure.get(j))) {
					structure.remove(j);
					j--;
				}
			}
		}
	}
	
}
