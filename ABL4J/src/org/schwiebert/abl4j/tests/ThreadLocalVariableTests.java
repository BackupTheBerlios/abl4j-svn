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
package org.schwiebert.abl4j.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.util.ABLInitializer;
import org.schwiebert.abl4j.util.IWordMapping;
import org.schwiebert.abl4j.util.StringMapping;
import org.schwiebert.abl4j.util.Tools;

public class ThreadLocalVariableTests {
	
	class TestThread extends Thread {
		
		@SuppressWarnings("unchecked")
		IWordMapping mapping1, mapping2;
		
		public void run() {
			Tools.configure(new ABLInitializer().getProperties());
			Random rand1 = Tools.getRandom();
			DataFactory.setMapping(new StringMapping());
			mapping1 = DataFactory.getMapping();
			assertTrue(new NonTerminal().value == 0);
			assertTrue(new NonTerminal().value == 1);
			sleepRandom(500);
			Random rand2 = Tools.getRandom();
			assertTrue(rand1 == rand2);
			mapping2 = DataFactory.getMapping();
			assertTrue(mapping1 == mapping2);
			assertTrue(new NonTerminal().value == 2);
			assertTrue(new NonTerminal().value == 3);
			NonTerminal.resetUpperNt();
			sleepRandom(500);
			assertTrue(new NonTerminal().value == 0);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInhertitedThreadLocals() throws Exception {
		List<TestThread> threads = new ArrayList<TestThread>();
		int threadSize = 100;
		for(int i = 0; i < threadSize; i++) {
			TestThread thread = new TestThread();
			thread.start();
			threads.add(thread);
			sleepRandom(50);
		}
		HashSet<IWordMapping> mappings = new HashSet<IWordMapping>();
		for (TestThread thread : threads) {
			thread.join();
			mappings.add(thread.mapping1);
			mappings.add(thread.mapping2);
		}
		assertTrue(mappings.size() == threadSize);
	}
		
	private void sleepRandom(int max) {
		try {
			Thread.sleep(new Random().nextInt(max));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
