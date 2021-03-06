package sad.designsmell.detection.repository.LongParameterList;

/**
 * This class implements the detection algorithms 
 * of the design smell LongParameterList.
 * 
 * @author Autogenerated files
 * 
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import padl.kernel.IAbstractLevelModel;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.LongParameterList.LongParameterListClassDetection;
import sad.designsmell.detection.IDesignSmellDetection;
import sad.designsmell.detection.repository.AbstractDesignSmellDetection;
import sad.kernel.ICodeSmell;
import sad.kernel.impl.DesignSmell;
import sad.util.OperatorsCodeSmells;
import sad.util.Relationships;

public class LongParameterListDetection extends AbstractDesignSmellDetection implements IDesignSmellDetection {
	private final OperatorsCodeSmells operators;
	private final Relationships relations;

	public LongParameterListDetection() {
		super();

		this.operators = OperatorsCodeSmells.getInstance();
		this.relations = Relationships.getInstance();
	}

	public String getName() {
		return "LongParameterList";
	}

	public String getRuleCardFile() {
		return "../SAD Rules Creator/rsc/LongParameterList.rules";
	}

	public void detect(final IAbstractLevelModel anAbstractLevelModel) {
		final Set candidateDesignSmells = new HashSet();

		final ICodeSmellDetection csLongParameterListClass = new LongParameterListClassDetection();
		csLongParameterListClass.detect(anAbstractLevelModel);
		final Set setLongParameterListClass = ((LongParameterListClassDetection) csLongParameterListClass)
				.getCodeSmells();

		final Iterator iterSet = setLongParameterListClass.iterator();
		while (iterSet.hasNext()) {
			final ICodeSmell aCodeSmell = (ICodeSmell) iterSet.next();
			final DesignSmell designSmell = new DesignSmell(aCodeSmell);
			designSmell.setName("LongParameterList");
			final String definition = "To defined";
			designSmell.setDefinition(definition);
			candidateDesignSmells.add(designSmell);
		}
		this.setSetOfDesignSmells(candidateDesignSmells);
	}
}
