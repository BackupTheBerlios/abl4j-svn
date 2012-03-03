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

import org.apache.log4j.Logger;
import org.schwiebert.abl4j.IAblComponent;
import org.schwiebert.abl4j.InvalidConfigurationException;
import org.schwiebert.abl4j.data.ITreeBank;
import org.schwiebert.abl4j.select.methods.SelectMethod;
import org.schwiebert.abl4j.util.AblProperties;
import org.schwiebert.abl4j.util.PropertiesMap;


/**
 * This class implements the selection part of ABL.
 * 
 * @author Menno van Zaanen (menno@ics.mq.edu.au) (original C++ Version)
 * @author Jeroen Geertzen (j.geertzen@uvt.nl) (modifications in C++ Version)
 * @author Stephan Schwiebert (sschwieb@spinfo.uni-koeln.de) (Java-Implementation)
 * 
 */
public class Select<T> implements IAblComponent<T>{

	private SelectMethod selectMethod;
			
	public void configure(PropertiesMap properties) throws InvalidConfigurationException {
		try {
			selectMethod = (SelectMethod) properties.getInitializer().getNewClassInstance(AblProperties.SELECT_METHOD);
			selectMethod.configure(properties);
		} catch (Exception e) {
			throw new InvalidConfigurationException(e);
		}
	}
	

	public void execute(ITreeBank<T> tb) {
		if(selectMethod == null) {
			throw new RuntimeException("SelectMethod has not been defined!");
		}
		Logger.getLogger(getClass()).info("Running select");
		selectMethod.select(tb);
		Logger.getLogger(getClass()).info("Select completed");
	}
	
}
