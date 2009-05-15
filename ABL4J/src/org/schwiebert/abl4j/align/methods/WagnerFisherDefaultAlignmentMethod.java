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

import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.distance.Alignment;
import org.schwiebert.abl4j.distance.WFDefault;

/**
 * Implements the "wf default" alignment method of ABL.
 * @author sschwieb
 *
 */
public final class WagnerFisherDefaultAlignmentMethod extends WagnerFisherAlignmentMethod {

	@SuppressWarnings("unchecked")
	protected Alignment findAlignment(ISentence s1, ISentence s2) {
		ISentence s1A = DataFactory.newSentence();
		s1A.addWords(s1.getWords());
		ISentence s2A = DataFactory.newSentence();
		s2A.addWords(s2.getWords());
		return new WFDefault(s1A, 0, s1.size(), s2A, 0, s2.size()).getAlignment();
	}
}
