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
package org.schwiebert.abl4j.examples;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.align.methods.AlignmentMethod;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.PropertiesMap;

@SuppressWarnings("unchecked")
public class DemoAlignMethod implements AlignmentMethod {

	public static final String ALIGN_SENTENCES = "custom.namespace.align.sentences";
	private boolean alignSentences;

	public void handleEditOperationStructure(ITreeBank tb, ITree current) {
		for(int i = 0; i < tb.size(); i++) {
			ITree<?> other = tb.get(i);
			align(current, other);
		}
	}

	private void align(ITree current, ITree other) {
		if(current == other) return;
		// Get words which are shared between both sentences:
		Set<IWord> allWords = new HashSet<IWord>(Arrays.asList(current.getWordArray()));
		allWords.retainAll(Arrays.asList(other.getWordArray()));
		// Create a constituent for each of these words:
		for (IWord word : allWords) {
			// New non terminal symbol
			NonTerminal nt = new NonTerminal();
			int index = current.indexOf(word, 0);
			// Create constituent
			IConstituent demo2 = DataFactory.newConstituent(current, index, index+1);
			// Add non terminal symbol to constituent
			demo2.add(nt);
			// Add structure to the current sentence
			current.addStructure(demo2);
			
			// Same procedure for the second sentence:
			index = other.indexOf(word, 0);
			IConstituent demo3 = DataFactory.newConstituent(other, index, index+1);
			demo3.add(nt);
			other.addStructure(demo3);
		}
	}

	public void configure(PropertiesMap properties) throws InvalidConfigurationException {
		alignSentences = properties.getBoolean(DemoAlignMethod.ALIGN_SENTENCES);
		System.out.println("Value: " + alignSentences);
	}

}
