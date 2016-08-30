package sad.codesmell.detection.repository.MessageChains;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IElement;
import padl.kernel.IEntity;
import padl.kernel.IField;
import padl.kernel.IGetter;
import padl.kernel.IInterface;
import padl.kernel.IMethod;
import padl.kernel.IParameter;
import padl.kernel.ISetter;
import padl.util.Util;
import pom.metrics.IUnaryMetric;
import pom.metrics.MetricsRepository;
import sad.codesmell.property.impl.FieldProperty;
import sad.codesmell.property.impl.InterfaceProperty;
import sad.codesmell.property.impl.MethodProperty;
import sad.codesmell.property.impl.MetricProperty;
import sad.codesmell.property.impl.SemanticProperty;
import sad.codesmell.property.impl.ClassProperty;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.AbstractCodeSmellDetection;
import sad.kernel.impl.CodeSmell;
import sad.util.BoxPlot;
import util.lang.Modifier;
import util.io.ProxyConsole;

/**
 * This class represents the detection of the code smell <CODESMELL>
 * 
 * @author Auto generated
 *
 */


public class MessageChainsClassDetection extends AbstractCodeSmellDetection implements ICodeSmellDetection {

	
	
	public String getName() {
		return "MessageChainsClassDetection";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set classesMessageChainsClass = new HashSet();
		
		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				final double NOTI = ((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOTI")).compute(anAbstractLevelModel, aClass);
				
				if (NOTI >= 4.0) {
					try {
						CodeSmell dc = new CodeSmell(
							"MessageChainsClass", "", 
							new ClassProperty(aClass));
						
						HashMap thresholdMap = new HashMap();
						thresholdMap.put("NOTI", new Double(4.0));
						
						dc.getClassProperty().addProperty(new MetricProperty("NOTI", 
							((IUnaryMetric) MetricsRepository.getInstance().getMetric("NOTI")).compute(anAbstractLevelModel, aClass), thresholdMap));
						
						classesMessageChainsClass.add(dc);
					}
					catch (final Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(ProxyConsole.getInstance().errorOutput());
					}
				}
			}
		}

		this.setSetOfSmells(classesMessageChainsClass);
		// return classesMessageChainsClass;

	}
	
	
}
