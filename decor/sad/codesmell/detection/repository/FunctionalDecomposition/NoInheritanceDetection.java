package sad.codesmell.detection.repository.FunctionalDecomposition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IEntity;
import pom.metrics.IUnaryMetric;
import pom.metrics.MetricsRepository;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.AbstractCodeSmellDetection;
import sad.codesmell.property.impl.ClassProperty;
import sad.codesmell.property.impl.MetricProperty;
import sad.kernel.impl.CodeSmell;
import util.io.ProxyConsole;

/**
 * This class represents the detection of the code smell <CODESMELL>
 * 
 * @author Auto generated
 *
 */


public class NoInheritanceDetection extends AbstractCodeSmellDetection implements ICodeSmellDetection {

	
	
	public String getName() {
		return "NoInheritanceDetection";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set classesNoInheritance = new HashSet();
		
		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				final double DIT = ((IUnaryMetric) MetricsRepository.getInstance().getMetric("DIT")).compute(anAbstractLevelModel, aClass);
				
				if (DIT >= 1.0) {
					try {
						CodeSmell dc = new CodeSmell(
							"NoInheritance", "", 
							new ClassProperty(aClass));
						
						HashMap thresholdMap = new HashMap();
						thresholdMap.put("DIT", new Double(1.0));
						
						dc.getClassProperty().addProperty(new MetricProperty("DIT", 
							((IUnaryMetric) MetricsRepository.getInstance().getMetric("DIT")).compute(anAbstractLevelModel, aClass), thresholdMap));
						
						classesNoInheritance.add(dc);
					}
					catch (final Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(ProxyConsole.getInstance().errorOutput());
					}
				}
			}
		}

		this.setSetOfSmells(classesNoInheritance);
		// return classesNoInheritance;

	}
	
	
}
