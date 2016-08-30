package miner.util.exception;

public class ConnectionException extends Exception {

    private String classe;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ConnectionException(String message, String classe, Throwable cause) {
        super(message, cause);
        this.classe = classe;
    }

    public ConnectionException(String message, String classe) {
        super(message);
        this.classe = classe;
    }

    public String getClasse() {
        return classe;
    }

}
