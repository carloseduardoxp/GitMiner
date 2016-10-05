package miner.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import miner.model.domain.Project;
import miner.model.service.ImportClassesService;

public class ImportClassesBatch implements miner.model.service.Observer {

	//  -Xms2048m -Xmx4096m
	public static void main(String[] args) throws Exception {
		ImportClassesBatch batch = new ImportClassesBatch();
		List<String> filters = Arrays.asList("/test/","/examples/","/example/");

		List<Project> projects = new ArrayList<>();
		
		projects.add(new Project(3,"Commons-Lang","https://github.com/apache/commons-lang.git"));
		projects.add(new Project(4,"Commons-IO","https://github.com/apache/commons-io.git"));				
		projects.add(new Project(5,"Commons-Collections","https://github.com/apache/commons-collections.git"));
		projects.add(new Project(6,"Commons-Logging","https://github.com/apache/commons-logging.git"));
		
//		projects.add(new Project(7,"JUnit","https://github.com/junit-team/junit4.git"));
//		projects.add(new Project(8,"Mockito","https://github.com/mockito/mockito.git"));		
//		projects.add(new Project(10,"Struts","https://github.com/apache/struts.git"));
//		projects.add(new Project(11,"JasperReports","https://github.com/Jaspersoft/jasperreports.git"));		
//		projects.add(new Project(13,"Undertow","https://github.com/undertow-io/undertow.git"));		
//		projects.add(new Project(16,"Maven","https://github.com/apache/maven.git"));		
//		projects.add(new Project(19,"VRaptor","https://github.com/caelum/vraptor.git"));						
//		projects.add(new Project(27,"Ant","https://github.com/apache/ant.git"));					
//		projects.add(new Project(28,"Tomcat","https://github.com/apache/tomcat.git"));		
//		projects.add(new Project(12,"Log4j","https://github.com/apache/log4j.git"));		
//				
//		projects.add(new Project(24,"HBase","https://github.com/apache/hbase.git"));
//		
//		projects.add(new Project("Jetty","https://github.com/eclipse/jetty.project.git"));		
//		projects.add(new Project("SpringFramework","https://github.com/spring-projects/spring-framework.git"));
//		projects.add(new Project("Spring-Boot","https://github.com/spring-projects/spring-boot.git"));
//		projects.add(new Project("Gradle","https://github.com/gradle/gradle.git"));		
//		projects.add(new Project("Spring-Boot","https://github.com/spring-projects/spring-boot.git"));
//		projects.add(new Project("Wildfly","https://github.com/wildfly/wildfly.git"));
//		projects.add(new Project("Cassandra","https://github.com/apache/cassandra.git"));	
//		projects.add(new Project("Hibernate","https://github.com/hibernate/hibernate-orm.git"));
				
		
		for (Project project: projects) {
			System.out.println("Tentando importar as classes do projeto "+project.getName());
			ImportClassesService service = new ImportClassesService(batch, project,filters);
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
