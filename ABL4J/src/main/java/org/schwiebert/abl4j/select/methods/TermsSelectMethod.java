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

import java.util.Vector;

import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.select.Phrase;
import org.schwiebert.abl4j.select.ProbabilityStore;

/**
 * The method leaf (or terms) computes the probability of a hypothesis
 * by counting the number of times the particular words of the
 * hypothesis have occurred in the learned text as a hypothesis, divided
 * by the total number of hypotheses.
 */
public class TermsSelectMethod extends AbstractSelectMethod {

	@SuppressWarnings("unchecked")
	public Phrase createPhrase(ITree t, IConstituent cons, int c) {
		int begin = cons.getBeginIndex();
		int end = cons.getEndIndex();
		ISentence s = t.createSubStructure(begin, end);
		return new Phrase(s, NonTerminal.ZERO_NON_TERMINAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public double computeCombinedProbability(ITree<?> t, Vector<IConstituent<?>> vc, ProbabilityStore prob) {
		double p = -Math.log((double) 1);
		int size = 0;
		NonTerminal nt = NonTerminal.ZERO_NON_TERMINAL;
		Phrase phrase = new Phrase(null, null);
		phrase.second = nt;
		for (IConstituent c : vc) {
			phrase.first = t.createSubStructure(c.getBeginIndex(), c.getEndIndex());
			double r = computeProbability(phrase, prob);
			c.setLocalScore(t.getSequenceId(), r);
			p += r;
			size++;
		}
		if (size != 0) {
			double result = p / size;
			return result;
		}
		return -1;
	}
	
	


	

}
