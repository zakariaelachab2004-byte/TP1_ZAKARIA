package ma.emsi.elachab.tp1.tp1_zakaria.llm;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

/**
 * Gère l'interface avec l'API de Gemini.
 */
@Dependent
public class LlmClientPourGemini implements Serializable {

    private final String key;
    private Client clientRest;
    private final WebTarget target;

    public LlmClientPourGemini() {
        // Récupère la clé secrète depuis la variable d'environnement (Sécurisé, comme demandé par le prof)
        this.key = System.getenv("GEMINI_KEY");
        this.clientRest = ClientBuilder.newClient();

        // Endpoint REST avec le modèle "gemini-3.5-flash-lite" recommandé dans l'énoncé
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent?key=" + this.key;
        this.target = clientRest.target(url);
    }

    /**
     * Envoie une requête à l'API de Gemini.
     * @param requestEntity le corps de la requête (en JSON).
     * @return réponse REST de l'API.
     */
    public Response envoyerRequete(Entity requestEntity) {
        Invocation.Builder request = target.request(MediaType.APPLICATION_JSON_TYPE);
        return request.post(requestEntity);
    }

    public void closeClient() {
        if (this.clientRest != null) {
            this.clientRest.close();
        }
    }
}