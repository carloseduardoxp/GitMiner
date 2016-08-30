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

package sad.codesmell.detection.repository.ManyFieldAttributesButNotComplex;

/**
 * This class represents the detection of the code smell FieldPublic
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
import padl.kernel.IField;
import sad.codesmell.property.impl.FieldProperty;
import sad.codesmell.property.impl.MethodProperty;
import sad.codesmell.property.impl.ClassProperty;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.AbstractCodeSmellDetection;
import sad.kernel.impl.CodeSmell;

import util.io.ProxyConsole;

/**
 * @author Auto generated
 */

public class FieldPublicDetection extends AbstractCodeSmellDetection implements ICodeSmellDetection {

	private static final int RATIO_PUBLIC_FIELD = 1;
	
	public String getName() {
		return "FieldPublic";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set classesFieldPublic = new HashSet();

		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				final Set setOfFieldPublic = new HashSet();
				int numField = 0;
				
				final Iterator iter2 = aClass.getIteratorOnConstituents(IField.class);
				while (iter2.hasNext()) {
					numField ++;
					final IField aField = (IField) iter2.next();
				if (aField.isPublic()  && !aField.isFinal()) {
						setOfFieldPublic.add(new FieldProperty(aField));
					}
				}
				
				if (numField > 0 && (setOfFieldPublic.size()*100/numField) >= FieldPublicDetection.RATIO_PUBLIC_FIELD) {
					try {
						ClassProperty classProp = new ClassProperty(aClass);
						classProp.addProperties(setOfFieldPublic);
							
						classesFieldPublic.add(new CodeSmell("FieldPublic", "", classProp));
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(ProxyConsole.getInstance().errorOutput());
					}
				}
			}
		}
		this.setSetOfSmells(classesFieldPublic);
	}	
}
