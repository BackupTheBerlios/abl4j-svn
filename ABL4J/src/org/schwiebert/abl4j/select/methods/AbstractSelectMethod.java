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
package org.schwiebert.abl4j.select.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.data.IConstituent;
import org.schwiebert.abl4j.data.ITree;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.select.KnowledgeBase;
import org.schwiebert.abl4j.select.OverlapInTree;
import org.schwiebert.abl4j.select.Phrase;
import org.schwiebert.abl4j.select.ProbabilityStore;
import org.schwiebert.abl4j.select.Range;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;
import org.schwiebert.abl4j.util.Tools;

@SuppressWarnings("unchecked")
public abstract class AbstractSelectMethod implements SelectMethod {

	
	public abstract Phrase createPhrase(ITree<?> t, IConstituent<?> cons, int c);
	
	public abstract double computeCombinedProbability(ITree<?> t, Vector<IConstituent<?>> vector, ProbabilityStore prob);
	
	private Logger logger = Logger.getLogger(getClass());
	private boolean preserve;

	private boolean forceCompatibility;
	
	private int numberOfThreads = 1;
	
	
	
	
	public void configure(PropertiesMap properties) {
		preserve = Boolean.parseBoolean(properties.getProperty(AblProperties.PRESERVE));
		forceCompatibility = Boolean.parseBoolean(properties.getProperty(AblProperties.COMPATIBILITY_MODE));
		numberOfThreads = Math.max(1, Integer.parseInt(properties.getProperty(AblProperties.THREADS)));
	}

	public void select(ITreeBank tb) {
		ProbabilityStore store = new ProbabilityStore();
		logger.info("Computing probabilities");
		computeProbabilities(tb, store);
		logger.info("Selecting probabilities");
		selectProbability(tb, store);
	}
	
