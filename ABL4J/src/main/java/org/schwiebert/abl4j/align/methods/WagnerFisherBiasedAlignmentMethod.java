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

import java.util.List;

import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.distance.WFBiased;

/**
 * Implements the "wf biased" alignment method of ABL.
 * @author sschwieb
 *
 */
public final class WagnerFisherBiasedAlignmentMethod extends WagnerFisherAlignmentMethod {

	@SuppressWarnings("unchecked")
	protected Alignment findAlignment(ISentence s1, ISentence s2) {
		IWord[] w1 = s1.getWordArray();
		IWord[] w2 = s2.getWordArray();
		return new WFBiased(w1, 0, w1.length, w2, 0, w2.length).getAlignment();
	}
	
}
