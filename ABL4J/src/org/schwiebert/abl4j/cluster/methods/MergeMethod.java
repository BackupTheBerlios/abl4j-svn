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
import org.schwiebert.abl4j.data.NonTerminal;

/**
 * 
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *
 * @param <NTM> The implementation of a {@link NonTerminalsMapping}
 */
public interface MergeMethod<NTM extends NonTerminalsMapping> extends IConfigurable {

	/**
	 * Sets the {@link NonTerminalsMapping} as it was
	 * created by a {@link ClusterMethod}.
	 * @param nonTerminalMapping
	 */
	public void setMapping(NTM nonTerminalMapping);
	
	/**
	 * Merge-Method
	 * @param treebank
	 */
	public void mergeClusters(ITreeBank treebank);

	/**
	 * Returns the number of clustered {@link NonTerminal}s.
	 * @return
	 */
	public int getNumberOfClusteredNTs();

}
