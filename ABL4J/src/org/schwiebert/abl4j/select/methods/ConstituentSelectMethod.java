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
import org.schwiebert.abl4j.select.Phrase;
import org.schwiebert.abl4j.select.ProbabilityStore;

/**
 * In addition to only consider the word of the sentence delimited by
 * the hypothesis as in the method leaf, this model computes the
 * probability based on the words of the hypothesis and its type label.
 */
public class ConstituentSelectMethod extends AbstractSelectMethod {

	@SuppressWarnings("unchecked")
	public Phrase createPhrase(ITree<?> t, IConstituent<?> cons, int c) {
		int begin = cons.getBeginIndex();
		int end = cons.getEndIndex();
		ISentence s = t.createSubStructure(begin, end);
		return new Phrase(s, t.getConstituentStructure().get(c).get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public double computeCombinedProbability(ITree<?> t, Vector<IConstituent<?>> vc, ProbabilityStore prob) {
		double p = -Math.log((double) 1);
		int size = 0;
		Phrase phrase = new Phrase(null, null);
		for (IConstituent c : vc) {
			phrase.first = t.createSubStructure(c.getBeginIndex(), c.getEndIndex());
			phrase.second = c.get(0);
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
