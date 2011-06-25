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

import java.util.Vector;

import org.schwiebert.abl4j.align.AllAlignment;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * Implements the "all"-alignment of ABL
 * @author sschwieb
 *
 */
public final class AllAlignmentsMethod extends AbstractAdvancedMethod {
	
	private boolean comparismMode = true;
	
	@SuppressWarnings("unchecked")
	@Override
	public final void handleEditOperationStructure(final ITreeBank tb, final ITree current) {
		int index = tb.indexOf(current);
		index++;
		final int tbSize = tb.size();
		for (; index < tbSize; index++) {
			ITree t = tb.get(index);
			AllAlignment aa = new AllAlignment(current, t, comparismMode);
			Vector<Alignment> alignments = aa.getAlignments();
			for(int i = 0; i < alignments.size(); i++) {
				handleEditOperationAlignment(current, t, alignments.get(i));
			}
			alignments.clear();
		}
	}

	/**
	 * In addition to {@link AbstractAdvancedMethod#configure(PropertiesMap)}, the
	 * property {@link AblProperties#COMPARISM_MODE} is also used.
	 */
	@Override
	public void configure(PropertiesMap properties) {
		this.comparismMode = properties.getBoolean(AblProperties.COMPARISM_MODE);
		// TODO Auto-generated method stub
		super.configure(properties);
	}

}
