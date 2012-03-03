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
package org.schwiebert.abl4j.random;

import java.util.Random;

public class NativeRandom extends Random {

	private static final long serialVersionUID = 1L;

	static {
		try {
			System.loadLibrary("abl4j_random");
		} catch (UnsatisfiedLinkError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public native int next();

	@Override
	public native void setSeed(long seed);

	@Override
	public int nextInt(int n) {
		return next() % n;
	}

	

	

}
