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

import org.schwiebert.abl4j.data.ITreeBank;
import org.xml.sax.XMLReader;

/**
 * Helper class used by {@link XMLReader} and {@link XMLVisitor} to 
 * serialize and deserialize an {@link ITreeBank}.
 * @author sschwieb
 *
 */
public class XmlConstants {
	
	public static final String ATTR_ID = "id";
	
	public static final String EL_CONSTITUENT = "constituent";
	public static final String ATTR_CONST_BEGIN = "begin";

	public static final String ATTR_CONST_END = "end";
	
	public static final String EL_NT = "nt";
	
	public static final String EL_TREE = "tree";
	public static final String ATTR_SCORE = "score";

	public static final String EL_COMMENT = "comment";
	public static final String EL_COMMENTS = "comments";
	
	public static final String EL_WORD = "word";
	public static final String ATTR_WORD_CONTENT = "content";
	public static final String ATTR_WORD_INDEX = "index";

	public static final String EL_SCORES = "scores";

	public static final String EL_SCORE = "score";

	public static final String EL_NT_LIST = "nonterminals";

	public static final String ATTR_TREE_ID = "tree_id";
}
