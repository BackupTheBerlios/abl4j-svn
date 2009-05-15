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

import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * Base class of Wagner-Fisher alignments. See {@link WagnerFisherBiasedAlignmentMethod}
 * and {@link WagnerFisherDefaultAlignmentMethod} for details.
 * @author sschwieb
 *
 */
public abstract class WagnerFisherAlignmentMethod extends AbstractAdvancedMethod {

	private boolean EXHAUSTIVE;

	/**
	 * In addition to {@link AbstractAdvancedMethod#configure(PropertiesMap)}, the
	 * property {@link AblProperties#EXHAUSTIVE} is also used.
	 */
	@Override
	public void configure(PropertiesMap properties) {
		super.configure(properties);
		EXHAUSTIVE = properties.getBoolean(AblProperties.EXHAUSTIVE);
	}



	@Override
	@SuppressWarnings("unchecked")
	public final void handleEditOperationStructure(ITreeBank tb, ITree current) {
		int index = tb.indexOf(current);
		index++;
		final int tbSize = tb.size();
		for (; index < tbSize; index++) {
			ITree t = tb.get(index);
			boolean doAlign = true;

			if (!EXHAUSTIVE && !current.isSimilarTo(t.getSequenceId())) {
				doAlign = false;
			}

			if (doAlign) {
				Alignment alignment = findAlignment(current, t);
				handleEditOperationAlignment((ITree) current, (ITree) t, alignment);
				// delete a;
			}

		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected abstract Alignment findAlignment(ISentence s1, ISentence s2);

}
