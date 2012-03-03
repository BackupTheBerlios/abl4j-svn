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

/**
 * Interface that simulates C/C++-<i>Predicates</i> for Iterator-methods
 * like <i>remove_if</i>.
 * @author sschwieb
 *
 * @param <E>
 */
public interface Predicate<E> {

	/**
	 * Returns true if the Predicate is true for Element e, false otherwise.
	 * @param e an Element
	 * @return true or false
	 */
	boolean matches(E e);

}
