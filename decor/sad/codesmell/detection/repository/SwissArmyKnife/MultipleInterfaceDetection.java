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

package sad.codesmell.detection.repository.SwissArmyKnife;

/**
 * This class represents the detection of the code smell MultipleInterface
 * 
 * @author Auto generated
 *
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IEntity;
import padl.kernel.IInterface;
import sad.codesmell.property.impl.InterfaceProperty;
import sad.codesmell.property.impl.MethodProperty;
import sad.codesmell.property.impl.ClassProperty;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.AbstractCodeSmellDetection;
import sad.kernel.impl.CodeSmell;

import util.io.ProxyConsole;

/**
 * @author Auto generated
 */

public class MultipleInterfaceDetection extends AbstractCodeSmellDetection implements ICodeSmellDetection {
	private static final int NB_INT = 3;

	public String getName() {
		return "MultipleInterface";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set classesMultipleInterface = new HashSet();

		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				final Set setOfInterfaces = new HashSet();
	
				final Iterator iter2 = aClass.getIteratorOnImplementedInterfaces();
				while (iter2.hasNext()) {
					final Object obj = iter2.next();
	
					if (obj instanceof IInterface) {
						final IInterface iInterface = (IInterface) obj;
						setOfInterfaces.add(new InterfaceProperty(iInterface));
					}
				}
	
				if (setOfInterfaces.size() >= MultipleInterfaceDetection.NB_INT) {
					try {
						CodeSmell dc = new CodeSmell(
							"MultipleInterface", "", 
							new ClassProperty(aClass));
						
						dc.getClassProperty().addProperties(setOfInterfaces);
						classesMultipleInterface.add(dc);
					}
					catch (final Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(ProxyConsole.getInstance().errorOutput());
					}
				}
			}
		}
		this.setSetOfSmells(classesMultipleInterface);
	}
}
