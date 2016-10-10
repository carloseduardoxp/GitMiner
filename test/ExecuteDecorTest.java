import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import miner.model.domain.Metric;
import miner.model.domain.SmellEnum;
import miner.util.exception.ValidationException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.impl.Factory;
import pom.metrics.IUnaryMetric;
import pom.metrics.MetricsRepository;
import sad.designsmell.detection.IDesignSmellDetection;
import sad.kernel.ICodeSmell;
import sad.kernel.impl.CodeSmell;
import sad.kernel.impl.CodeSmellComposite;
import sad.kernel.impl.DesignSmell;

public class ExecuteDecorTest {
	
	private static List<SmellEnum> smells = Arrays.asList(SmellEnum.values());
	
	public static void main(String args[]) throws Exception {
		IIdiomLevelModel iIdiomLevelModel = analyseCodeLevelModelFromJavaSourceFiles("C:\\Mestrado\\test","execution");
		getMetrics(iIdiomLevelModel);
		List<SmellTest> smells = analyseCodeLevelModel(iIdiomLevelModel);
		System.out.println("Smells");
		for (SmellTest smell: smells) {
			System.out.println(smell);
		}
	}
	
	public static IIdiomLevelModel analyseCodeLevelModelFromJavaSourceFiles(String path, String name) throws Exception {		
		final CompleteJavaFileCreator creator = new CompleteJavaFileCreator(new String[] { path }, new String[] { "" },
				new String[] { path });
		final ICodeLevelModel codeLevelModel = Factory.getInstance().createCodeLevelModel(name);
		codeLevelModel.create(creator);
		final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator2);

		final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator1);

		return (IIdiomLevelModel) new AACRelationshipsAnalysis()
				.invoke(codeLevelModel);
	}

    public static List<SmellTest> analyseCodeLevelModel(final IIdiomLevelModel idiomLevelModel) throws Exception {
    	List<SmellTest> smellArquivo = new ArrayList<>();
        try {
            for (int i = 0; i < smells.size(); i++) {
                final String antipatternName = smells.get(i).toString();

                final Class<?> detectionClass = Class
                        .forName("sad.designsmell.detection.repository."
                                + antipatternName + '.' + antipatternName
                                + "Detection");
                final IDesignSmellDetection detection = (IDesignSmellDetection) detectionClass
                        .newInstance();

                detection.detect(idiomLevelModel);                 
                for (Object s : detection.getDesignSmells()) {
                    DesignSmell ds = (DesignSmell) s;
                    for (Object o : ds.listOfCodeSmells()) {
                        ICodeSmell cs = (ICodeSmell) o;
                        SmellTest sa = processa(cs, ds.getName());
                        if (sa != null) {
                            smellArquivo.add(sa);
                        }
                    }
                }                
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return smellArquivo;
    }

    private static SmellTest processa(ICodeSmell cs, String name) throws Exception {
        if (cs instanceof CodeSmell) {
            return new SmellTest(SmellEnum.getSmellName(name),cs.getIClass().getDisplayID(),cs.toString() );
        } else if (cs instanceof CodeSmellComposite) {
            CodeSmellComposite co = (CodeSmellComposite) cs;
            for (Object o : co.getSetOfCodeSmellsOfGeneric()) {
                ICodeSmell ics = (ICodeSmell) o;
                return processa(ics, name);
            }
        }
        throw new Exception("Cant find cs instance " + cs);
    }       
    
    private static void getMetrics(IIdiomLevelModel iIdiomLevelModel) throws ValidationException {
		final Iterator iter = iIdiomLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				System.out.println("Class "+aClass.getDisplayID());
				for (Metric metric: Metric.values()) {	
					System.out.println(metric);
					System.out.println("Metric: "+metric+" - "+((IUnaryMetric) MetricsRepository.getInstance().getMetric(metric.name())).compute(iIdiomLevelModel, aClass));
				}
			}
		}

	}
    

}

class SmellTest {
	private SmellEnum smell;
	
	private String fileName;
	
	private String text;

	public SmellTest(SmellEnum smell, String fileName, String text) {
		super();
		this.smell = smell;
		this.fileName = fileName;
		this.text = text;
	}

	public SmellEnum getSmell() {
		return smell;
	}

	public void setSmell(SmellEnum smell) {
		this.smell = smell;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "SmellTest [smell=" + smell + ", fileName=" + fileName + ", text=" + text + "]";
	}
	
	
	
}
