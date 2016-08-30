package miner.model.dao.structure.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sqls")
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeSql implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "type")
    private String type;

    @XmlElement(name = "sql")
    private List<Query> queries = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

}
