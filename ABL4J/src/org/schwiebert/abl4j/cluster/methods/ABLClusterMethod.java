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

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.cluster.AblNtMap;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * This class implements the clustering part of the aligning phase of ABL.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
@SuppressWarnings("unchecked")
public class ABLClusterMethod implements ClusterMethod<AblNtMap>, MergeMethod<AblNtMap> {

	private Logger logger = Logger.getLogger(ABLClusterMethod.class);
	
	private AblNtMap ntm = new AblNtMap();


	public AblNtMap getMapping() {
		return ntm;
	}
	
	public void setMapping(AblNtMap ntm) {
		this.ntm = ntm;
	}

	public int getNumberOfClusteredNTs() {
		return ntm.getNewNtCount();
	}


	public int getNumberOfUnclusteredNTs() {
		return ntm.getOldNtCount();
	}


	
	public boolean findClusters(ITreeBank tb) {
		// consider all trees in treebank
		final int size = tb.size();
		for(int i = 0; i < size; i++) {
			ITree t = tb.get(i);
			findClustersInTree(t);
		}
		return true;
	}
	
	private void findClustersInTree(ITree<?> t) {
		NonTerminal ntNew = new NonTerminal();
		int treenr = 0;
		logger.debug("Finding clusters " + treenr);
		treenr++;

		// for each hypothesis in the tree
	
		for (IConstituent<?> c : t.getConstituentStructure()) {
			// get the first NT
			if(c == null) {
				logger.error("Empty constituent found in tree " + t.getSequenceId());
				continue;
			}
			NonTerminal i = c.get(0);
			
			// get new NT for first old NT
			ntNew = ntm.getNewNT(t, c, i);
			// take the next NT:
			for (int x = 1; x < c.size(); x++) {
				i = c.get(x);
				ntm.putNTmapping(i, ntNew);
			}
		}
	}

	
	public void mergeClusters(ITreeBank tb) {
		// consider all trees in treebank
		final int size = tb.size();
		for(int i = 0; i < size; i++) {
			ITree t = tb.get(i);
			mergeClustersInTree(t);
		}
	}
	
	private void mergeClustersInTree(ITree<?> t) {
		NonTerminal ntNew;
		// for each hypothesis in the tree
		for (IConstituent<?> c : t.getConstituentStructure()) {
			for (int x = 0; x < c.size(); x++) {
				// get the first NT
				NonTerminal i = c.get(x);
				// get new NT for first old NT
				ntNew = ntm.getNewNT(t, c, i);
				// merge all remaining NTs to the first one
				c.mergeNonterminals(ntNew);

			}

		}
	}

	public void configure(PropertiesMap properties) {
		// TODO Auto-generated method stub
		
	}
	
	

}
