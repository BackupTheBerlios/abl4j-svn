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
package org.schwiebert.abl4j.align;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.IAblComponent;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.align.methods.AlignmentMethod;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

/**
 * This class implements the aligning phase of ABL.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 *         (Java-Implementation)
 * 
 */
@SuppressWarnings("unchecked")
public class Align implements IAblComponent {

	private final Logger logger = Logger.getLogger(Align.class);

	/**
	 * Global treebank: this is where all the data is stored
	 */
	private ITreeBank tb;

	/**
	 * The alignment method used for the alignment
	 */
	private AlignmentMethod alignmentMethod;

	private final NonTerminal start = NonTerminal.ZERO_NON_TERMINAL;

	private boolean exhaustive;

	private PropertiesMap properties;

	public void configure(PropertiesMap properties) throws InvalidConfigurationException {
		try {
			this.properties = properties;
			alignmentMethod = (AlignmentMethod) properties.getInitializer().getNewClassInstance(AblProperties.ALIGN_TYPE);
			alignmentMethod.configure(properties);
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}

	final class StructureThread extends Thread {

		private int mod;
		private int offset;
		private int counter = 0;
		private long startTime = 0;
		private final int LOG_MSG;

		public StructureThread(int threads, int i, int log) {
			this.mod = threads;
			this.offset = i;
			LOG_MSG = log;
		}

		public void run() {
			setName("Structurize " + offset);
			startTime = System.currentTimeMillis();
			final int tbSize = tb.size();
			for (int i = 0; i < tbSize; i++) {
				counter++;
				if (counter % LOG_MSG == 0) {
					long time = System.currentTimeMillis() - startTime;
					startTime = System.currentTimeMillis();
					logger.info("Thread " + offset + ": " + LOG_MSG / mod + " sequences aligned in " + (time / 1000) + " seconds. " + (tb.size() - i)
							/ mod + " sequences remaining, FreeMem: " + Runtime.getRuntime().freeMemory());
				}
				if (i % mod != offset)
					continue;
				final ITree<?> current = tb.get(i);
				logger.debug("Aligning sequence " + i);
				// start symbol
				IConstituent c = DataFactory.newConstituent(current, 0, current.size());
				c.setLocalScore(0, 0);
				c.add(start);
				current.addStructure(c);
				// edit distance alignment
				if (!exhaustive) {
					current.buildSimilarSentencesSet();
				}
				alignmentMethod.handleEditOperationStructure(tb, current);
				// tb.incrementCurrentIndex();
			}
		}

	}

	public void execute(ITreeBank tb) {
		if (alignmentMethod == null) {
			throw new RuntimeException("AlignmentMethod has not been defined!");
		}
		this.tb = tb;
		exhaustive = properties.getBoolean(AblProperties.EXHAUSTIVE);
		logger.info("Finding structure");
		final int threads = properties.getInteger(AblProperties.THREADS);
		final List<StructureThread> threadsList = new ArrayList<StructureThread>(threads);
		if (threads <= 1) {
			logger.info("Running align single threaded");
			new StructureThread(threads, 0, 500).run();
		} else {
			logger.info("Running align multi threaded ( " + threads + ")");
			for (int i = 0; i < threads; i++) {
				logger.info("Starting thread " + i);
				StructureThread thread = new StructureThread(threads, i, 500 * threads);
				threadsList.add(thread);
				thread.start();
			}
		}
		try {
			for (StructureThread thread : threadsList) {
				thread.join();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		logger.info("Align completed");
	}

}
