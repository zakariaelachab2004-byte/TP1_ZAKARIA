package ma.emsi.elachab.tp1.tp1_zakaria.llm;

/**
 * Exception levée si une requête envoyée à l'API du LLM échoue.
 */
public class RequeteException extends Exception {
    private int status;
    private String requeteJson;

    public RequeteException(int status) {
        this.status = status;
    }

    public RequeteException(String message) {
        super(message);
    }

    public RequeteException(String message, String requeteJson) {
        super(message);
        this.requeteJson = requeteJson;
    }

    public RequeteException() {
    }

    public int getStatus() {
        return status;
    }

    public String getRequeteJson() {
        return requeteJson;
    }
}