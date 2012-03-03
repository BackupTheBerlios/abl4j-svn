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
package org.schwiebert.abl4j.examples;

import java.io.IOException;
import java.util.Properties;

import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.align.Align;
import org.schwiebert.abl4j.cluster.Cluster;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.io.ITreebankReader;
import org.schwiebert.abl4j.io.ITreebankWriter;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.io.NoReaderAvailableException;
import org.schwiebert.abl4j.io.NoWriterAvailableException;
import org.schwiebert.abl4j.select.Select;
import org.schwiebert.abl4j.util.ABLInitializer;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

public class Usage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Usage().runABL4J();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void runABL4J() throws NoReaderAvailableException, IOException, InvalidConfigurationException, NoWriterAvailableException {
		// Initialize ABL4J:
		ABLInitializer initializer = new ABLInitializer();
		Properties props = new Properties();
		props.put(AblProperties.INPUT_FILE, "testdata/input/input.txt");
		props.put(AblProperties.OUTPUT_FILE, "testdata/output.txt");
		initializer.initialize(null, props);
		PropertiesMap properties = initializer.getProperties();
		// Create a new Treebank
		ITreeBank treeBank = DataFactory.newTreeBank();
		ITreebankReader reader = IOFactory.getReader(properties);
		reader.readTreebank(treeBank);
		// Create and configure Align, Cluster and Select components
		Align align = new Align();
		Cluster cluster = new Cluster();
		Select select = new Select();

		align.configure(properties);
		cluster.configure(properties);
		select.configure(properties);
		// Execute components
		align.execute(treeBank);
		cluster.execute(treeBank);
		select.execute(treeBank);
		
		// Write treebank
		ITreebankWriter writer = IOFactory.getWriter(properties);
		writer.writeTreebank(treeBank);
	}

}
