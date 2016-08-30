package miner.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.javafile.eclipse.astVisitors.VisitorFirstParsing;
import padl.creator.javafile.eclipse.astVisitors.VisitorSecondParsing;
import padl.creator.javafile.eclipse.astVisitors.VisitorThirdParsing;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.impl.Factory;
import parser.wrapper.ExtendedASTVisitor;
import parser.wrapper.NamedCompilationUnit;
import pom.metrics.IUnaryMetric;
import pom.metrics.MetricsRepository;

public class SourceCodeAnalyser {

	private static final ASTParser parser;
	
	private SourceCodeAnalyser() {
		
	}

	static {
		parser = ASTParser.newParser(AST.JLS8);
	}
	
	public static IIdiomLevelModel getIdiomLevelModel(char[] sourceCode, String fileName) throws Exception {		
		final ICodeLevelModel codeLevelModel = Factory.getInstance().createCodeLevelModel(fileName);
		createModelFromSource(sourceCode, fileName, codeLevelModel);
		final padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator annotator2 = 
				new padl.creator.javafile.eclipse.astVisitors.LOCModelAnnotator(codeLevelModel);
		parse(sourceCode,fileName,annotator2);

		final padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator annotator1 = 
				new padl.creator.javafile.eclipse.astVisitors.ConditionalModelAnnotator(codeLevelModel);
		parse(sourceCode,fileName,annotator1);

		final IIdiomLevelModel idiomLevelModel = (IIdiomLevelModel) new AACRelationshipsAnalysis().invoke(codeLevelModel);
		return idiomLevelModel;
	}
	
	private static void createModelFromSource(char[] sourceCode, String fileName,final ICodeLevelModel aCodeLevelModel) {

		final VisitorFirstParsing firstParseVisitor = new VisitorFirstParsing(aCodeLevelModel);
		parse(sourceCode,fileName,firstParseVisitor);

		// second visit: inheritance, methods, fields added to the model
		final VisitorSecondParsing secondParseVisitor = new VisitorSecondParsing(aCodeLevelModel);

		parse(sourceCode,fileName,secondParseVisitor);

		// third visit : method invocations added to the model
		final VisitorThirdParsing thirdParseVisitor = new VisitorThirdParsing(aCodeLevelModel);
		parse(sourceCode,fileName,thirdParseVisitor);
	}

	private static ASTNode parseJavaSourceCode(char[] sourceCode, String fileName) {
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setSource(sourceCode);
		parser.setUnitName(fileName);
		final ASTNode node = parser.createAST(null);
		return node;
	}

	private static void parse(char[] sourceCode, String fileName,final ExtendedASTVisitor visitor) {
		ASTNode astNode = parseJavaSourceCode(sourceCode, fileName);
		if (astNode instanceof CompilationUnit) {
			final NamedCompilationUnit namedCU = new NamedCompilationUnit(fileName, (CompilationUnit) astNode);
			namedCU.accept(visitor);
		}
	}
	
	public static void testarMetrica(char[] sourceCode, String fileName) throws Exception {
		IIdiomLevelModel anAbstractLevelModel =getIdiomLevelModel(sourceCode,fileName);
		final Set LargeClassClassesFound = new HashSet();

		final HashMap mapOfLargeClassValues = new HashMap();
		boolean thereIsLargeClass = false;

		final Iterator iter = anAbstractLevelModel.getIteratorOnTopLevelEntities();
		while (iter.hasNext()) {
			final IEntity entity = (IEntity) iter.next();
			//System.out.println(entity);
			if (entity instanceof IClass) {
				final IClass aClass = (IClass) entity;
				thereIsLargeClass = true;

				final double NMD = ((IUnaryMetric) MetricsRepository.getInstance().getMetric("NMD"))
						.compute(anAbstractLevelModel, aClass);
				final double NAD = ((IUnaryMetric) MetricsRepository.getInstance().getMetric("NAD"))
						.compute(anAbstractLevelModel, aClass);
				mapOfLargeClassValues.put(aClass, new Double[] { new Double(NMD + NAD), new Double(0) });
				// final double NMD_NAD = ((IUnaryMetric)
				// MetricsRepository.getInstance().getMetric("NMD_NAD")).compute(anAbstractLevelModel,
				// aClass);
				// mapOfLargeClassValues.put(aClass, new Double(NMD_NAD));
				System.out.println("NMD "+NMD + " "+aClass.getDisplayPath());
				System.out.println("NAD "+NAD + " "+aClass.getDisplayID());
			}
		}		
	}

}
