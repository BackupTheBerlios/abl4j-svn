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

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;
import org.schwiebert.abl4j.util.WordMapping;


/**
 * Helper class for IO Operations.
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *
 */
@SuppressWarnings("unchecked")
public class TreebankReader implements ITreebankReader {

	private int indexInCurrentLine = 0;
	
	private Reader reader;
	private boolean doClose = true;

	private boolean ignoreAnnotations;
	
	public void configure(PropertiesMap pm) throws InvalidConfigurationException {
		String inputFile = pm.getProperty(AblProperties.INPUT_FILE);
		String encoding = pm.getProperty(AblProperties.INPUT_ENCODING);
		ignoreAnnotations = pm.getBoolean(AblProperties.INPUT_IGNORE_ANNOTATIONS);
		try {
			if(inputFile == null) {
				InputStream in = (InputStream) pm.get(AblProperties.INPUT_STREAM);
				if(in != null) {
					reader = new InputStreamReader(in, encoding);
				} else {
					Logger.getLogger(TreebankReader.class).info("Expecting input from System.in");
					reader = new InputStreamReader(System.in, encoding);
					doClose = false;
				}
			} else {
				reader = new InputStreamReader(new FileInputStream(inputFile), encoding);
			}
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}

	/**
	 * Fills a {@link ITreeBank} from content provided by a {@link Reader}.
	 * @param tb
	 * @throws IOException
	 */
	public void readTreebank(final ITreeBank tb) throws IOException {
		int cnt = 0;
		BufferedReader br = new BufferedReader(reader);
		boolean first = true;
		while (true) {
			ITree t = readTree(br);
			if(first) {
				String commentLine = t.getCommentLine();
				t.setCommentLine(null);
				String[] lines = commentLine.split("\n");
				for(int i = 0; i < lines.length; i++) {
					String comment = lines[i];
//				for (String comment : lines) {
					if(comment.startsWith("#")) {
						comment = comment.substring(1).trim();
					}
					tb.addComment(comment);
				}
				first = false;
			}
			if (t == null)
				break;
			// keep track of sentence relation when not exhaustive comparison
			if (!tb.isExhaustive()) {
				t.setSequenceId(++cnt);
				t.registerWordsInSentence();
			}
			// add tree to treebank
			tb.addTree(t);
		}
		tb.setCurrentIndex(0);
		if(doClose) {
			br.close();			
		}
		Logger.getLogger(TreebankReader.class).info("Treebank with " + tb.size() + " trees read");
	}

	/**
	 * Parses a {@link ITree} from content provided by a reader. The content
	 * must be formatted in ABL- or ABL4J-syntax.
	 * @param is the reader
	 * @return the tree, or null if the current line's length is 0.
	 * @throws IOException if an IO-Error occurs while reading from <code>is</code>.
	 */
	private ITree readTree(final BufferedReader is) throws IOException {
		ITree t = DataFactory.newTree();
		String line = is.readLine();
		StringBuffer commentLines = new StringBuffer();
		while (line != null && line.startsWith("#")) {
			if (commentLines.length() > 0)
				commentLines.append("\n");
			commentLines.append(line);
			line = is.readLine();
		}
		if (line == null)
			return null;
		// read the sentence part of the tree
		readSentence(t, line);
		if(ignoreAnnotations) return t;
		if (t.size() == 0)
			return null;
		if (indexInCurrentLine < line.length()) {
			while (indexInCurrentLine < line.length()) {
				char c = line.charAt(indexInCurrentLine);
				if (c == '(') {
					readStructure(t, line);
				} else if (c == '<') {
					readSequenceScore(t, line);
				} else {
					indexInCurrentLine++;
				}
			}
		}
		if (t.getCommentLine() == null) {
			t.setCommentLine(commentLines + "");
		} else {
			t.setCommentLine(t.getCommentLine() + commentLines);
		}
		return t;
	}
	
	private void readSequenceScore(final ITree t, final String line) {
		StringBuffer buffer = new StringBuffer();
		final int lineLength = line.length();
		while(indexInCurrentLine < lineLength) {
			char c = line.charAt(indexInCurrentLine);
			buffer.append(c);
			indexInCurrentLine++;
			if (c == '>') {
				break;
			}
		}
		String scoreString = buffer.substring("<score=".length(), buffer.lastIndexOf(">")).trim();
		double score = Double.parseDouble(scoreString);
		t.setSentenceScore(score);
	}

	/**
	 * Parses the structure of a {@link ITree} from String <code>line</code>.
	 * The (constituent-) structure must be formatted in ABL- or ABL4J-syntax.
	 * @param t The tree that will be enriched with structural information
	 * @param line The structural information to parse
	 */
	private void readStructure(final ITree t, final String line) {
		StringBuffer structure = new StringBuffer();
		while (indexInCurrentLine < line.length()) {
			char c = line.charAt(indexInCurrentLine);
			structure.append(c);
			indexInCurrentLine++;
			if (c == ')') {
				break;
			}
		}
		String structureString = structure.toString().substring(1, structure.length() - 1);
		String begin = "", end = "";
		int indexInStructure = 0;
		while (indexInStructure < structureString.length()) {
			char c = structureString.charAt(indexInStructure);
			indexInStructure++;
			if (c != ',') {
				begin += c;
			} else {
				break;
			}
		}
		while (indexInStructure < structureString.length()) {
			char c = structureString.charAt(indexInStructure);
			indexInStructure++;
			if (c != ',') {
				end += c;
			} else {
				break;
			}
		}
		int b = Integer.parseInt(begin);
		int e = Integer.parseInt(end);
		indexInStructure++;
		IConstituent cons = DataFactory.newConstituent(t, b, e);
		while (indexInStructure < structureString.length()) {
			String nTString = "";
			while (indexInStructure < structureString.length()) {
				char c = structureString.charAt(indexInStructure);
				indexInStructure++;
				if (c != ',' && c != ']') {
					if (c == '<') {
						int newIndex = structureString.indexOf('>', indexInStructure);
						String prob = structureString.substring(indexInStructure, newIndex);
						indexInStructure = newIndex + 1;
						readProbability(cons, prob);
					} else {
						nTString += c;
					}
				} else {
					break;
				}
			}
			nTString = nTString.trim();
			if (nTString.length() > 0) {
				Long ntValue = Long.parseLong(nTString);
				NonTerminal nt = new NonTerminal(ntValue);
				cons.add(nt);
			}
			nTString = "";
		}
		t.addStructure(cons);
	}

	private void readProbability(final IConstituent cons, final String prob) {
		String[] parts = prob.split(" ");
		String id = parts[0].substring(2);
		String pr = parts[1].substring(2);
		cons.setLocalScore(Integer.parseInt(id), Double.parseDouble(pr));
	}

	/**
	 * Parses the words of a sentence from String <code>currentLine</code> and adds 
	 * the information to {@link ITree} <code>t</code>.
	 * @param t the tree
	 * @param currentLine
	 */
	private void readSentence(final ITree t, final String currentLine) {
		t.clear();
		IWord word = null;
		indexInCurrentLine = 0;
		while ((word = readWord(currentLine)) != null) {
			t.addWord(word);
		}
	}

	/**
	 * Parses a single {@link IWord} from String <code>currentLine</code>.
	 * The parsed Word is registered within an instance of {@link WordMapping}.
	 * @see DataFactory#getWord(Object) for details.
	 * @param currentLine the String which contains the {@link ISentence}.
	 * @return the Word, or <code>null</code> if the current line is empty or already parsed, or if the sentence delimiter @@@ was found.
	 */
	private IWord readWord(final String currentLine) {
		StringBuffer w;
		
		// when there is sth on istream -> go to first non-space char
		if (indexInCurrentLine >= currentLine.length()) {
			return null;
		}
		while (true) {
			if (indexInCurrentLine >= currentLine.length()) {
				return null;
			}
			char c = currentLine.charAt(indexInCurrentLine);
			if (!Character.isWhitespace(c)) {
				break;
			}
			indexInCurrentLine++;
		}

		w = new StringBuffer();
		while (indexInCurrentLine < currentLine.length() && !Character.isWhitespace(currentLine.charAt(indexInCurrentLine))) {
			w.append(currentLine.charAt(indexInCurrentLine));
			indexInCurrentLine++;
		}
		String string = w.toString();
		// if the @@@ sentence delimiter has been read -> stop
		if (string.compareTo("@@@") == 0) {
			return null;
		}
		// store the extracted word
		IWord word = DataFactory.getWord(string);
		return word;
	}


}
