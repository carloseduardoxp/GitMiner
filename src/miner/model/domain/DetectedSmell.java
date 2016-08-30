package miner.model.domain;

public class DetectedSmell {

    private Integer id;

    private SmellEnum smell;
    
    private String obs;

    private ClassCommitChange change;

    public DetectedSmell() {
    }    

    public DetectedSmell(SmellEnum smell, ClassCommitChange change) {
        this.smell = smell;
        this.change = change;
    }

    public DetectedSmell(SmellEnum smell, String obs) {
        this.smell = smell;
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

    
   
  

}
