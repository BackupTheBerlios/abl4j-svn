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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Test;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.NonTerminal;
import org.schwiebert.abl4j.select.Main;



public class SelectTests extends AbstractTest {
	
	private static int counter = 0;
	
	private static int overallJavaTime;
	private static int overallCppTime;

	static FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith("_c.txt");
		}
		
	};
	
	public SelectTests() {
		super(new File("testdata/clustered"), new File("testdata/selected"), "selected", filter);
	}
	
	@Override
	protected void runJavaProgram(String[] args) throws IOException {
		Main.main(args);
	}

	@Test
	public void testFirstSelection() throws IOException {
		String cCmd = "orig/abl_select ";
		counter = runProgram(cCmd, "-s f", counter);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@Test
	public void testTermSelection() throws IOException {
		String cCmd = "orig/abl_select ";
		counter = runProgram(cCmd, "-s t", counter);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@Test
	public void testConstSelection() throws IOException {
		String cCmd = "orig/abl_select ";
		counter = runProgram(cCmd, "-s c", counter);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println("Total Java Time: " + overallJavaTime);
		System.out.println("Total C++ Time : " + overallCppTime);
		System.out.println("(Please note that C++ version would probably be executed faster if not called from java code.)");
	}

	@Override
	protected void compareConstituents(Map<NonTerminal, Integer> tb1Map,
			Map<NonTerminal, Integer> tb2Map, List<IConstituent<?>> cs1,
			List<IConstituent<?>> cs2) {
		
	}

	@Override
	public boolean compareFiles(String fileName, String fileName2)
			throws IOException {
		return super.compareTreeBanks(fileName, fileName2);
	}
	
	
	
}
