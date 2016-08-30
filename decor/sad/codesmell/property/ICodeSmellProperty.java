/*******************************************************************************
 * Copyright (c) 2001-2014 Yann-Ga�l Gu�h�neuc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Yann-Ga�l Gu�h�neuc and others, see in file; API and its implementation
 ******************************************************************************/
package sad.codesmell.property;

/**
 * Interface used to define CodeSmell properties.
 *
 * @author Pierre Leduc
 * @version 1.0
 * @since 2006/05/26
 */
public interface ICodeSmellProperty {

	/**
	 * Return a string representation of the property.
	 * 
	 * @param count A reference number to the current Anti Pattern
	 * @return The string representation of the property
	 */
	public String toString(final int defectCount, final int propertyCount, final String codesmellName);
}
