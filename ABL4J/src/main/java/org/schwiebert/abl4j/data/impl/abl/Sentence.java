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
package org.schwiebert.abl4j.data.impl.abl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.schwiebert.abl4j.data.ISentence;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.util.GoogleHashSet;

/**
 * Represents a Sentence as a List of {@link IWord} objects.
 * @author sschwieb
 *
 * @param <T>
 */
public class Sentence<T> implements ISentence<T>, Serializable {

	private static final long serialVersionUID = 7500901585917712938L;

	/**
	 * storing the sentence ID
	 */
	private int sentenceId;

	/**
	 * keeping track of sentences to compare with
	 */
	private Set<Integer> similars;


	/**
	 * An optional  comment line
	 */
	private String commentLine = "";

	protected IWord<T>[] words;

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sentenceId;
		result = prime * result + Arrays.hashCode(words);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (sentenceId != other.sentenceId)
			return false;
		if (!Arrays.equals(words, other.words))
			return false;
		return true;
	}

	public Sentence() {
		
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#setCommentLine(java.lang.String)
	 */
	public void setCommentLine(String commentLine) {
		this.commentLine = commentLine;
	}
	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#getCommentLine()
	 */
	public String getCommentLine() {
		return commentLine;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#setId(int)
	 */
	public void setSequenceId(int id) {
		sentenceId = id;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#getId()
	 */
	public int getSequenceId() {
		return sentenceId;
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#registerWordsInSentence()
	 */
	public void registerWordsInSentence() {
		Set<IWord<T>> allWords = new HashSet<IWord<T>>();
		for (IWord<T> iWord : words) {
			if(!allWords.contains(iWord)) {
				iWord.addSentenceToWord(sentenceId);
				allWords.add(iWord);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#buildSimilarSentencesSet()
	 */
	@SuppressWarnings("unchecked")
	public void buildSimilarSentencesSet() {
		if(similars == null) {
			similars = new GoogleHashSet<Integer>();
		}
		final int size = size();
		for (int i = 0; i < size; i++) {
			IWord w = get(i);
			List<Integer> v = w.getSentenceOccurrences();
			final int occSize = v.size();
			int start = -1;
			for(int j = 0; j < occSize; j++) {
				int sentence = v.get(j);
				if(sentence == sentenceId) {
					start = j+1;
					break;
				}
			}
			if(start >= 0 && start < occSize) {
				for(int j = start; j < occSize; j++) {
					Integer id = v.get(j);
					similars.add(id);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#isSimilarTo(int)
	 */
	public boolean isSimilarTo(int id) {
		if(similars == null) return false;
		return similars.contains(id);
	}


	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#getSequenceText()
	 */
	public String getSequenceText() {
		StringBuffer buffer = new StringBuffer();
		final int size = size();
		for(int i = 0; i < size; i++) {
			buffer.append(get(i).getWord() + " ");
		}
		return "" + buffer;
	}
	
	/* (non-Javadoc)
	 * @see org.schwiebert.abl4j.data.ISentence#indexOf(java.lang.Object, int)
	 */
    public int indexOf(IWord<T> elem, int index) {
    	int elementCount = size();
    	if (elem == null) {
	    for (int i = index ; i < elementCount ; i++)
		if (get(i)==null)
		    return i;
    	} else {
	    for (int i = index ; i < elementCount ; i++)
		if (elem.equals(get(i)))
		    return i;
    	}
	return -1;
    }
   

	public void setWords(IWord<T>[] words) {
		this.words = words;
	}

	public int size() {
		return words.length;
	}

	public IWord<T> get(int index) {
		return words[index];
	}
	
	public IWord<T>[] subArray(final int begin, final int end) {
		IWord<T>[] array = new IWord[end - begin];
		for(int i = begin; i < end; i++) {
			array[i-begin] = words[i]; 
		}
		return array;
	}
    
	public void clear() {
		words = null;
	}

	public IWord<T>[] getWordArray() {
		return words;
	}
	
	
	
}
