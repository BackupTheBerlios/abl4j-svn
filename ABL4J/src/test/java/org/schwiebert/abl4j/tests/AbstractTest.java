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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.data.IWord;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.data.impl.abl.TreeBank;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.io.ITreebankReader;
import org.schwiebert.abl4j.io.NoReaderAvailableException;
import org.schwiebert.abl4j.io.TreebankReader;
import org.schwiebert.abl4j.util.ABLInitializer;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;

public abstract class AbstractTest {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	protected File[] inputFiles;

	private File outputDirectory;

	private String namePrefix;
	
	protected long javaTime, cppTime;
	
	public AbstractTest(File inputDirectory, File outputDirectory, String namePrefix, FilenameFilter filter) {
		this.inputFiles = inputDirectory.listFiles(filter);
		this.outputDirectory = outputDirectory;
		this.namePrefix = namePrefix;
	}
	
	public void printExecTime() {
		System.out.println("Java: " + javaTime);
		System.out.println("C++ : " + cppTime);
		System.out.println("(Please note that C++ version would probably be executed faster if not called from java code.)");
	}

	public int runProgram(String cCmd, String cmdOpts, int counter)
			throws IOException {
		assertTrue("At least one input file must be available!", inputFiles != null && inputFiles.length > 0);
		assertTrue("Output Directory " + outputDirectory + " must exist!", outputDirectory.exists());
		for(File input : inputFiles) {
			try {
				String currentOpts = cmdOpts + " -i " + input.getAbsolutePath() + " ";
				currentOpts = currentOpts.trim();
				logger.info("Processing " + currentOpts);
				//cCmd = cCmd + " -i " + input.getAbsolutePath() + " ";
				String abl4jCommand = currentOpts;
				String jFileName = new File(outputDirectory, namePrefix + "_" +(counter < 100 ? 0 : "") + (counter < 10 ? 0 : "") + counter + "_j.txt").getAbsolutePath();
				abl4jCommand += " -o " + jFileName + " -j";
				String[] args = abl4jCommand.split("[ ]+");
				long javaStart = System.currentTimeMillis();
				runJavaProgram(args);
				
				long javaEnd = System.currentTimeMillis();
				String ablCommand = cCmd + currentOpts;
				String cFileName = new File(outputDirectory, namePrefix + "_" + (counter < 100 ? 0 : "") + (counter < 10 ? 0 : "") + counter + "_c.txt").getAbsolutePath();
				ablCommand += " -o " + cFileName;
				long cppStart = System.currentTimeMillis();
				Process p = Runtime.getRuntime().exec(ablCommand);
				InputStream in = p.getInputStream();
				while(in.read() != -1) {
					// Wait to ensure process is finished
				}
				long cppEnd = System.currentTimeMillis();
				String errorMsg = "Compare Failed on Fileset: " + counter;
				counter++;
				assertTrue(errorMsg, compareFiles(jFileName, cFileName));
				
				javaTime += (javaEnd - javaStart);
				cppTime += (cppEnd - cppStart);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return counter;
	}
	
	protected abstract void runJavaProgram(String[] args) throws IOException;


	public boolean compareTreeBanks(String fileName, String fileName2) {
		PropertiesMap pm = new PropertiesMap(new ABLInitializer());
		pm.setProperty(AblProperties.INPUT_FILE, fileName);
		pm.setProperty(AblProperties.INPUT_ENCODING, "UTF-8");
		TreebankReader reader = new TreebankReader();
		ITreeBank tb1 = DataFactory.newTreeBank();
		ITreeBank tb2 = DataFactory.newTreeBank();
		try {
			reader.configure(pm);
			reader.readTreebank(tb1);
			
			pm.setProperty(AblProperties.INPUT_FILE, fileName2);
			reader.configure(pm);
			reader.readTreebank(tb2);
			compareTreebanks(tb1, tb2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}

	private void compareTreebanks(ITreeBank tb1, ITreeBank tb2) {
		Assert.assertEquals(tb1.size(), tb2.size());
		Assert.assertEquals(tb1.getNumberOfConstituents(), tb2.getNumberOfConstituents());
		Map<NonTerminal, Integer> tb1Map = new HashMap<NonTerminal, Integer>();
		Map<NonTerminal, Integer> tb2Map = new HashMap<NonTerminal, Integer>();
		for(int i = 0; i < tb1.size(); i++) {
			ITree t1 = tb1.get(i);
			ITree t2 = tb2.get(i);
			Assert.assertEquals(t1.size(), t2.size());
			for(int j = 0; j < t1.size(); j++) {
				IWord w1 = t1.get(j);
				IWord w2 = t2.get(j);
				Assert.assertEquals(w1.getIndex(), w2.getIndex());
			}
			List<IConstituent<?>> cs1 = t1.getConstituentStructure();
			List<IConstituent<?>> cs2 = t2.getConstituentStructure();
			Assert.assertEquals(cs1.size(), cs2.size());
			compareConstituents(tb1Map, tb2Map, cs1, cs2);
		}
	}

	protected void compareConstituents(Map<NonTerminal, Integer> tb1Map,
			Map<NonTerminal, Integer> tb2Map, List<IConstituent<?>> cs1,
			List<IConstituent<?>> cs2) {
		for(int j = 0; j < cs1.size(); j++) {
			IConstituent<?> c1 = cs1.get(j);
			IConstituent<?> c2 = cs2.get(j);
			Assert.assertEquals(c1.getBeginIndex(), c2.getBeginIndex());
			Assert.assertEquals(c1.getEndIndex(), c2.getEndIndex());
			Collection<NonTerminal> nts1 = c1.getNonTerminals();
			Collection<NonTerminal> nts2 = c2.getNonTerminals();
			List<Integer> map1 = new ArrayList<Integer>();
			for (NonTerminal nt : nts1) {
				Integer value = tb1Map.get(nt);
				if(value == null) {
					value = tb1Map.size();
					tb1Map.put(nt, value);
				}
				map1.add(value);
			}
			List<Integer> map2 = new ArrayList<Integer>();
			for (NonTerminal nt : nts2) {
				Integer value = tb2Map.get(nt);
				if(value == null) {
					value = tb2Map.size();
					tb2Map.put(nt, value);
				}
				map2.add(value);
			}
			Assert.assertEquals(map1, map2);
		}
	}
	
	public boolean compareFiles(String fileName, String fileName2) throws IOException {
		//if(true) return true;
		BufferedReader j = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
		BufferedReader c = new BufferedReader(new InputStreamReader(new FileInputStream(fileName2), "UTF-8"));
		String jString, cString;
		boolean toReturn = true;
		do {
			jString = j.readLine();
			while (jString != null && jString.startsWith("#")) {
				jString = j.readLine();
			}
			cString = c.readLine();
			while (cString != null && cString.startsWith("#")) {
				cString = c.readLine();
			}
			if (!(jString == null || cString == null)) {
				if (!jString.startsWith("#")) {
					if (jString.compareTo(cString) != 0) {
						System.out.println("Not equal: ");
						System.out.println("J: " + jString);
						System.out.println("C: " + cString);
						toReturn = false;
					}
				}
			}
			if ((jString == null && cString != null) || (jString != null && cString == null)) {
				return false;
			}
		} while (jString != null && cString != null);
		return toReturn;
	}
	
}
