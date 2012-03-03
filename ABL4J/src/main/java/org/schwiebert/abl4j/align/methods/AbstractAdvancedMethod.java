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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.align.InsertMode;
import org.schwiebert.abl4j.align.PartType;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.distance.EditOperation;
import org.schwiebert.abl4j.distance.EditOperation.SubDis;
import org.schwiebert.abl4j.distance.EditOperation.Substitute;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.IntegerPair;
import org.schwiebert.abl4j.util.Pair;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * Base class of "advanced" alignment methods.
 * @author sschwieb
 * @see AllAlignmentsMethod
 * @see WagnerFisherAlignmentMethod
 *
 */
public abstract class AbstractAdvancedMethod implements AlignmentMethod {
	
	private PartType PART_TYPE;
	
	private boolean EXCLUDE_EMPTY;
	
	private boolean NO_MERGE;
		
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public AbstractAdvancedMethod() {
		
	}

	
	/**
	 * The Properties {@link AblProperties#PART_TYPE}, {@link AblProperties#EXCLUDE_EMPTY}
	 * and {@link AblProperties#NO_MERGE} are read and will be used within the alignment
	 * process. Subclasses check additional properties - see {@link WagnerFisherAlignmentMethod}
	 * and {@link AllAlignmentsMethod} for details.
	 */
	public void configure(PropertiesMap properties) {
		String optarg = properties.getProperty(AblProperties.PART_TYPE);
		if ("equal".equals(optarg)) {
			PART_TYPE = PartType.EQUAL;
		} else if ("unequal".equals(optarg)) {
			PART_TYPE = PartType.UNEQUAL;
		} else if ("both".equals(optarg)) {
			PART_TYPE = PartType.BOTH;
		}
		EXCLUDE_EMPTY = properties.getBoolean(AblProperties.EXCLUDE_EMPTY);
		NO_MERGE = properties.getBoolean(AblProperties.NO_MERGE);
	}

	/**
	 * Performs the edit operations
	 */
	public abstract void handleEditOperationStructure(ITreeBank tb, ITree current);
	
	

	@SuppressWarnings("unchecked")
	protected final void handleEditOperationAlignment(final ITree t1, final ITree t2, final List<EditOperation> alignments) {

		InsertMode currentMode = InsertMode.NONE, nextWord = InsertMode.NONE;
		Pair<Integer, Integer> begin = new IntegerPair(0,0);
		Pair<Integer, Integer> current = new IntegerPair(0,0);
		final int alignmentsSize = alignments.size();
		for(int i = 0; i < alignmentsSize; i++) {
			EditOperation operation = alignments.get(i);
//		for (EditOperation operation : alignments) {
			if ((operation instanceof SubDis || operation instanceof Substitute) && ((t1.get(current.getFirst())).equals(t2.get(current.getSecond())))) {
				nextWord = InsertMode.SAME; // match
			} else {
				nextWord = InsertMode.DIFF;
			}
			if (currentMode == InsertMode.NONE) {
				currentMode = nextWord;
			}
			if (currentMode != nextWord) { // handle hypothesis
				IConstituent c1 = DataFactory.newConstituent(t1, begin.getFirst(), current.getFirst());
				IConstituent c2 = DataFactory.newConstituent(t2, begin.getSecond(), current.getSecond());
				if (((PART_TYPE == PartType.BOTH) || (PART_TYPE == PartType.EQUAL)) && (currentMode == InsertMode.SAME)) {
					insertConstituents(t1, t2, c1, c2);
				} else if (((PART_TYPE == PartType.BOTH) || (PART_TYPE == PartType.UNEQUAL)) && (currentMode == InsertMode.DIFF)) {
					insertConstituents(t1, t2, c1, c2);
				}
				begin = current;
				currentMode = nextWord;
			}
			current = operation.nextCoordinates(current);
		}
		// Handle hypotheses at the end of the sentence
		if ((PART_TYPE == PartType.BOTH) || ((PART_TYPE == PartType.EQUAL) && (currentMode == InsertMode.SAME))
				|| ((PART_TYPE == PartType.UNEQUAL) && (currentMode == InsertMode.DIFF))) {
			final IConstituent c1 = DataFactory.newConstituent(t1, begin.getFirst(), t1.size());
			final IConstituent c2 = DataFactory.newConstituent(t2, begin.getSecond(), t2.size());
			insertConstituents(t1, t2, c1, c2);
		}
	}
	

	@SuppressWarnings("unchecked")
	protected final synchronized void insertConstituents(final ITree t1, final ITree t2, final IConstituent c1, final IConstituent c2) {
		if (!NO_MERGE) {
			final Iterator<IConstituent> c1pos = t1.findStructure(c1);
			final Iterator<IConstituent> c2pos = t2.findStructure(c2);
			if (c1pos.hasNext()) {
				if (c2pos.hasNext()) {
					c1.add(c2pos.next().getFirst());
					c2.add(c1pos.next().getFirst());
					insertConstituent(t1, c1);
					insertConstituent(t2, c2);
				} else {
					c2.add(c1pos.next().getFirst());
					insertConstituent(t2, c2);
				}
			} else {
				if (c2pos.hasNext()) {
					c1.add(c2pos.next().getFirst());
					insertConstituent(t1, c1);
				} else {
					final NonTerminal n = NonTerminal.newNonTerminal();
					c1.add(n);
					c2.add(n);
					insertConstituent(t1, c1);
					insertConstituent(t2, c2);
				}
			}
		} else {
			final NonTerminal n = NonTerminal.newNonTerminal();
			c1.add(n);
			c2.add(n);
			insertConstituent(t1, c1);
			insertConstituent(t2, c2);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected final void insertConstituent(final ITree t, final IConstituent c) {
		
			if (!(EXCLUDE_EMPTY && c.empty()) && c.valid()) {
				t.addStructure(c);
		}
	}

}
