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

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class PropertiesMap extends Properties {

	private static final long serialVersionUID = 1878271272136938458L;

	private ABLInitializer initializer;
	
	public PropertiesMap(ABLInitializer initializer) {
		this.initializer = initializer;
	}

	@SuppressWarnings("unchecked")
	public Class getClass(String propertyName) throws ClassNotFoundException {
		String className = super.getProperty(propertyName);
		Class theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
		return theClass;
	}
	
	@SuppressWarnings("unchecked")
	public Object getNewClassInstance(String propertyName) throws Exception {
		Class cls = getClass(propertyName);
		return cls.newInstance();
	}

	public boolean getBoolean(String property) {
		String value = getProperty(property);
		if(value == null) return false;
		return Boolean.parseBoolean(value);
	}
	
	public void put(String key, boolean value) {
		put(key, value + "");
	}
	
	public void put(String key, int value) {
		put(key, value+"");
	}
	

	@SuppressWarnings("unchecked")
	public void logProperties() {
		Logger logger = Logger.getLogger(PropertiesMap.class);
		logger.info("Configuration:");
		Iterator keys = keySet().iterator();
		while(keys.hasNext()) {
			Object k = keys.next();
			logger.info(k + "=" + getProperty((String)k));
		}
	}

	public int getInteger(String property) {
		String value = getProperty(property);
		if(value == null) return -1;
		return Integer.parseInt(value);
	}

	public void setProperty(String key, boolean value) {
		put(key, value);
	}

	public ABLInitializer getInitializer() {
		return initializer;
	}

	
}
