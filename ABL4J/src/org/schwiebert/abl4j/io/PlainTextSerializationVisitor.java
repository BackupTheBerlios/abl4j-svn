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
import java.util.Iterator;
import java.util.Properties;

import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.AblProperties;

@SuppressWarnings("unchecked")
public class PlainTextSerializationVisitor implements ISerializationVisitor {

	private Writer writer;
	
	private boolean printConstituentScore = true;
	private boolean printSentenceId = false;
	private boolean printSentenceScore = false;
	
	public void initialize(Writer writer, Properties properties) throws IOException {
		boolean compatibility = Boolean.parseBoolean(properties.getProperty(AblProperties.COMPATIBILITY_MODE));
		if(compatibility) {
			printConstituentScore = false;
			printSentenceId = false;
			printSentenceScore = false;
		} else {
			printConstituentScore = true;
			printSentenceId = false;
			printSentenceScore = true;
		}
		this.writer = writer;
	}

	public void visitConstituent(IConstituent c, boolean begin) throws IOException {
		if(begin) {
			writer.write("(" + c.getBeginIndex() + "," + c.getEndIndex() + ",[");
		} else {
			if (printConstituentScore && /*c.getLocalScoreMap() != null && */ c.getLocalScoreMap().size() != 0) {
				Iterator<Integer> sentences = c.getLocalScoreMap().keySet().iterator();
				while (sentences.hasNext()) {
					Integer sentenceId = sentences.next();
					writer.write(" <s=" + sentenceId + " p=" + c.getLocalScoreMap().get(sentenceId) + ">");
				}
			}
			writer.write("])");
		}
	}

	public void visitNonTerminal(NonTerminal nonTerminal, boolean hasNext) throws IOException {
		try {
			writer.write(nonTerminal.value+"");
			if(hasNext) {
				writer.write(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

		
	public void visitSentence(ISentence sentence) throws IOException {
		if(sentence.getCommentLine() != null && sentence.getCommentLine().length() > 0) {
			writer.write("# " + sentence.getCommentLine() + "\n");
		}
		if (printSentenceId) {
			writer.write("<S " + sentence.getSequenceId() + "> ");
		}
	}

	public void visitTree(ITree tree, boolean begin) throws IOException {
		if(!begin) {
			if(printSentenceScore) {
				writer.write(" @@@ <score=" + tree.getSentenceScore() + ">");
			}
			writer.write("\n");
		} else {
			writer.write("@@@ ");
		}

	}

	public void visitTreeBank(ITreeBank<?> treebank) throws IOException {
			if(treebank.getComments() != null) {
				for (String comment : treebank.getComments()) {
					if(comment.trim().length() > 0)
					writer.write("# " + comment.trim() + "\n");
				}
			}
	}

	public void visitWord(IWord word, boolean hasNext) throws IOException {
		writer.write(""+word.getWord());
		writer.write(' ');
	}

	public void close() throws IOException {
		writer.close();
	}

	

	

}
