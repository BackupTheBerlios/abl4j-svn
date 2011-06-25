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

import java.io.Serializable;

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
public class NonTerminal implements Serializable, Comparable<NonTerminal> {

	private static final long serialVersionUID = 3264432434761762448L;

	/**
	 * the value of the nonterminal
	 */
	public final long value;
		
	private static class UpperNTBuffer {
		
		long upperNT = 0;
	}
	
	private static InheritableThreadLocal<UpperNTBuffer> upperNt = new InheritableThreadLocal<UpperNTBuffer>();
	
	/**
	 * Only required for testing purposes. Don't call this
	 * from anywhere else.
	 */
	public static void resetUpperNt() {
		upperNt.set(new UpperNTBuffer());
		ZERO_NON_TERMINAL =  new NonTerminal(0);
		upperNt.get().upperNT = 0;
	}
	
	public static NonTerminal ZERO_NON_TERMINAL;
		
	/**
	 * Creates a new Nonterminal with value n.
	 * @param n
	 */
	public NonTerminal(long n) {
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
	public NonTerminal() {
		synchronized(upperNt) {
			value = upperNt.get().upperNT + 1;
			upperNt.get().upperNT = value;
		}
	}

	/**
	 * Returns true, if obj is an instanceof Nonterminal
	 * and the value of both objects is identical.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NonTerminal) {
			return ((NonTerminal) obj).value == value;
		}
		return false;
	}
	
	

	public int compareTo(NonTerminal arg0) {
		if(arg0.value < value) return 1;
		if(arg0.value > value) return -1;
		return 0;
	}

	/**
	 * Returns the hashcode of field value, calculated as
	 * in {@link Long#hashCode()} 
	 */
	@Override
	public int hashCode() {
		return (int) (value ^ (value >>> 32));
	}


	public String toString() {
		return "NT " + value;
	}

}
