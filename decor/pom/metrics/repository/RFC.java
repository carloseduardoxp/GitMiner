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
package pom.metrics.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import padl.kernel.IAbstractModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class RFC extends AbstractMetric implements IMetric, IUnaryMetric {
	public String getDefinition() {
		return "Response for class: a count of the number of methods of an entity and the number of methods of other entities that are invoked by the methods of the entity.";
	}
	protected double concretelyCompute(
		final IAbstractModel anAbstractModel,
		final IFirstClassEntity anEntity) {

		final Set setOfCalledMethods = new HashSet();
		final Iterator iteratorOnDeclaredMethods =
			super.classPrimitives.listOfDeclaredMethods(anEntity).iterator();
		while (iteratorOnDeclaredMethods.hasNext()) {
			final IOperation operation =
				(IOperation) iteratorOnDeclaredMethods.next();
			final Iterator iteratorOnCalledMethods =
				operation.getIteratorOnConstituents(IMethodInvocation.class);
			while (iteratorOnCalledMethods.hasNext()) {
				final IMethodInvocation methodInvocation =
					(IMethodInvocation) iteratorOnCalledMethods.next();
				final IOperation calledMethod =
					methodInvocation.getCalledMethod();
				if (calledMethod != null) {
					setOfCalledMethods.add(calledMethod.getDisplayName());
				}
			}
		}
		return setOfCalledMethods.size();
	}
}
