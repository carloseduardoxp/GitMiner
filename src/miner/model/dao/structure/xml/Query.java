package miner.model.dao.structure.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sql")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "query")
    private String query;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
