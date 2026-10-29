package ma.emsi.elachab.tp1.tp1_zakaria.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.elachab.tp1.tp1_zakaria.llm.JsonAdapterPourGemini;
import ma.emsi.elachab.tp1.tp1_zakaria.llm.LlmInteraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Backing bean pour la page JSF index.xhtml.
 * Portée view pour conserver l'état de la conversation qui dure pendant plusieurs requêtes HTTP.
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    // --- Ajout des propriétés pour le mode Debug (TP1) ---
    private boolean debug;
    private String texteRequeteJson;
    private String texteReponseJson;

    /**
     * Service pour manipuler le JSON et communiquer avec l'API Gemini. (Remplace GenerateurReponse)
     */
    @Inject
    private JsonAdapterPourGemini jsonAdapter;

    @Inject
    private FacesContext facesContext;

    public Bb() {
    }

    public String getRoleSysteme() { return this.roleSysteme; }
    public void setRoleSysteme(String roleSysteme) { this.roleSysteme = roleSysteme; }

    public boolean isRoleSystemeChangeable() { return this.roleSystemeChangeable; }

    public String getQuestion() { return this.question; }
    public void setQuestion(String question) { this.question = question; }

    public String getReponse() { return this.reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public String getConversation() { return conversation.toString(); }
    public void setConversation(String conversation) { this.conversation = new StringBuilder(conversation); }

    // --- Getters et Setters pour le mode Debug ---
    public boolean isDebug() { return debug; }
    public void setDebug(boolean debug) { this.debug = debug; }

    public String getTexteRequeteJson() { return texteRequeteJson; }
    public void setTexteRequeteJson(String texteRequeteJson) { this.texteRequeteJson = texteRequeteJson; }

    public String getTexteReponseJson() { return texteReponseJson; }
    public void setTexteReponseJson(String texteReponseJson) { this.texteReponseJson = texteReponseJson; }


    /**
     * Envoie la question à l'API Gemini via le JsonAdapter.
     * @return null pour rester sur la même page.
     */
    public String envoyer() {
        if (this.question == null || this.question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        // Si c'est le début de la conversation, on transmet le rôle système et on bloque la liste déroulante
        if (this.conversation.isEmpty()) {
            this.jsonAdapter.setSystemRole(this.roleSysteme);
            this.roleSystemeChangeable = false;
        }

        // Appel à l'API du LLM (Code fourni dans l'énoncé)
        try {
            LlmInteraction interaction = jsonAdapter.envoyerRequete(this.question);
            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

            afficherConversation();
        } catch (Exception e) {
            this.reponse = "Erreur : " + e.getMessage();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            facesContext.addMessage(null, message);
        }

        return null;
    }

    /**
     * Pour un nouveau chat.
     * L'utilisation de faces-redirect=true est cruciale pour détruire le bean ViewScoped et réinitialiser la conversation.
     */
    public String nouveauChat() {
        return "index.xhtml?faces-redirect=true";
    }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();
            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));

            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
            role = """
                    You are an aggressive fitness coach. Answer the user's questions, but always yell at them to do push-ups and stay disciplined.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Coach Sportif Agressif"));
        }

        return this.listeRolesSysteme;
    }
}