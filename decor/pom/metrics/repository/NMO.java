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
/**
 * NMO - Number Of Methods Overridden
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */
package pom.metrics.repository;

import padl.kernel.IAbstractModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NMO extends AbstractMetric implements IMetric, IUnaryMetric {
	protected double concretelyCompute(final IAbstractModel anAbstractModel, final IFirstClassEntity firstClassEntity) {
		return super.classPrimitives
			.listOfOverriddenMethods(firstClassEntity)
			.size();
	}
	public String getDefinition() {
		String def = "Number of methods overridden by an entity.";
		return def;
	}
}
