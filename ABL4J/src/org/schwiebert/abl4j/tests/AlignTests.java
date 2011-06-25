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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import org.schwiebert.abl4j.align.Main;


public class AlignTests extends AbstractTest {

	static FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".txt");
		}
		
	};
	
	private static int overallJavaTime;
	private static int overallCppTime;
	
	public AlignTests() {
		super(new File("testdata/input"), new File("testdata/aligned"), "aligned", filter);
	}

	private static int counter = 0;
	
	@Override
	protected void runJavaProgram(String[] args) throws IOException {
		Main.main(args);
	}

	private void runAlignment(String[] align_opts) throws IOException {
		String[] part_opts = { "e", "u", "b" };
		String[] empty = { "", "-e" };
		String[] nomerge = { "", "-n" };
		String cCmd = "orig/abl_align ";
		for (int i = 0; i < align_opts.length; i++) {
			for (int j = 0; j < part_opts.length; j++) {
				for (int x = 0; x < empty.length; x++) {
					for (int z = 0; z < nomerge.length; z++) {
						StringBuffer cmdOpts = new StringBuffer();
						cmdOpts.append("-a " + align_opts[i] + " ");
						cmdOpts.append("-p " + part_opts[j] + " ");
						cmdOpts.append(empty[x] + " ");
						cmdOpts.append(nomerge[z]);
						System.out.println("OPTS: " + cmdOpts);
						counter = runProgram(cCmd, cmdOpts + "", counter);
					}
				}
			}
		}
	}
	
	@Test
	public void testL() throws IOException {
		String[] align_opts = {"l"};
		runAlignment(align_opts);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	
	
	@Test
	public void testT() throws IOException {
		String[] align_opts = {"r"};
		runAlignment(align_opts);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@Test
	public void testB() throws IOException {
		String[] align_opts = {"b"};
		runAlignment(align_opts);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@Test
	public void testA() throws IOException {
		String[] align_opts = {"a"};
		runAlignment(align_opts);
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	private static void doSend(String command,	String server,	String port) throws IOException {
		Socket socket = new Socket(server, Integer.parseInt(port));
		OutputStream os = socket.getOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(os);
		out.write(command.getBytes());
		out.write('\r');
		out.flush();
		socket.close();
	}
	
	public static void startProfiling() {
		try {
			doSend("start", "localhost", "15599");
		} catch (IOException e) {
			Logger.getLogger(AlignTests.class).error("Failed to start profiling - was the profiling service enabled?");
		}
	}
	
	public static void stopProfiling() {
		try {
			doSend("finish", "localhost", "15599");
		} catch (IOException e) {
			Logger.getLogger(AlignTests.class).error("Failed to stop profiling - was the profiling service enabled?");
		}
	}
	
	@Test
	public void testWM() throws IOException {
		String[] align_opts = {"wm"};
		startProfiling();
		runAlignment(align_opts);
		stopProfiling();
		printExecTime();
		overallJavaTime+= javaTime;
		overallCppTime+= cppTime;
	}
	
	@Test
	public void testWB() throws IOException {
		String[] align_opts = {"wb"};
		runAlignment(align_opts);
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
