/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */

package sad.codesmell.detection.repository.FunctionalDecomposition;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IEntity;
import padl.kernel.IMethod;
import sad.codesmell.property.impl.MethodProperty;
import sad.codesmell.property.impl.ClassProperty;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.AbstractCodeSmellDetection;
import sad.kernel.impl.CodeSmell;

import util.io.ProxyConsole;

/**
 * This class represents the detection of the code smell ClassOneMethod
 * @author Auto generated
 */

public class ClassOneMethodDetection extends AbstractCodeSmellDetection implements ICodeSmellDetection {
	
	public String getName() {
		return "ClassOneMethod";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set classClassOneMethod = new HashSet();

		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;

				/*
				 * Why it does not work ? int i =
				 * aClass.getNumberOfConstituents(IMethod.class);
				 * System.out.println("------> " + i + " <---------");
				 */
	
				// We count first the number of methods
				int i = 0;
				final Iterator iterBis = aClass.getIteratorOnConstituents(IMethod.class);
				while (iterBis.hasNext()) {
					final IMethod aMethod = (IMethod) iterBis.next();
					if (!(aMethod.getDisplayName().matches("main"))) {
						i++;
					}
				}
	
				// if there is only one method, we add it as a code smell
				// ClassOneMethod
				if (i == 1) {
					final Iterator iter2 = aClass
							.getIteratorOnConstituents(IMethod.class);
					while (iter2.hasNext()) {
						final IMethod aMethod = (IMethod) iter2.next();
						if (!(aMethod.getDisplayName().matches("main"))) {
							try {
								CodeSmell dc = new CodeSmell(
									"ClassOneMethod", "", 
									new ClassProperty(aClass));
								
								dc.getClassProperty().addProperty(new MethodProperty(aMethod));
								classClassOneMethod.add(dc);
							}
							catch (final Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace(ProxyConsole.getInstance().errorOutput());
							}
							
							
						}
					}
				}
			}
		}
		this.setSetOfSmells(classClassOneMethod);
	}
}
