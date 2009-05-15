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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * AblReader-Implementation that deserializes an  {@link ITreeBank}  from (proprietary) XML.
 * @author  sschwieb
 * @see XMLVisitor
 * @see  IOFactory
 */
@SuppressWarnings("unchecked")
public class XmlReader implements ITreebankReader, IXmlDeserializationVisitor {

	private Document document;
	private Element root;
	
	/**
	 * @uml.property  name="treebank"
	 * @uml.associationEnd  
	 */
	private ITreeBank treebank;
	/**
	 * @uml.property  name="currentTree"
	 * @uml.associationEnd  
	 */
	private ITree currentTree;
	/**
	 * @uml.property  name="constituent"
	 * @uml.associationEnd  
	 */
	private IConstituent constituent;
	
	private Reader reader;
	
	public void configure(PropertiesMap pm) throws InvalidConfigurationException {
		String inputFile = pm.getProperty(AblProperties.INPUT_FILE);
		String encoding = pm.getProperty(AblProperties.INPUT_ENCODING);
		try {
			if(inputFile == null) {
				reader = new InputStreamReader(System.in, encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(inputFile), encoding);
			}
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}

	public void readTreebank(ITreeBank tb) throws IOException {
		try {
			reader = new BufferedReader(reader);
			this.treebank = tb;
			SAXBuilder builder = new SAXBuilder(false);
			document = builder.build(reader);
			root = document.getRootElement();
			walk(root);
			reader.close();
		} catch (JDOMException e) {
			throw new IOException(e.getMessage());
		}
	}

	private void walk(Element element) throws IOException {
		String name = element.getName();
		if(XmlConstants.EL_CONSTITUENT.equals(name)) {
			visitConstituent(element);
		}
		if(XmlConstants.EL_NT.equals(name)) {
			visitNonTerminal(element);
		}
		if(XmlConstants.EL_TREE.equals(name)) {
			visitTree(element);
		}
		if(XmlConstants.EL_WORD.equals(name)) {
			visitWord(element);
		}
		if(XmlConstants.EL_SCORE.equals(name)) {
			visitScore(element);
		}
		List<Element> children = element.getChildren();
		for (Element child : children) {
			walk(child);
		}
	}

	private void visitScore(Element element) {
		double score = Double.parseDouble(element.getAttributeValue(XmlConstants.ATTR_SCORE));
		int treeId = Integer.parseInt(element.getAttributeValue(XmlConstants.ATTR_TREE_ID));
		constituent.setLocalScore(treeId, score);
		
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void initialize(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void visitConstituent(Element constituent) throws IOException {
		int begin = Integer.parseInt(constituent.getAttributeValue(XmlConstants.ATTR_CONST_BEGIN));
		int end = Integer.parseInt(constituent.getAttributeValue(XmlConstants.ATTR_CONST_END));
		this.constituent = DataFactory.newConstituent(currentTree, begin, end);
		currentTree.addStructure(this.constituent);
	}

	public void visitNonTerminal(Element nonTerminal) throws IOException {
		int id = Integer.parseInt(nonTerminal.getAttributeValue(XmlConstants.ATTR_ID));
		NonTerminal nt = new NonTerminal(id);
		System.out.println("Adding " + id + " to constituent of size " + constituent.size());
		constituent.add(nt);
	}

	public void visitSentence(Element sentence) throws IOException {
		// Not used
	}

	public void visitTree(Element tree) throws IOException {
		this.currentTree = DataFactory.newTree();
		treebank.addTree(currentTree);
		Element comments = tree.getChild(XmlConstants.EL_COMMENTS);
		this.currentTree.setSequenceId(Integer.parseInt(tree.getAttributeValue(XmlConstants.ATTR_ID)));
		if(comments != null) {
			List<Element> list = comments.getChildren(XmlConstants.EL_COMMENT);
			StringBuffer b = new StringBuffer();
			for(int i = 0; i < list.size(); i++) {
				b.append(list.get(i));
				if(i < list.size()-1) {
					b.append("\n");
				}
			}
			currentTree.setCommentLine(b+"");
		}
	}

	public void visitTreeBank(Element treebank) throws IOException {
		Element comments = treebank.getChild(XmlConstants.EL_COMMENTS);
		List<Element> list = comments.getChildren(XmlConstants.EL_COMMENT);
		for (Element comment : list) {
			this.treebank.addComment(comment.getTextTrim());
		}
		
	}

	public void visitWord(Element word) throws IOException {
		String content = word.getAttributeValue(XmlConstants.ATTR_WORD_CONTENT);
		word.getAttributeValue(XmlConstants.ATTR_WORD_INDEX);
		IWord w = DataFactory.getWord(content);
		currentTree.addWord(w);
		
	}

}
