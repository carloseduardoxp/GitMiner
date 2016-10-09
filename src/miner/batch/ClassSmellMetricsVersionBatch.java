package miner.batch;

import java.util.ArrayList;
import java.util.List;

import miner.model.domain.Project;
import miner.model.service.ClassSmellMetricsVersionService;

public class ClassSmellMetricsVersionBatch implements miner.model.service.Observer {

	//  -Xms2048m -Xmx4096m
	public static void main(String[] args) throws Exception {
		ImportClassesBatch batch = new ImportClassesBatch();

		List<Project> projects = new ArrayList<>();
		
		//projects.add(new Project(3,"Commons-Lang","https://github.com/apache/commons-lang.git"));
		projects.add(new Project(4,"Commons-IO","https://github.com/apache/commons-io.git"));				
		//projects.add(new Project(5,"Commons-Collections","https://github.com/apache/commons-collections.git"));
		projects.add(new Project(6,"Commons-Logging","https://github.com/apache/commons-logging.git"));
		projects.add(new Project(1,"JCommon","https://github.com/facebook/jcommon.git"));
		
		for (Project project: projects) {
			System.out.println("Tentando importar as classes do projeto "+project.getName());
			ClassSmellMetricsVersionService service = new ClassSmellMetricsVersionService(batch, project);
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