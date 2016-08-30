package miner.model.domain;

import miner.util.Log;
import miner.util.exception.ValidationException;

public enum FileModificationTypeEnum {

    ADDED("A"),
    COPIED("C"),
    DELETED("D"),
    MODIFIED("M"),
    MERGED("MM"),
    ADDED_MERGED("AA"),
    ADDED_MODIFIED_MERGED("AM"),
    MODIFIED_ADDED_MERGED("MA"),
    MERGED_DELETE("DD"),
    RENAMED("R"),
    TYPE_CHANGED("T"),
    UNMERGED("U"),
    UNKNOWN("X"),
    BROKEN("B"),
    ALL_OR_NONE("*");

    private String character;

    private FileModificationTypeEnum(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }    

    public static FileModificationTypeEnum getType(String character) throws ValidationException {
        for (FileModificationTypeEnum type : FileModificationTypeEnum.values()) {
            if (type.getCharacter().equals(character)) {
                return type;
            }
        }
        Log.writeLog("Cant find character "+character+" in FileModificationType");
        throw new ValidationException("Cant find character "+character+" in FileModificationType");
    }
    
    public static FileModificationTypeEnum getTypeName(String name) throws ValidationException {
        for (FileModificationTypeEnum type : FileModificationTypeEnum.values()) {
            if (type.toString().equals(name)) {
                return type;
            }
        }
        Log.writeLog("Cant find name "+name+" in FileModificationType");
        throw new ValidationException("Cant find name "+name+" in FileModificationType");
    }

}
