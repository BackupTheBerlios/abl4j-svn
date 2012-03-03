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
package org.schwiebert.abl4j.cluster.methods;

import org.schwiebert.abl4j.IConfigurable;
import org.schwiebert.abl4j.cluster.NonTerminalsMapping;
import org.schwiebert.abl4j.data.ITreeBank;

/**
 * 
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *
 * @param <NTM> The implementation of a {@link NonTerminalsMapping}
 */
public interface ClusterMethod<NTM extends NonTerminalsMapping> extends IConfigurable {
	
	/**
	 * Searches for clusters, which must be stored in a
	 * {@link NonTerminalsMapping} that will be used by
	 * a {@link MergeMethod}.
	 * @param tb
	 * @return true if the operation could be completed, false otherwise
	 */
	public boolean findClusters(ITreeBank tb);
	
	/**
	 * Returns the number of nonterminals before
	 * merge operation was applied.
	 * @return
	 */
	public int getNumberOfUnclusteredNTs();

	/**
	 * Returns the {@link NonTerminalsMapping} used by a
	 * {@link MergeMethod} for merging.
	 * @return
	 */
	public NTM getMapping();


}
