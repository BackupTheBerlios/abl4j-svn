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
package org.schwiebert.abl4j.cluster;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;


/**
 * This class contains the mapping from existing {@link NonTerminal}s in the
 * {@link ITreeBank} to new {@link NonTerminal}s for hypotheses with similar context.
 * 
 */
public class AblNtMap extends HashMap<NonTerminal, NonTerminal> implements NonTerminalsMapping {

	private static final long serialVersionUID = -5875852910988031984L;

	private int upperNtNew;

	private Logger logger = Logger.getLogger(AblNtMap.class);
	
	/**
	 * Create a new, empty {@link AblNtMap}.
	 */
	public AblNtMap() {
		upperNtNew = 0;
	}

	/**
	 * Returns the number of non-terminals that have
	 * been created
	 */
	public int getNewNtCount() {
		return upperNtNew;
	}
	

	public int getOldNtCount() {
		return size();
	}

	/**
	 * Associates a given NT with a new NT value if it
	 * has not been encountered before and associates this given NT with an
	 * existing NT value when it has.
	 */
	@SuppressWarnings("unchecked")
	public NonTerminal getNewNT(ITree t, IConstituent c, NonTerminal ntOld) {
		// look if the old NT exists in the map
		boolean contained = super.containsKey(ntOld);

		if (!contained) {
			// no -> create new NT
			NonTerminal ntNew = new NonTerminal(upperNtNew++);
			putNTmapping(ntOld, ntNew);
			return ntNew;
		} else {
			// yes -> return NT
			return get(ntOld);
		}
	}

	/**
	 * Aassociates the first NT provided with the
	 * second NT provided
	 */
	public void putNTmapping(NonTerminal ntOld, NonTerminal ntNew) {
		NonTerminal old = get(ntOld);
		if (old != null) {
			put(ntOld, old);
		} else {
			put(ntOld, ntNew);
		}
		String dmsg = "map [" + ntOld + "] to [" + ntNew + "]";
		logger.debug(dmsg);
	}
}