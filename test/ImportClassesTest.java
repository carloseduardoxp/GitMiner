import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IEnum;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.IInterface;
import padl.kernel.impl.Factory;

public class ImportClassesTest {
	public static List<String> analyseCodeLevelModelFromJavaSourceFiles(String path, String classPath,String name) throws Exception {		
		final CompleteJavaFileCreator creator = new CompleteJavaFileCreator(new String[] { path }, new String[] { "" },
				new String[] { classPath });
		final ICodeLevelModel codeLevelModel = Factory.getInstance().createCodeLevelModel(name);
		codeLevelModel.create(creator);
		final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator2);

		final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(
				codeLevelModel);
		creator.applyAnnotator(annotator1);

		final IIdiomLevelModel idiomLevelModel = (IIdiomLevelModel) new AACRelationshipsAnalysis()
				.invoke(codeLevelModel);
		return getClassName(idiomLevelModel);
	}

	private static List<String> getClassName(IIdiomLevelModel anAbstractLevelModel) {
		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		List<String> classNames = new ArrayList<>();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			if (entity instanceof IClass) {
				IClass aClass = (IClass) entity;
				//aClass.getLocalPath();
				classNames.add(aClass.getDisplayID());
			} else if (entity instanceof IEnum) {
				IEnum aEnum = (IEnum) entity;
				classNames.add(aEnum.getDisplayID());			
			} else if (entity instanceof IInterface) {
				IInterface aInterface = (IInterface) entity;
				classNames.add(aInterface.getDisplayID());
			} 			
		}
		return classNames;
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(analyseCodeLevelModelFromJavaSourceFiles("C:\\Mestrado\\test\\zookeeper","C:\\Mestrado\\test\\zookeeper","ENUM"));
	}
}
