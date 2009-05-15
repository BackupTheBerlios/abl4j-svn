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

import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * This class is used to create an                     {@link ITreebankReader}                     or                     {@link ITreebankWriter}                    . The concrete implementation  of the object returned by                     {@link IOFactory#getWriter(PropertiesMap)}                     or                     {@link IOFactory#getReader(PropertiesMap)}                    depends on the property                     {@link AblProperties#WRITER_CLASS}                     (or                     {@link AblProperties#READER_CLASS}                    ). The provided                     {@link PropertiesMap}                     is further used to configure the writer or reader. See                     {@link TreebankReader}                    or                     {@link TreebankWriter}                     for custom implementations. Currently, ABL4J supports two document formats: the "original" format as used by ABL, and a proprietary XML format.
 * @author                     sschwieb
 * @see ITreebankWriter
 * @see ITreebankReader
 * @uml.dependency   supplier="org.schwiebert.abl4j.io.ITreebankReader" stereotypes="Standard::Create"
 * @uml.dependency   supplier="org.schwiebert.abl4j.io.ITreebankWriter" stereotypes="Standard::Create"
 */
public class IOFactory {
	
	public static ITreebankWriter getWriter(PropertiesMap pm) throws NoWriterAvailableException {
		try {
			ITreebankWriter writer = (ITreebankWriter) pm.getNewClassInstance(AblProperties.WRITER_CLASS);
			writer.configure(pm);
			return writer;
		} catch (Exception e) {
			throw new NoWriterAvailableException("Couldn't get AblWriter", e);
		}
	}
	
	public static ITreebankReader getReader(PropertiesMap pm) throws NoReaderAvailableException {
		try {
			ITreebankReader reader = (ITreebankReader) pm.getNewClassInstance(AblProperties.READER_CLASS);
			reader.configure(pm);
			return reader;
		} catch (Exception e) {
			throw new NoReaderAvailableException("Couldn't get AblReader: " + e.getMessage(), e);
		}
	}


}
