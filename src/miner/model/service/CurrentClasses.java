package miner.model.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import miner.model.domain.ClassCommitChange;
import miner.model.domain.Commit;
import miner.model.domain.CommitChange;

public class CurrentClasses {
	
	private List<ClassCommitChange> changes;
	
	public CurrentClasses() {
		changes = new ArrayList<>();
	}
	
	public boolean addCommit(Commit commit) {
		boolean isChangeAnyone = false;
		for (CommitChange change: commit.getChanges()) {
			if (change.getChangeType() == ChangeType.ADD) {
				for (ClassCommitChange ccc: change.getClassCommitchange()) {
					if (ccc.getJavaClass().isAnalyse()) {
						changes.add(ccc);
						isChangeAnyone = true;
					}
 				}
			} else if (change.getChangeType() == ChangeType.DELETE) {
				for (ClassCommitChange ccc: change.getClassCommitchange()) {					
					changes.remove(ccc);		
					isChangeAnyone = true;
 				}
			} else {
				for (ClassCommitChange ccc: change.getClassCommitchange()) {
					if (ccc.getJavaClass().isAnalyse()) {						
						isChangeAnyone = true;						
					}
 				}
			}
		}
		return isChangeAnyone;
	}
	
	public String[] getPaths() {
		List<String> paths = new ArrayList<String>();
		for (ClassCommitChange commitChange: changes) {
			paths.add(commitChange.getCommitChange().getLocalPath());
		}
		return paths.stream().toArray(String[]::new);
	}

}
