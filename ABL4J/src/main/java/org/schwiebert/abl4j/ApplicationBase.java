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
package org.schwiebert.abl4j;

import java.io.IOException;

import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.io.NoWriterAvailableException;
import org.schwiebert.abl4j.util.ABLInitializer;
import org.schwiebert.abl4j.util.PropertiesMap;

public class ApplicationBase {

	protected PropertiesMap properties;
	
	protected PropertiesMap initialize(String[] args) {
		ABLInitializer initializer = new ABLInitializer();
		initializer.initialize(args);
		this.properties = initializer.getProperties();
		return properties;
	}
	
	protected void writeTreebank(ITreeBank tb) throws NoWriterAvailableException, IOException {
		IOFactory.getWriter(properties).writeTreebank(tb);
	}
}
