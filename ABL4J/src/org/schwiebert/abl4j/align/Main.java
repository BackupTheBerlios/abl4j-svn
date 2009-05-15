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

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.schwiebert.abl4j.ApplicationBase;
import org.schwiebert.abl4j.data.DataFactory;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.io.ITreebankReader;
import org.schwiebert.abl4j.io.IOFactory;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.Tools;


public class Main extends ApplicationBase {

	public static final String PROGRAM_NAME = Align.class.getName();
	
	// Default seeds (which are chosen with the -s flag).
	private int seeds[]={0, 76, 44, 68, 66, 43, 82, 34, 48, 32};
	// Default seed value.
	private int seed=0;

	private static void usage() {
		System.out.println("ABL4J Align " + Tools.VERSION + " (C) Stephan Schwiebert 2007.");
		System.out.println("Alignment-Based Learner");
		System.out.print("Usage:" + Main.class.getName());
		System.out.println(" [OPTION]...");
		System.out.print("This program learns structure from the sentences in the input ");
		System.out.println("file. It");
		System.out.print("corresponds to the alignment learning phase of the ");
		System.out.println("Alignment-Based Learning framework");
		System.out.println();
		System.out.print("  -i, --input=FILE   ");
		System.out.println("Name of input file (- means System.in, default)");
		System.out.print("  -o, --output=FILE  ");
		System.out.println("Name of output file (- means System.out, default)");
		System.out.print("  -a, --align=TYPE   ");
		System.out.println("TYPE is one of:");
		System.out.println("                       - wagner_min, wm:");
		System.out.println("                           wagner_fisher edit distance");
		System.out.println("                           default gamma");
		System.out.println("                       - wagner_biased, wb:");
		System.out.println("                           wagner_fisher edit distance");
		System.out.println("                           biased gamma");
//		System.out.println("                       - suffix_tree_1, st1:");
//		System.out.println("                           suffix tree based alignment");
//		System.out.println("                           method 1");
//		System.out.println("                       - suffix_tree_2, st2:");
//		System.out.println("                           suffix tree based alignment");
//		System.out.println("                           method 2");
//		System.out.println("                       - suffix_tree_3, st3:");
//		System.out.println("                           suffix tree based alignment");
//		System.out.println("                           method 3");
//		System.out.println("                       - suffix_tree_4, st4:");
//		System.out.println("                           suffix tree based alignment");
//		System.out.println("                           method 4");
		System.out.println("                       - all, aa, a:");
		System.out.println("                           all possible alignments");
		System.out.println("                       - left, l:");
		System.out.println("                           left branching trees");
		System.out.println("                       - right, r:");
		System.out.println("                           right branching trees");
		System.out.println("                       - both, b:");
		System.out.println("                           right or left branching trees");
		System.out.println("  -p, --part=TYPE    ");
		System.out.println("Part of the sentences that should be used as hypotheses.");
		System.out.println("                      (defaults to unequal)");
		System.out.println("                       - equal, e:");
		System.out.println("                           equal parts");
		System.out.println("                       - unequal, u:");
		System.out.println("                           unequal parts");
		System.out.println("                       - both, b:");
		System.out.println("                           equal and unequal parts");
		// System.out.println( " -s, --seed NUMBER ");
		// System.out.println( "Seed (for the both alignment type)" );
		System.out.println("  -e, --excl_empty   ");
		System.out.println("Do not generate hypotheses that span 0 words");
		System.out.println("  -n, --nomerge      ");
		System.out.println("Do not try to merge hypotheses");
		System.out.println("  -d, --debug        ");
		System.out.println("Output debug information. Not used, please configure log4j instead.");
		System.out.println("  -h, --help         ");
		System.out.println("Show this help and exit");
		System.out.println("  -v, --verbose      ");
		System.out.println("Show details about the aligning learning process");
		System.out.println("  -V, --version      ");
		System.out.println("Show version information and exit");
		System.out.println("  -x, --exhaustive   ");
		System.out.println("Compare exhaustively each possible sentence pair");
		System.out.println("  -j, --java-compatibility   ");
		System.out.println("Force java compatibility mode. (See ABL4J docs)");
		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	private void writeInfoheader(final ITreeBank tb) throws IOException {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ENGLISH);
		tb.addComment(PROGRAM_NAME + " :: execution time :: " + df.format(Calendar.getInstance().getTime()));
		tb.addComment(PROGRAM_NAME + " :: hyps generated :: " + tb.getNumberOfConstituents());
	}

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
		boolean exhaustive = properties.getBoolean(AblProperties.EXHAUSTIVE);
		boolean verbose = properties.getBoolean(AblProperties.VERBOSE);
		long startTime = System.currentTimeMillis();
		Tools.setSeed(seeds[seed]);
		ITreeBank tb = DataFactory.newTreeBank();
		tb.setExhaustive(exhaustive);
		ITreebankReader reader = IOFactory.getReader(properties);
		System.out.println("Using reader " + reader);
		reader.readTreebank(tb);
//		AlignmentMethod alignmentMethod = null;
//		alignmentMethod = (AlignmentMethod) properties.getNewClassInstance(AblProperties.ALIGN_TYPE);
//		alignmentMethod.setProperties(properties);
		Align align = new Align();
		align.configure(properties);
		if (verbose) {
			System.out.println(PROGRAM_NAME + "  : # sentences loaded            : " + tb.size());
		}
		align.execute(tb);
		long endTime = System.currentTimeMillis();
		writeInfoheader(tb);
		writeTreebank(tb);
		if (verbose) {
			System.out.println(PROGRAM_NAME + "  : # hypotheses generated        : " + tb.getNumberOfConstituents());
			System.out.println(PROGRAM_NAME + "  : # seconds execution time      : " + (endTime - startTime) / 1000);
		}
	}

}
