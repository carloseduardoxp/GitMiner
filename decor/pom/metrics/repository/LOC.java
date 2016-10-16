/*******************************************************************************
 * Copyright (c) 2001-2014 Yann-Gaël Guéhéneuc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Yann-Gaël Guéhéneuc and others, see in file; API and its implementation
 ******************************************************************************/
package pom.metrics.repository;

import java.util.Iterator;
import padl.kernel.IAbstractModel;
import padl.kernel.IClass;
import padl.kernel.IConstructor;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethod;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;
import util.io.ProxyConsole;
import util.lang.Modifier;

public class LOC extends AbstractMetric implements IMetric, IUnaryMetric {
	protected double concretelyCompute(
		final IAbstractModel anAbstractModel,
		final IFirstClassEntity anEntity) {

		int loc = 0;

		if (anEntity instanceof IClass) {
			final IClass clazz = (IClass) anEntity;
			final Iterator iteratorOnFields = 
					clazz.getIteratorOnConstituents(IField.class);
			while (iteratorOnFields.hasNext()) {
				final IField field = (IField) iteratorOnFields.next();
				loc++;
			}
			
			final Iterator iteratorOnMethods =
				clazz.getIteratorOnConstituents(IConstructor.class);

			while (iteratorOnMethods.hasNext()) {
				final IConstructor method = (IConstructor) iteratorOnMethods.next();
				
				if (!method.isAbstract()
						&& (method.getVisibility() & Modifier.NATIVE) == 0) {
					final String[] codeLines = method.getCodeLines();
					if (codeLines.length != 0) {
						loc += method.getCodeLines().length;
					}
					else {
						ProxyConsole
							.getInstance()
							.debugOutput()
							.print(this.getClass().getName());
						ProxyConsole
							.getInstance()
							.debugOutput()
							.print(" reports that ");
						ProxyConsole
							.getInstance()
							.debugOutput()
							.print(clazz.getName());
						ProxyConsole.getInstance().debugOutput().print('.');
						ProxyConsole
							.getInstance()
							.debugOutput()
							.print(method.getName());
						ProxyConsole
							.getInstance()
							.debugOutput()
							.println(" has no code lines!");
					}
				}
			}
		}

		return loc;
	}
	public String getDefinition() {
		return "Number of lines of code of all the methods of an entity.";
	}
}
