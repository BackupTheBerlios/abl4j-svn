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
package org.schwiebert.abl4j.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;

/**
 * Definition of a visitor-interface that can be used to serialize a {@link ITreeBank}.
 * @author sschwieb
 *
 */
@SuppressWarnings("unchecked")
public interface ISerializationVisitor {
	
	public void initialize(Writer writer, Properties properties) throws IOException;
	
	public void visitTreeBank(ITreeBank<?> treebank) throws IOException;
	
	public void visitTree(ITree tree, boolean begin) throws IOException;
	
	public void visitSentence(ISentence tree) throws IOException;
	
	public void visitConstituent(IConstituent constituent, boolean begin) throws IOException;

	public void visitWord(IWord word, boolean hasNext) throws IOException;
	
	public void visitNonTerminal(NonTerminal nonTerminal, boolean hasNext) throws IOException;

	public void close() throws IOException;
	
}
