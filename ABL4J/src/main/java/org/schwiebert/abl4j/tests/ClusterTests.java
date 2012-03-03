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

import org.junit.AfterClass;
import org.junit.Test;
import org.schwiebert.abl4j.cluster.Main;



public class ClusterTests extends AbstractTest {

	static FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith("_c.txt");
		}
		
	};
	
	private static int overallJavaTime;
	private static int overallCppTime;
	
	public ClusterTests() {
		super(new File("testdata/aligned"), new File("testdata/clustered"), "clustered", filter);
	}
	
	@Override
	protected void runJavaProgram(String[] args) throws IOException {
		Main.main(args);
	}

	@Test
	public void testClustering() throws IOException {
		String cCmd = "orig/abl_cluster ";
		super.runProgram(cCmd, "", 0);
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
	
}
