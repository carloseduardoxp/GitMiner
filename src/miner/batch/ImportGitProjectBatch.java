package miner.batch;

import java.util.ArrayList;
import java.util.List;

import miner.model.domain.Project;
import miner.model.service.ImportGitProjectService;

public class ImportGitProjectBatch implements miner.model.service.Observer {

	//  -Xms2048m -Xmx4096m
	public static void main(String[] args) throws Exception {
		ImportGitProjectBatch batch = new ImportGitProjectBatch();
		boolean downloadAgain = false;
		boolean importOnlyMasterBranch = true;
		List<Project> projects = new ArrayList<>();
//		projects.add(new Project("Commons-Lang","https://github.com/apache/commons-lang.git"));
//		projects.add(new Project("Commons-IO","https://github.com/apache/commons-io.git"));
//		projects.add(new Project("Commons-Collections","https://github.com/apache/commons-collections.git"));
//		projects.add(new Project("Commons-Logging","https://github.com/apache/commons-logging.git"));
//		projects.add(new Project("JUnit","https://github.com/junit-team/junit4.git"));
//		projects.add(new Project("Mockito","https://github.com/mockito/mockito.git"));		
//		projects.add(new Project("Struts","https://github.com/apache/struts.git"));
//		projects.add(new Project("JasperReports","https://github.com/Jaspersoft/jasperreports.git"));
//		projects.add(new Project("Log4j","https://github.com/apache/log4j.git"));
//		projects.add(new Project("Undertow","https://github.com/undertow-io/undertow.git"));		
//		projects.add(new Project("Maven","https://github.com/apache/maven.git"));		
//		projects.add(new Project("VRaptor","https://github.com/caelum/vraptor.git"));
//		projects.add(new Project("HBase","https://github.com/apache/hbase.git"));				
//		projects.add(new Project("Ant","https://github.com/apache/ant.git"));					
//		projects.add(new Project("Tomcat","https://github.com/apache/tomcat.git"));
	//	projects.add(new Project("Hibernate","https://github.com/hibernate/hibernate-orm.git"));
		
		
		projects.add(new Project("Jetty","https://github.com/eclipse/jetty.project.git"));		
		projects.add(new Project("SpringFramework","https://github.com/spring-projects/spring-framework.git"));		
		projects.add(new Project("Gradle","https://github.com/gradle/gradle.git"));		
		projects.add(new Project("Wildfly","https://github.com/wildfly/wildfly.git"));
		projects.add(new Project("Cassandra","https://github.com/apache/cassandra.git"));	
		projects.add(new Project("Spring-Boot","https://github.com/spring-projects/spring-boot.git"));
				
		
		for (Project project: projects) {
			System.out.println("Tentando importar o projeto "+project.getName());
			ImportGitProjectService service = new ImportGitProjectService(batch, project, downloadAgain, importOnlyMasterBranch);
			service.execute();
		}

	}

	@Override
	public void sendStatusMessage(String message) {
		System.out.println(message);
		
	}

	@Override
	public void sendCurrentPercent(Integer percent) {
		System.out.println(percent);
		
	}

}
