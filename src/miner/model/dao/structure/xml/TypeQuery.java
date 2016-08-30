package miner.model.dao.structure.xml;

public enum TypeQuery {

    SELECT("select"),
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private String type;

    private TypeQuery(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
