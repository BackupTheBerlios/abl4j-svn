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

import org.apache.log4j.Logger;

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
				/*String ablCommand = cCmd + currentOpts;
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
				*/
				javaTime += (javaEnd - javaStart);
				//cppTime += (cppEnd - cppStart);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return counter;
	}
	
	protected abstract void runJavaProgram(String[] args) throws IOException;


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
						System.out.println(jString);
						System.out.println(cString);
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
