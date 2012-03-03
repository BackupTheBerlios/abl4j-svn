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
package org.schwiebert.abl4j.select;

import java.util.HashMap;
import java.util.Map;

import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.Pair;


/**
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class ProbabilityStore {

	private final Map<Phrase, Integer> pstore = new HashMap<Phrase, Integer>();
	private final Map<NonTerminal, Integer> nstore = new HashMap<NonTerminal, Integer>();

	public synchronized int increase(Phrase phrase) {
		int toReturn = 0;
		Integer value = pstore.get(phrase);
		if (value == null) {
			value = 0;
		}
		value++;
		pstore.put(phrase, value);
		toReturn = value;
		Integer value2 = nstore.get(phrase.second);
		if (value2 == null)
			value2 = 0;
		nstore.put(phrase.second, value2 + 1);
		return toReturn;
	}

	public int getCount(Phrase phrase) {
		Integer value = pstore.get(phrase);
		if(value == null) {
			System.out.println("NO PHRASE FOUND: " + phrase + ", " + pstore.size());
		}
		return value.intValue();
	}

	public int getCount(NonTerminal nonterm) {
		return nstore.get(nonterm);
	}

}