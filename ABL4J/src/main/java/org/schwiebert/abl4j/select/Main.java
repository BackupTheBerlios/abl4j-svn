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
package org.schwiebert.abl4j.select;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.schwiebert.abl4j.ApplicationBase;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.Tools;


public final class Main extends ApplicationBase {

	public static final String PROGRAM_NAME = Select.class.getName();
	
	private int inputHypotheses, outputHypotheses;


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
		initialize(args);
		ITreeBank tb = DataFactory.newTreeBank();
		boolean verbose = properties.getBoolean(AblProperties.VERBOSE);
		IOFactory.getReader(properties).readTreebank(tb);
		inputHypotheses = tb.getNumberOfConstituents();
		Select select = new Select();
		select.configure(properties);
		select.execute(tb);
		outputHypotheses = tb.getNumberOfConstituents();
		if (verbose) {
			System.out.println(PROGRAM_NAME + " : # sentences loaded            : " + tb.size());
		}
		writeInfoheader(tb);
		writeTreebank(tb);
		if (verbose) {
			System.out.println(PROGRAM_NAME + " : # hypotheses loaded           : " + inputHypotheses);
			System.out.println(PROGRAM_NAME + " : # hypotheses selected         : " + outputHypotheses);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeInfoheader(final ITreeBank tb) throws IOException {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ENGLISH);
		tb.addComment(PROGRAM_NAME + " :: execution time :: " + df.format(Calendar.getInstance().getTime()));
		//tb.comments.add(PROGRAM_NAME + " :: command call :: " + options);
		tb.addComment(PROGRAM_NAME + " :: hyps loaded :: " + inputHypotheses);
		tb.addComment(PROGRAM_NAME + " :: hyps generated :: " + outputHypotheses);
	}

	private static void usage() {
		System.out.println("ABL4J Select " + Tools.VERSION + " (C) Stephan Schwiebert 2007.");
		System.out.println("Alignment-Based Learner");
		System.out.println("Usage:" + Main.class.getName());
		System.out.println(" [OPTION]...");
		System.out.println("This program selects from overlapping constituents found in the " + "treebank in the"
				+ "input file. It corresponds to the selection learning phase " + "of the" + "Alignment-Based Learning framework.");
		System.out.println("  -i, --input=FILE   ");
		System.out.println("Name of input file (- means stdin, default)");
		System.out.println("  -o, --output=FILE  ");
		System.out.println("Name of output file (- means stdout, default)");
		System.out.println("  -s, --select=TYPE   ");
		System.out.println("TYPE is one of:");
		System.out.println("                       - first, f:");
		System.out.println("                           earlier learned constituents are " + "                           correct");
		System.out.println("                       - terms, t, leaf, l:");
		System.out.println("                           terms selection method");
		System.out.println("                       - const, c, branch, b:");
		System.out.println("                           const selection method");
		System.out.println("  -m, --preserve_mem ");
		System.out.println("Preserves memory use (and is slower).");
		System.out.println("  -d, --debug        ");
		System.out.println("Output debug information. Not used, please configure log4j instead.");
		System.out.println("  -h, --help         ");
		System.out.println("Show this help and exit");
		System.out.println("  -v, --verbode      ");
		System.out.println("Show details about the selection learning process");
		System.out.println("  -V, --version      ");
		System.out.println("Show version information and exit");
		System.out.println("  -j, --java-compatibility   ");
		System.out.println("Force java compatibility mode. (See ABL4J docs)");
		System.exit(0);
	}

}