	private void selectProbability(ITreeBank tb, ProbabilityStore store) {
		long start = System.currentTimeMillis();
		List<SelectThread> threads = new ArrayList<SelectThread>(numberOfThreads);
		for(int i = 0; i < numberOfThreads; i++) {
			SelectThread st = new SelectThread(tb, store, i);
			threads.add(st);
			st.start();
		}
		if(threads.size() == 1) {
			logger.info("Select single-threaded.");
		} else {
			logger.info("Started " + threads.size() + " select threads.");
		}
		for (SelectThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		logger.info("Selection time: " + (end - start) + " ms.");
	}
	
	protected double computeProbability(Phrase p, ProbabilityStore store) {
		double result = -Math.log((double) (store.getCount(p)) / store.getCount(p.second));
		return result;
	}
	
	final class SelectThread extends Thread {
		
		private final ITreeBank tb;
		private final ProbabilityStore store;
		private final int number;

		public SelectThread(ITreeBank tb, ProbabilityStore store, int number) {
			this.tb = tb;
			this.store = store;
			this.number = number;
		}
		
		public void run() {
			final int size = tb.size();
			for (int i = 0; i < size; i++) {
				if(i % numberOfThreads == number) {
					selectProbabilityInTree(tb.get(i), store);
					if (preserve) {
						logger.error("preserve not supported!");
					}
				}
			}
		}
		
	}

	private void selectProbabilityInTree(ITree t, ProbabilityStore store) {
		KnowledgeBase known = new KnowledgeBase();
		Vector<IConstituent<?>> overlap = new Vector<IConstituent<?>>();
		Tools.removeCopyIf(t, overlap);
		List<IConstituent> toRemove = Tools.removeIf(t.getConstituentStructure(), new OverlapInTree(t));
		for (IConstituent constituent : toRemove) {
			t.getConstituentStructure().remove(constituent);
		}
		Vector<IConstituent<?>> res = selectProbabilityInRange(t, overlap, 0, t.size(), known, store);
		for (IConstituent<?> c : res) {
			t.addStructure(c);
		}
	}

	private void computeProbabilities(ITreeBank tb, ProbabilityStore prob) {
		if (!preserve) {
			final int size = tb.size();
			for(int i = 0; i < size; i++) {
				ITree<?> tree = tb.get(i);
				computeProbabilitiesInTree(tree, prob);
			}
		} else {
			logger.error("Preserve not supported!");
		}
	}

	private void computeProbabilitiesInTree(ITree<?> t, ProbabilityStore store) {
		for (int c = 0; c < t.getConstituentStructure().size(); c++) {
			// take only first Nonterminal (should be clustered)
			Phrase p;
			IConstituent<?> cons = t.getConstituentStructure().get(c);
			p = createPhrase(t, cons, c);
			int newProb = store.increase(p);
			cons.setLocalScore(t.getSequenceId(), newProb);
		}
	}
	

	private boolean lessOrEqual(double a, double b) {
		if(forceCompatibility) {
			double result = a - b;
			if (result + 0.000001 < 0) {
				return true;
			}
			if(result < 0.000001) {
				return true;
			}
			return false;
		} else {
			return a <= b;
		}
	}
	
	private boolean less(double a, double b) {
		if(forceCompatibility) {
			double result = a - b;
			result += 0.000001;
			return result < 0;
		} else {
			return a < b;
		}
	}

	private Vector<IConstituent<?>> selectProbabilityInRange(ITree<?> t, Vector<IConstituent<?>> overlap, int begin, int end, KnowledgeBase known,
			ProbabilityStore store) {
		Vector<IConstituent<?>> k = known.get(new Range(begin, end));
		if (k != null) {
			Vector<IConstituent<?>> toReturn = new Vector<IConstituent<?>>();
			toReturn.addAll(k);
			return toReturn;
		}
		if (begin + 1 == end) {
			known.put(new Range(begin, end), new Vector<IConstituent<?>>());
			Vector<IConstituent<?>> v = new Vector<IConstituent<?>>();
			return v;
		}
		int boundary = begin + 1;
		Vector<Vector<IConstituent<?>>> n_best = new Vector<Vector<IConstituent<?>>>();
		n_best.add(new Vector<IConstituent<?>>());
		double best_p = computeCombinedProbability(t, n_best.get(0), store);
		boolean uninit = true;
		double new_p;
		int maxSize = 0;
		while (boundary != end) {
			Vector<IConstituent<?>> res = selectProbabilityInRange(t, overlap, begin, boundary, known, store);
	     	Vector<IConstituent<?>> res2 = selectProbabilityInRange(t, overlap, boundary, end, known, store);
			if (res2 != null) {
				res.addAll(res2);
			}
			new_p = computeCombinedProbability(t, res, store);
			if ( lessOrEqual(new_p, best_p) || (uninit)) {
				if (uninit) {
					best_p = new_p;
					uninit = (best_p < 0);
				}
				if ((new_p >= 0) && (less(new_p, best_p))) {
					n_best.clear();
					maxSize = 0;
					best_p = new_p;
				}
				if (res.size() > maxSize) {
					maxSize = res.size();
				}
				n_best.add(res);
			} 
			boundary++;
		}
		Vector<Vector<IConstituent<?>>> new_best = new Vector<Vector<IConstituent<?>>>();
		final int size = n_best.size();
		for(int j = 0; j < size; j++) {
			Vector<IConstituent<?>> i = n_best.get(j);
			if (i.size() == maxSize) {
				new_best.add(i);
			}
		}
		Vector <IConstituent<?>> best = new Vector<IConstituent<?>>();
		int random = 0;
		if (new_best.size() > 1) {
			random = Tools.random(new_best.size());
		}
		if (new_best.size() > 0) {
			best = new_best.get(random);
		}
		IConstituent<?> beconst = findConstituent(overlap, begin, end);
		if (beconst != null) {
			best.add(beconst);
		}
		Vector<IConstituent<?>> v = new Vector<IConstituent<?>>();
		v.addAll(best);
		known.put(new Range(begin, end), v);
		return best;

	}
	
	private IConstituent<?> findConstituent(Vector<IConstituent<?>> overlap, int begin, int end) {
		for (IConstituent<?> tmp : overlap) {
			if (tmp.getBeginIndex() == begin && tmp.getEndIndex() == end) {
				return tmp;
			}
		}
		return null;
	}
	
}
