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
package org.schwiebert.abl4j.data;


/**
 * This class is used to store a nonterminal. Creating a nonterminal actually
 * gives an unused nonterminal ''constant''. Creating a nonterminals always
 * returns a new, unique one. They are printed as integers. It is also possible
 * to create a nonterminal by initialising it with an integer. However, it may
 * be the case that when initialising the nonterminal with an integer, it is has
 * the same value as an already existing one.
 * 
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
public class LongNonTerminal extends NonTerminal {
	
	private static final long serialVersionUID = -3188269828701889124L;
	
	private final long value;
	
	@Override
	public long value() {
		return value;
	}
	
	public LongNonTerminal(long n) {
		this.value = n;
		synchronized (NonTerminal.class) {
			if (n >= upperNt.get().upperNT) {
				upperNt.get().upperNT =n + 1;
			}
		}
	}

	/**
	 * Creates a new Nonterminal whose value will be the
	 * currently largest value.
	 */
	public LongNonTerminal() {
		synchronized(upperNt) {
			value = (upperNt.get().upperNT + 1);
			upperNt.get().upperNT = value;
		}
	}
	
}
