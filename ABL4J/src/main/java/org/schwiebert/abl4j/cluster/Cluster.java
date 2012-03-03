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

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.IAblComponent;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.cluster.methods.ClusterMethod;
import org.schwiebert.abl4j.cluster.methods.MergeMethod;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;


/**
 * This class implements the clustering part of the aligning phase.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
@SuppressWarnings("unchecked")
public class Cluster<NTM extends NonTerminalsMapping> implements IAblComponent{

	private ITreeBank tb;
	
	private Logger logger = Logger.getLogger(Cluster.class);
	
	private ClusterMethod<NTM> clusterMethod;
	
	private MergeMethod<NTM> mergeMethod;
	
	public void configure(PropertiesMap properties) throws InvalidConfigurationException {
		try {
			clusterMethod = (ClusterMethod) properties.getInitializer().getNewClassInstance(AblProperties.CLUSTER_METHOD);
			clusterMethod.configure(properties);
			mergeMethod = (MergeMethod) properties.getInitializer().getNewClassInstance(AblProperties.MERGE_METHOD);
			mergeMethod.configure(properties);
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}		
	}

	public void execute(ITreeBank treeBank) {
		if(clusterMethod == null) {
			throw new RuntimeException("ClusterMethod has not been defined!");
		}
		if(mergeMethod == null) {
			throw new RuntimeException("MergeMethod has not been defined!");
		}
		this.tb = treeBank;
		logger.info("Finding clusters");
		boolean clustered = clusterMethod.findClusters(tb);
		if(!clustered) {
			logger.error("Cluster operation failed!");
			return;
		}
		NTM ntm = clusterMethod.getMapping();
		logger.info("Merging clusters");
		mergeMethod.setMapping(ntm);
		mergeMethod.mergeClusters(tb);
		logger.info("Cluster completed");
	}

	/**
	 * Returns the size of the treebank
	 * @return
	 */
	public int size() {
		return tb.size();
	}

	/**
	 * Returns the {@link ITreeBank}
	 * @return
	 */
	public ITreeBank getTreebank() {
		return tb;
	}

	/**
	 * Returns the number of unique {@link NonTerminal}s in input
	 * @return
	 */
	public int getNumberOfUniqueNTInput() {
		return clusterMethod.getNumberOfUnclusteredNTs();
	}

	/**
	 * Returns the number of unique {@link NonTerminal}s in output
	 * @return
	 */
	public int getNumberOfUniqueNTOutput() {
		return mergeMethod.getNumberOfClusteredNTs();
	}

}
