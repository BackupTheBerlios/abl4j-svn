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
package org.schwiebert.abl4j.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.align.Predicate;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.random.MersenneTwister;
import org.schwiebert.abl4j.random.NativeRandom;
import org.schwiebert.abl4j.select.OverlapInTree;


/**
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de)
 * 
 */
public class Tools {

	public static final String VERSION = "0.9.5";
	
	private static InheritableThreadLocal<Random> threadLocalRandom = new InheritableThreadLocal<Random>();
	
	private static InheritableThreadLocal<Boolean> threadLocalCompatibility = new InheritableThreadLocal<Boolean>();
	
	private static Logger logger = Logger.getLogger(Tools.class);
	

	public static void configure(PropertiesMap properties) {
		threadLocalCompatibility.set(properties.getBoolean(AblProperties.COMPATIBILITY_MODE));
		logger.debug("Compatibility Mode " + (threadLocalCompatibility.get() ? "on" : "off"));
		if(threadLocalCompatibility.get()) {
			threadLocalRandom.set(new NativeRandom());
		} else {
			threadLocalRandom.set(new MersenneTwister());
		}
		int seed = properties.getInteger(AblProperties.SEED);
		if(seed == -1) seed = 0;
		setSeed(seed);
	}
	
	/**
	 * If true, the application is run in compatibilityMode. This means that
	 * results of ABL and ABL4J should always be identical. If false, the results
	 * might not be identical, but ABL4J will run faster. See ABL4J docs for 
	 * details.
	 * 
	 * @return
	 */
	public static boolean compatibilityMode() {
		return threadLocalCompatibility.get();
	}



	@SuppressWarnings("unchecked")
	public static List copyIf(List list, int begin, int end, Predicate p) {
		// This procedure copies all elements for which p is true from first
		// to last into res. This procedure is defined in the Bjarne
		// Stroustrup C++ book.
		List toReturn = new ArrayList();
		for (int i = begin; i < end; i++) {
			if (p.matches(list.get(i))) {
				toReturn.add(list.get(i));
			}
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public static List removeIf(List list, int begin, int end, Predicate p) {
		List toRemove = new ArrayList();
		for (int i = begin; i < end; i++) {
			if (p.matches(list.get(i))) {
				toRemove.add(list.get(i));
			}
		}
		//System.out.println("Returning: " + toRemove.size());
		return toRemove;
	}
	
	
	/**
	 * OutputIterator remove_copy_if(InputIterator first, InputIterator
	 * last, OutputIterator result, Predicate pred);
	 * 
	 * Remove_copy_if copies elements from the range [first, last) to a
	 * range beginning at result, except that elements for which pred is
	 * true are not copied. The return value is the end of the resulting
	 * range. This operation is stable, meaning that the relative order of
	 * the elements that are copied is the same as in the range [first,
	 * last).
	 */
	public static void removeCopyIf(ITree<?> t, Vector<IConstituent<?>> overlap) {
		int begin = 0;
		int end = t.getConstituentStructure().size();
		OverlapInTree overlapInTree = new OverlapInTree(t);
		for (int i = begin; i < end; i++) {
			IConstituent<?> c = t.getConstituentStructure().get(i);
			if (overlapInTree.matches(c)) {
				overlap.add(c);
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	public static int findIf(List list, int begin, int end, Predicate p) {
		for (int i = begin; i < end && i < list.size(); i++) {
			if (p.matches(list.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param seed
	 */
	public static void setSeed(int seed) {
		threadLocalRandom.get().setSeed(seed);
	}
	
	/**
	 * @param n
	 * @return
	 */
	public static int random(int n) {
		return threadLocalRandom.get().nextInt(n);
	}

	
	/**
	 * For testing purpose only.
	 * @return
	 */
	public static Random getRandom() {
		return threadLocalRandom.get();
	}
	

}
