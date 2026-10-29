package ma.emsi.elachab.tp1.tp1_zakaria.llm;

/**
 * Record Java représentant l'ensemble des données échangées lors d'une interaction avec l'API du LLM.
 *
 * @param questionJson    Le JSON formaté de la requête envoyée.
 * @param reponseJson     Le JSON formaté brut retourné par l'API.
 * @param reponseExtraite Le texte seul de la réponse extrait du JSON.
 */
public record LlmInteraction(
        String questionJson,
        String reponseJson,
        String reponseExtraite
) {}