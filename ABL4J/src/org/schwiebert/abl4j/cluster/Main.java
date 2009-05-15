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
package org.schwiebert.abl4j.cluster;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.PropertyConfigurator;
import org.schwiebert.abl4j.ApplicationBase;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.Tools;


public final class Main extends ApplicationBase {

	public static String PROGRAM_NAME = Cluster.class.getName();

	private StringBuffer options;

	@SuppressWarnings("unchecked")
	private Cluster<? extends NonTerminalsMapping> cluster;
	
	
	public static void main(String[] args) {
		try {
			new Main().run(args);
		} catch (Exception e) {
			e.printStackTrace();
			usage();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void run(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		initialize(args);
		ITreeBank treeBank = DataFactory.newTreeBank();
		boolean verbose = properties.getBoolean(AblProperties.VERBOSE);
		IOFactory.getReader(properties).readTreebank(treeBank);
		int hypotheses = treeBank.getNumberOfConstituents();
		cluster = new Cluster();
		cluster.configure(properties);
		if (verbose) {
			System.out.println(PROGRAM_NAME + ": # sentences loaded            : " + cluster.size());
		}
		cluster.execute(treeBank);
		writeInfoheader(cluster.getTreebank(), args);
		writeTreebank(cluster.getTreebank());
		if (verbose) {
			System.out.println(PROGRAM_NAME + ": # hypotheses loaded           : " + hypotheses);
			int unti = cluster.getNumberOfUniqueNTInput();
			long unto = cluster.getNumberOfUniqueNTOutput();
			System.out.println(PROGRAM_NAME + ": # unique non-terminals input  : " + unti);
			System.out.println(PROGRAM_NAME + ": # unique non-terminals output : " + unto);
			System.out.println(PROGRAM_NAME + ": # hypotheses clustered        : " + (unti - unto) + " (" + (double) (unti - unto) / unti
					+ ")");
			// System.out.println(program_name + ": # seconds execution time : "
			// + (double)(clock()-startTime)/CLOCKS_PER_SEC);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeInfoheader(ITreeBank tb, String[] args) throws IOException {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ENGLISH);
		tb.addComment(PROGRAM_NAME + " :: execution time :: " + df.format(Calendar.getInstance().getTime()));
		tb.addComment(PROGRAM_NAME + " :: command call :: " + options);
		tb.addComment(PROGRAM_NAME + " :: unique NTs in :: "+ cluster.getNumberOfUniqueNTInput());
		tb.addComment(PROGRAM_NAME + " :: unique NTs out :: "	+ cluster.getNumberOfUniqueNTOutput());
	}

	private static void usage() {
		System.out.println("ABL4J Cluster " + Tools.VERSION + " (C) Stephan Schwiebert 2007.");
		System.out.println("Alignment-Based Learner");
		System.out.println("Usage:" + Main.class.getName());
		System.out.println(" [OPTION]...");
		System.out.println("This program clusters hypotheses from the fuzzy trees in the ");
		System.out.println("input file. It");
		System.out.println("corresponds to the cluster phase of the ");
		System.out.println("Alignment-Based Learning framework.");
		System.out.println();
		System.out.println("  -i, --input=FILE   ");
		System.out.println("Name of input file (- means stdin, default)");
		System.out.println("  -o, --output=FILE  ");
		System.out.println("Name of output file (- means stdout, default)");
		System.out.println("  -d, --debug        ");
		System.out.println("Output debug information. Not used, please configure log4j instead.");
		System.out.println("  -h, --help         ");
		System.out.println("Show this help and exit");
		System.out.println("  -v, --verbose      ");
		System.out.println("Show details about the clustering process");
		System.out.println("  -V, --version      ");
		System.out.println("Show version information and exit");
		System.out.println("  -j, --java-compatibility   ");
		System.out.println("Force java compatibility mode. (See ABL4J docs)");
		System.exit(0);
	}
}
