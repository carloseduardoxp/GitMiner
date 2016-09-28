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
			if (change.getChangeType() == ChangeType.ADD || change.getChangeType() == ChangeType.MODIFY) {
				for (ClassCommitChange ccc: change.getClassCommitchange()) {
					if (ccc.getJavaClass().isAnalyse()) {
						removeClass(ccc.getJavaClass().getName());
						changes.add(ccc);
						if (!ccc.getJavaClass().isContentEnum() && !ccc.getJavaClass().isContentInterface()) {
							isChangeAnyone = true;	
						}						
					}
 				}
			} else if (change.getChangeType() == ChangeType.DELETE) {
				for (ClassCommitChange ccc: change.getClassCommitchange()) {	
					boolean isRemoved = removeClass(ccc.getJavaClass().getName());		
					if (isRemoved) {
						isChangeAnyone = true;
					}
 				}
				//TODO other types RENAMED, ETC.. 
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
	
	private boolean removeClass(String name) {
		for (ClassCommitChange ccc: changes) {
			if (ccc.getJavaClass().getName().equals(name)) {
				return changes.remove(ccc);
			}
		}
		return false;
	}

	public String[] getPaths() {
		List<String> paths = new ArrayList<String>();
		for (ClassCommitChange commitChange: changes) {
			paths.add(commitChange.getCommitChange().getLocalPath());
		}		
		return paths.stream().toArray(String[]::new);
	}

}
