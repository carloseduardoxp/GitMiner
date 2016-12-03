package miner.model.domain;

public class DetectedSmell {

    private Integer id;

    private SmellEnum smell;
    
    private String text;

    private ClassCommitChange change;

    public DetectedSmell() {
    }    

    public DetectedSmell(SmellEnum smell, ClassCommitChange change,String text) {
        this.smell = smell;
        this.change = change;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ClassCommitChange getChange() {
        return change;
    }

    public void setChange(ClassCommitChange change) {
        this.change = change;
    }

	@Override
	public String toString() {
		return "DetectedSmell [id=" + id + ", smell=" + smell + ", obs=" + text + ", change=" + change.getJavaClass().getId()+" - "+change.getCommitChange().getId() + "]";
	}

    
   
  

}
