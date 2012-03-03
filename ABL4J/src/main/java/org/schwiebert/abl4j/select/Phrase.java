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

import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.Pair;

/**
 * Helper class that represents a Phrase
 * 
 * @author sschwieb
 * 
 */
public final class Phrase {

	private static final long serialVersionUID = 8624865838792609484L;
	public NonTerminal second;
	public ISentence first;

	public Phrase(final ISentence a, final NonTerminal b) {
		this.first = a;
		this.second = b;
	}
	
	/**
	 * Returns <code>true</code> if <code>this.first</code> equals <code>obj.first</code> and 
	 * <code>this.second</code> equals <code>obj.second</code>.
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			return first.equals(((Phrase) obj).first) && second.equals(((Phrase) obj).second);
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns a hashCode generated from first and second
	 */
	@Override
	public int hashCode() {
		return first.hashCode() * 31 + second.hashCode();
	}
	


}