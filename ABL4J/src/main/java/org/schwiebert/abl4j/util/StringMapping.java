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
package org.schwiebert.abl4j.util;

import java.util.Comparator;

/**
 * Default {@link IWordMapping} that can be used with Strings.
 * @author sschwieb
 *
 */
public class StringMapping extends WordMapping<String> {

	private static final long serialVersionUID = -8395059718441538478L;

	public StringMapping() {
		super(new Comparator<String>() {

			public int compare(final String o1, final String o2) {
				return o1.compareTo(o2);
			}
			
		});
	}
	
}
