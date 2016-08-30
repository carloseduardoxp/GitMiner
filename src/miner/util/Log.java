package miner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import miner.model.dao.structure.DaoFactory;
import miner.util.exception.ObjectNotFoundException;
import miner.util.exception.ValidationException;

public class Log {

    private PrintWriter pw;       
    
    private final String path;
    
    private String projectNameTemp;
    
    private String actionTemp;
    
    private static Log log;
    
    SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HHmmss");

    private Log() throws ObjectNotFoundException,ValidationException {
        path = DaoFactory.getConfigurationDao().getConfiguration().getPathLog();
        projectNameTemp = "";
        actionTemp = "";
    }
    
    public synchronized static void writeLog(String projectName,String action,String message) 
            throws ObjectNotFoundException,FileNotFoundException,ValidationException{
        if (log == null) {
            log = new Log();
        }
        if (log.newLog(projectName,action)) {
            if (log.pw != null) {
                log.pw.close();
            }
            log.projectNameTemp = projectName;
            log.actionTemp = action;
            log.createNewFile();
        }
        writeLog(message);
    }
    
    public synchronized static void writeLog(String message) {
        log.pw.println(log.sdf.format(new Date())+"  "+message);
        log.pw.flush();
    }
    
    private void createNewFile() throws FileNotFoundException {
        
        File file = new File(path + "/" + sdf.format(new Date())
                    + projectNameTemp + " - " +actionTemp+ ".txt");
        pw = new PrintWriter(file);
    }
    
    private boolean newLog(String projectName, String action) {
       return !(projectName.equals(projectNameTemp) && action.equals(actionTemp));
    }
    
}
