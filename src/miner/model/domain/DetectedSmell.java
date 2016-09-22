package miner.model.domain;

public class DetectedSmell {

    private Integer id;

    private SmellEnum smell;
    
    private String obs;

    private ClassCommitChange change;

    public DetectedSmell() {
    }    

    public DetectedSmell(SmellEnum smell, ClassCommitChange change,String obs) {
        this.smell = smell;
        this.change = change;
        this.obs = obs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SmellEnum getSmell() {
        return smell;
    }

    public void setSmell(SmellEnum smell) {
        this.smell = smell;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public ClassCommitChange getChange() {
        return change;
    }

    public void setChange(ClassCommitChange change) {
        this.change = change;
    }

	@Override
	public String toString() {
		return "DetectedSmell [id=" + id + ", smell=" + smell + ", obs=" + obs + ", change=" + change.getJavaClass().getId()+" - "+change.getCommitChange().getId() + "]";
	}

    
   
  

}
