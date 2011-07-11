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
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.xml.sax.XMLReader;

/**
 * Visitor-Implementation that serializes an {@link ITreeBank} in (proprietary) XML.
 * @author sschwieb
 * @see XMLReader
 * @see IOFactory
 *
 */
@SuppressWarnings("unchecked")
public class XMLVisitor implements ISerializationVisitor {

	private Element root;
	private Document document;
	private Writer writer;
	private Element currentTree;
	private Element currentConstituent;
	private Element currentNtList;
		
	public void close() throws IOException {
		Format format = Format.getPrettyFormat();
		format.setTextMode(Format.TextMode.PRESERVE);
		XMLOutputter outp = new XMLOutputter(format);
		outp.output(document, writer);
		writer.close();
	}
	
	public void initialize(Writer writer, Properties properties) throws IOException {
		document = new Document();
		this.writer = writer;
	}

	public void visitConstituent(IConstituent constituent, boolean begin)
			throws IOException {
		if(begin) {
			currentConstituent = new Element(XmlConstants.EL_CONSTITUENT);
			currentConstituent.setAttribute(XmlConstants.ATTR_CONST_BEGIN, constituent.getBeginIndex()+"");
			currentConstituent.setAttribute(XmlConstants.ATTR_CONST_END, constituent.getEndIndex()+"");
			currentNtList = new Element(XmlConstants.EL_NT_LIST);
			currentConstituent.addContent(currentNtList);
			Map<Integer, Double> scores = constituent.getLocalScoreMap();
			Element scoresList = new Element(XmlConstants.EL_SCORES);
			currentConstituent.addContent(scoresList);
			for (Integer sequenceId : scores.keySet()) {
				Element score = new Element(XmlConstants.EL_SCORE);
				score.setAttribute(XmlConstants.ATTR_TREE_ID, sequenceId+"");
				score.setAttribute(XmlConstants.ATTR_SCORE, scores.get(sequenceId)+"");
				scoresList.addContent(score);
			}
			currentTree.addContent(currentConstituent);
		}
	}

	public void visitNonTerminal(NonTerminal nonTerminal, boolean hasNext)
			throws IOException {
		Element nt = new Element(XmlConstants.EL_NT);
		nt.setAttribute(XmlConstants.ATTR_ID,nonTerminal.value() + "");
		currentNtList.addContent(nt);
	}

	public void visitSentence(ISentence sentence) throws IOException {
		currentTree = new Element(XmlConstants.EL_TREE);
		root.addContent(currentTree);
		currentTree.setAttribute(XmlConstants.ATTR_ID, sentence.getSequenceId()+"");
		if(sentence.getCommentLine() != null) {
			String[] cl = sentence.getCommentLine().split("\n");
			Element comments = new Element(XmlConstants.EL_COMMENTS);
			currentTree.addContent(comments);
			for (String c : cl) {
				Element comment = new Element(XmlConstants.EL_COMMENT);
				comment.setText(c);
				comments.addContent(comment);
			}
		}
		
	}

	public void visitTree(ITree tree, boolean begin) throws IOException {
		currentTree.setAttribute(XmlConstants.ATTR_SCORE, tree.getSentenceScore()+"");
	}

	public void visitTreeBank(ITreeBank<?> treebank) throws IOException {
		root = new Element("treebank");
		document.setRootElement(root);
		if(treebank.getComments() != null && treebank.getComments().size() > 0) {
			Element comments = new Element(XmlConstants.EL_COMMENTS);
			root.addContent(comments);
			for (String comment : treebank.getComments()) {
				Element c = new Element(XmlConstants.EL_COMMENT);
				c.setText(comment);
				comments.addContent(c);
			}
		}

	}

	public void visitWord(IWord word, boolean hasNext) throws IOException {
		Element w = new Element(XmlConstants.EL_WORD);
		w.setAttribute(XmlConstants.ATTR_WORD_CONTENT, ""+word.getWord());
		w.setAttribute(XmlConstants.ATTR_WORD_INDEX, word.getIndex()+"");
		currentTree.addContent(w);
	}

}
