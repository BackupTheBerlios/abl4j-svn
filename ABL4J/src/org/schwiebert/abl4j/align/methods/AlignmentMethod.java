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

import org.schwiebert.abl4j.IConfigurable;
import org.schwiebert.abl4j.align.Align;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;

/**
 * An AlignmentMethod. In ABL4J, all alignment methods are represented
 * as interfaces, so they can be passed to the Align component via its
 * constructor {@link Align#Align(ITreeBank, AlignmentMethod)}.
 * 
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 * 
 * @see Align
 */
public interface AlignmentMethod extends IConfigurable {
	
	public void handleEditOperationStructure(ITreeBank tb, ITree<?> current);
	
	
}
