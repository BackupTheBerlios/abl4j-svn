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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.ObjectPair;
import org.schwiebert.abl4j.util.Pair;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * An {@link ITreebankReader} that is able to read the Tiger Corpus format - see <a
 * href
 * ="http://www.ims.uni-stuttgart.de/projekte/TIGER/TIGERCorpus/">http://www.
 * ims.uni-stuttgart.de/projekte/TIGER/TIGERCorpus/</a> for details about the
 * corpus. Existing annotations are converted to ABLs internal data structures.
 * By setting the property {@link AblProperties#INPUT_IGNORE_ANNOTATIONS} to 
 * <code>true</code>, the structural information of the tiger treebank is ignored.  
 * 
 * @author sschwieb
 * 
 */
public class TigerCorpusReader implements ITreebankReader {

	private Reader reader;

	private boolean ignoreAnnotations = false;

	public void configure(PropertiesMap pm) throws InvalidConfigurationException {
		String inputFile = pm.getProperty(AblProperties.INPUT_FILE);
		String encoding = pm.getProperty(AblProperties.INPUT_ENCODING);
		ignoreAnnotations = pm.getBoolean(AblProperties.INPUT_IGNORE_ANNOTATIONS);
		try {
			if (inputFile == null) {
				reader = new InputStreamReader(System.in, encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(inputFile), encoding);
			}
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void readTreebank(ITreeBank tb) throws IOException {
		try {
			SAXBuilder saxB = new SAXBuilder();
			BufferedReader br = new BufferedReader(reader);
			Document xmlText = saxB.build(br);
			Element root = xmlText.getRootElement();
			List<Element> children = root.getChildren();
			HashMap<String, Object> internalMapping = new HashMap<String, Object>();
			int cnt = 0;
			List<IWord> temp = new ArrayList<IWord>();
			for (Iterator<Element> it = children.iterator(); it.hasNext();) {
				Element s = it.next();
				Element terminals = s.getChild("graph").getChild("terminals");
				ITree tree = DataFactory.newTree();
				List<Element> ts = terminals.getChildren("t");
				int counter = 0;
				temp.clear();
				for (Iterator<Element> iter = ts.iterator(); iter.hasNext();) {
					Element t = iter.next();
					String word = t.getAttributeValue("word");
					IWord w = DataFactory.getWord(word);
					String id = t.getAttributeValue("id");
					internalMapping.put(id, counter++);
				}
				IWord[] words = new IWord[temp.size()];
				temp.toArray(words);
				tree.setWords(words);
				if (!ignoreAnnotations) {
					Element nonterminals = s.getChild("graph").getChild("nonterminals");
					List<Element> nts = nonterminals.getChildren("nt");
					for (Iterator<Element> iter = nts.iterator(); iter.hasNext();) {
						Element nt = iter.next();
						IConstituent constituent = buildConstituent(internalMapping, tree, nt);
						if (constituent != null) {
							tree.addStructure(constituent);
						}
					}
				}
				if (!tb.isExhaustive()) {
					tree.setSequenceId(++cnt);
					tree.registerWordsInSentence();
				}
				tb.addTree(tree);
			}
			br.close();
		} catch (Exception e) {
			throw new IOException("Couldn't read treebank");
		}
	}

	@SuppressWarnings("unchecked")
	protected IConstituent buildConstituent(HashMap<String, Object> internalMapping, ITree tree, Element nt) {
		List<Element> edges = nt.getChildren("edge");
		String beginRef = edges.get(0).getAttributeValue("idref");
		String endRef = edges.get(edges.size() - 1).getAttributeValue("idref");
		Pair<String, String> pair = new ObjectPair<String, String>(beginRef, endRef);
		internalMapping.put(nt.getAttributeValue("id"), pair);
		int begin = getWordIndex(beginRef, internalMapping, true);
		int end = getWordIndex(endRef, internalMapping, false);
		if (begin != -1 && end != -1) {
			IConstituent c = DataFactory.newConstituent(tree, begin, end);
			return c;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private int getWordIndex(String id, HashMap<String, Object> map, boolean begin) {
		Object found = map.get(id);
		if (found == null)
			return -1;
		if (found instanceof Integer) {
			if (begin)
				return (Integer) found;
			return (Integer) found + 1;
		}
		Pair<String, String> pair = (Pair<String, String>) found;
		if (begin) {
			return getWordIndex(pair.getFirst(), map, begin);
		} else {
			return getWordIndex(pair.getSecond(), map, begin);
		}
	}

}
