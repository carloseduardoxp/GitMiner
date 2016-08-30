package miner.model.dao.structure.xml;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import miner.util.exception.ConnectionException;

@XmlRootElement(name = "databaseOperations")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseOperation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String XmlSqlFile = System.getProperty("user.dir")+"/script/SqlQueries.xml";

    private static DatabaseOperation databaseOperation;

    @XmlElement(name = "sqls")
    private List<TypeSql> typesSql;

    public List<TypeSql> getTypesSql() {
        return typesSql;
    }

    public void setTypesSql(List<TypeSql> tipos) {
        this.typesSql = tipos;
    }
    
    public static String getSql(TypeQuery typeQuery, String queryName) throws ConnectionException {
        try {
            return getDatabaseOperation().getQuery(typeQuery.getType(), queryName);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new ConnectionException("Error on search query " + typeQuery + " " + queryName + " "+ e.getMessage(), DatabaseOperation.class.getName());
        }
    }

    private String getQuery(String type, String queryName) throws ConnectionException {
        for (TypeSql typeSql : typesSql) {
            if (typeSql.getType().equals(type)) {
                for (Query sql : typeSql.getQueries()) {
                    if (sql.getName().equals(queryName)) {
                        return sql.getQuery();
                    }
                }
            }
        }
        throw new ConnectionException("Cant find query " + queryName + " of type " + type, DatabaseOperation.class.getName());
    }   

    private static DatabaseOperation getDatabaseOperation() throws JAXBException {
        if (databaseOperation == null) {
            JAXBContext context = JAXBContext.newInstance(new Class[]{DatabaseOperation.class});
            Unmarshaller unmarshaller = context.createUnmarshaller();
            databaseOperation = (DatabaseOperation) unmarshaller.unmarshal(new File(XmlSqlFile));
        }
        return databaseOperation;
    }

}
