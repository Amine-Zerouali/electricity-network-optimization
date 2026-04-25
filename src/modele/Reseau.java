package modele;

import java.util.ArrayList;
import java.util.List;

import app.Interface;

/**
 * Classe principale du projet, représentant l'intégralité du réseau électrique.
 * <p>
 * Cette classe agit comme un contrôleur : elle contient les listes de tous les
 * {@link Generateur} et {@link Maison} et gère toutes les opérations métier :
 * <ul>
 * <li>Ajout et mise à jour des composants.</li>
 * <li>Gestion des connexions et déconnexions.</li>
 * <li>Validation de l'état du réseau (capacité, connexions).</li>
 * <li>Calcul des coûts (Dispersion, Surcharge).</li>
 * <li>Affichage de l'état du réseau.</li>
 * </ul>
 * Elle est conçue pour être pilotée par la classe {@link Interface}.
 */
public class Reseau {
	/** Liste de tous les générateurs du réseau. */
    private List<Generateur> generateurs;
    /** Liste de toutes les maisons du réseau. */
    private List<Maison> maisons;
    
    /**
     * Construit un nouveau réseau vide en initialisant les listes.
     */
    public Reseau() {
        this.generateurs = new ArrayList<>();
        this.maisons = new ArrayList<>();
    }

    
    // --- GETTERS ---
    
    /**
     * Récupère la liste des générateurs du réseau.
     * 
     * @return La liste des générateurs présents dans le réseau.
     */
    public List<Generateur> getGenerateurs() {
        return generateurs;
    }

    /**
     * Récupère la liste des maisons du réseau.
     * 
     * @return La liste des maisons présentes dans le réseau.
     */
    public List<Maison> getMaisons() {
        return maisons;
    }

    
    // --- GESTION DES COMPOSANTS ---
    
    /**
     * Ajoute un nouveau générateur ou met à jour un générateur existant.
     * Si un générateur avec le même nom existe déjà, sa capacité maximale est mise à jour.
     * Sinon, un nouveau générateur est créé et ajouté à la liste.
     *
     * @param nom Le nom du générateur.
     * @param capaciteMax La capacité maximale en kW.
     * @return Un message décrivant l'action effectuée.
     * @throws ReseauException Si le nom est déjà pris.
     */
    public String ajouterGenerateur(String nom, double capaciteMax) throws ReseauException {
    	if (chercherMaison(nom) != null) {
    		throw new ReseauException("Impossible : Le nom '" + nom + "' est déjà utilisé par une maison.");
        }
    	
    	Generateur g = chercherGenerateur(nom);
    	
    	if (g != null){
    		double ancienneCap = g.getCapaciteMax();
            g.setCapaciteMax(capaciteMax);
            return String.format("Générateur '%s' mis à jour : %.1fkW -> %.1fkW", nom, ancienneCap, capaciteMax);
    	}
    	else {
    		generateurs.add(new Generateur(nom, capaciteMax));
            return "Générateur '" + nom + "' créé avec succès (" + capaciteMax + "kW).";
    	}
    }
    
    /**
     * Ajoute une nouvelle maison ou met à jour une maison existante.
     * Si une maison avec le même nom existe déjà, son type est mis à jour.
     * <p>
     * <b>Gestion du cache :</b> Si la maison est mise à jour et qu'elle était connectée,
     * cette méthode force la mise à jour de la {@code chargeActuelle} de son générateur
     * en la retirant (ancienne consommation) puis en la rajoutant (nouvelle consommation).
     *
     * @param nom Le nom de la maison.
     * @param type Le {@link TypeMaison} (BASSE, NORMAL, FORTE).
     * @return Un message décrivant l'action effectuée.
     * @throws ReseauException Si le nom est déjà pris.
     */
    public String ajouterMaison(String nom, TypeMaison type) throws ReseauException{
    	if (chercherGenerateur(nom) != null) {
    		throw new ReseauException("Impossible : Le nom '" + nom + "' est déjà utilisé par un générateur.");
        }
    	
    	Maison m = chercherMaison(nom);
    	
    	if (m != null){
    		TypeMaison ancienType = m.getType();
    		Generateur g = m.getGenerateurAssocie();
    		
    		// Retirer la maison du générateur (soustrait l'ancienne consommation)
    		if (g != null) g.retirerMaison(m);
    		// Mettre à jour le type de la maison
    		m.setType(type);
    		// Rajouter la maison au générateur (ajoute la nouvelle consommation)
    		if (g != null) g.ajouterMaison(m);
    		
    		// Avertit l'utilisateur de la MAJ
    		return String.format("Maison '%s' mise à jour : %s -> %s", nom, ancienType, type);
    	}
    	else {
    		maisons.add(new Maison(nom, type));
    		return String.format("Maison '%s' créée avec succès (%s).", nom, type);
    	}
    }
    
    
    // --- GESTION DES CONNEXIONS ---
    
    /**
     * Connecte une maison à un générateur.
     * Gère la saisie inversée (M1 G1 ou G1 M1).
     * <p>
     * Affiche des messages d'erreur si :
     * <ul><li>Les noms sont introuvables.</li>
     * <li>La maison est déjà connectée à ce générateur.</li>
     * <li>La maison est déjà connectée à un *autre* générateur.</li></ul>
     *
     * @param s1 Le nom de la maison ou du générateur.
     * @param s2 Le nom du générateur ou de la maison.
     * @return Un message de confirmation.
     * @throws ReseauException Si les éléments sont introuvables ou déjà connectés.
     */
    public String connecter(String s1, String s2) throws ReseauException {
    	String nomM = connecterChercherMaison(s1, s2);
    	String nomG = connecterChercherGenerateur(s1, s2);
    	
        if (nomM == null || nomG == null) {
        	throw new ReseauException("Éléments introuvables.");
        }
        
        Maison m = chercherMaison(nomM);
        Generateur g = chercherGenerateur(nomG);
        
        Generateur ancienG = m.getGenerateurAssocie();
        if(ancienG == g) {
           throw new ReseauException("Déjà connectés.");
        }
        // On ne peut pas connecter une maison déjà connectée
        if (ancienG != null) {
        	throw new ReseauException("Déjà connectée à " + ancienG.getNom());
        }
        
        g.ajouterMaison(m);
        m.setGenerateurAssocie(g);
        
        return String.format("Succès : %s connecté à %s.", m.getNom(), g.getNom());
    }
    
    /**
     * Déconnecte une maison d'un générateur.
     * Gère la saisie inversée (M1 G1 ou G1 M1).
     * <p>
     * Affiche des messages d'erreur si :
     * <ul><li>Les noms sont introuvables.</li>
     * <li>La maison n'est pas connectée à ce générateur.</li>
     * <li>La maison est connectée à un *autre* générateur.</li>
     * <li>La maison n'est connectée à rien.</li></ul>
     *
     * @param s1 Le nom de la maison ou du générateur.
     * @param s2 Le nom du générateur ou de la maison.
     * @throws ReseauException Si la connexion n'existe pas ou est invalide.
     */
    public void deconnecter(String s1, String s2) throws ReseauException {
    	String nomM = connecterChercherMaison(s1, s2);
    	String nomG = connecterChercherGenerateur(s1, s2);
    	
        if (nomM == null || nomG == null) {
        	throw new ReseauException("Éléments introuvables.");
        }
        
        Maison m = chercherMaison(nomM);
        Generateur g = chercherGenerateur(nomG);
        
        Generateur ancienG = m.getGenerateurAssocie();
        
        if(ancienG == g) { // Cas souhaité : la connexion existe
            m.setGenerateurAssocie(null);
            g.retirerMaison(m);
        }
        else if (ancienG != null) { // Cas d'erreur : connectée ailleurs
        	throw new ReseauException("Erreur : La maison " + nomM + " est connectée à " + ancienG.getNom() + ", pas à " + nomG + ".");
        }
        else { // Cas d'erreur : non connectée
        	throw new ReseauException("Erreur : La maison " + nomM + " n'est connectée à rien.");
        }
    }
    
    /**
     * Méthode "helper" privée pour trouver un nom de maison parmi deux chaînes,
     * quel que soit leur ordre.
     *
     * @param s1 Première chaîne (ex: "M1" ou "G1").
     * @param s2 Deuxième chaîne (ex: "G1" ou "M1").
     * @return Le nom de la maison, ou {@code null} si aucune n'est trouvée.
     */
    private String connecterChercherMaison(String s1, String s2) {
    	if (chercherMaison(s1) != null)
    		return s1;
        if (chercherMaison(s2) != null)
        	return s2;
        return null;
    }
    
    /**
     * Méthode "helper" privée pour trouver un nom de générateur parmi deux chaînes,
     * quel que soit leur ordre.
     *
     * @param s1 Première chaîne (ex: "M1" ou "G1").
     * @param s2 Deuxième chaîne (ex: "G1" ou "M1").
     * @return Le nom du générateur, ou {@code null} si aucun n'est trouvé.
     */
    private String connecterChercherGenerateur(String s1, String s2) {
    	if (chercherGenerateur(s1) != null)
    		return s1;
        if (chercherGenerateur(s2) != null)
        	return s2;
        return null;
    }
    
    
    // ----- VERIFICATION DU RESEAU -----
    
    /**
     * Vérifie que chaque maison est connectée à un générateur.
     * Si ce n'est pas le cas, affiche la liste des maisons non connectées.
     *
     * @return {@code true} si toutes les maisons sont connectées, sinon {@code false}.
     */
    private boolean verifierConnexion(){
    	return maisonsNonConnectees().isEmpty();
    }
    
    /**
     * Méthode "helper" privée pour construire la liste des maisons non connectées.
     *
     * @return Une liste d'objets {@link Maison} non associés à un générateur.
     */
    private List<Maison> maisonsNonConnectees(){
    	List<Maison> maisonsNonConnectees = new ArrayList<Maison>();
    	
    	for (Maison m : maisons){
    		if (m.getGenerateurAssocie() == null){
    			maisonsNonConnectees.add(m);
    		}
    	}
    	return maisonsNonConnectees;
    }
    
    
    /**
     * Vérifie que la capacité totale des générateurs est >= la demande totale des maisons.
     * Si ce n'est pas le cas, affiche un message détaillé du déficit.
     *
     * @return {@code true} si la capacité est suffisante, sinon {@code false}.
     */
    private boolean verifierCapacite(){
    	double sommeCapacite = 0;
    	
        for (Generateur g : generateurs) {
        	sommeCapacite += g.getCapaciteMax();
        }
        
        double sommeConsommation = 0;
        
        for (Maison mi : maisons) {
        	sommeConsommation += mi.getConsommation();
        }
        
        return sommeCapacite >= sommeConsommation;
    }
    
    /**
     * Vérifie qu'il y a au moins un générateur ET une maison dans le réseau.
     *
     * @return {@code true} si le réseau n'est pas vide, sinon {@code false}.
     */
    public boolean verifierNonVide() {
    	return !generateurs.isEmpty() && !maisons.isEmpty();
    }
    
    /**
     * Vérifie le réseau et lance une exception précise si quelque chose ne va pas.
     * 
     * @throws ReseauException Si une vérification échoue.
     */
    public void verificationReseau() throws ReseauException {
        // Vérification si vide
        if (!verifierNonVide()) {
            throw new ReseauException("Le réseau doit contenir au moins un générateur et une maison.");
        }

        // Vérification des connexions
        if (!verifierConnexion()) {
            // On reconstruit le message précis pour l'interface
            List<Maison> orphelines = maisonsNonConnectees();
            StringBuilder sb = new StringBuilder("Maisons non connectées : ");
            for (Maison m : orphelines) {
            	sb.append(m.getNom()).append(" ");
            }
            throw new ReseauException(sb.toString());
        }

        // Vérification de la capacité
        if (!verifierCapacite()) {
            // On recalcule juste pour le message d'erreur (c'est rapide)
            double cap = generateurs.stream().mapToDouble(Generateur::getCapaciteMax).sum();
            double cons = maisons.stream().mapToDouble(Maison::getConsommation).sum();
            throw new ReseauException(String.format("Capacité insuffisante (%.0f < %.0f)", cap, cons));
        }
    }
    
    
    // ----- CALCUL COUT DU RESEAU -----
    
    
    
    /**
     * Calcule la dispersion (Disp(S)) du réseau.
     * C'est la somme des écarts de chaque générateur par rapport à la moyenne.
     * <p>
     * Formule: {@code Disp(S) = Σ |ug - ū|}
     *
     * @return La valeur de la dispersion (un nombre positif).
     */
    public double calculerDispersion() {
    	if (generateurs.isEmpty()) return 0; // Évite la division par zéro
    	
        List<Double> uList = new ArrayList<>();
        double sum = 0.0;
        
        for (Generateur g : generateurs) {
            double u = g.getTauxUtilisation();
            uList.add(u);
            sum += u;
        }
        
        double moyenne = sum / uList.size(); // ū
        double disp = 0.0;
        
     // Calcule la somme des écarts absolus
        for (double u : uList)
        	disp += Math.abs(u - moyenne);
        
        return disp;
    }
    
    
    /**
     * Calcule la pénalisation des surcharges (Surcharge(S)) du réseau.
     * C'est la somme des surcharges normalisées de chaque générateur.
     * <p>
     * Formule: {@code Surcharge(S) = Σ max(0, (Lg - Cg) / Cg)}
     *
     * @return La valeur de la surcharge (un nombre positif).
     */
    public double calculerSurcharge() {
        double s = 0.0;
        
        for (Generateur g : generateurs) {
            double Lg = g.getChargeActuelle();
            double Cg = g.getCapaciteMax();
            double excedent = Lg - Cg;
            
            if (excedent > 0)
                s += excedent / Cg;
        }
        
        return s;
    }
    
    
    /**
     * Calcule le coût total du réseau.
     * <p>
     * Formule: {@code Cout(S) = Disp(S) + λ * Surcharge(S)}
     *
     * @param lambda Le coefficient de pénalisation de la surcharge.
     * @return Le coût total du réseau.
     */
    public double calculerCout(double lambda) {
        return calculerDispersion() + lambda * calculerSurcharge();
    }
    
    
    
    // --- AUTRES OPERATIONS ---
    /**
     * Retourne une représentation textuelle complète de l'état du réseau.
     * <p>
     * Inclut le tableau des générateurs (charge, taux, capacité) et la liste
     * des maisons connectées, ainsi que les maisons orphelines.
     * @return Une chaîne de caractères multi-lignes décrivant l'état actuel du réseau.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n---------------------------------------------------\n");
        sb.append("---               ÉTAT DU RÉSEAU                ---\n");
        sb.append("---------------------------------------------------\n");

        // Afficher les générateurs
        sb.append(String.format("GÉNÉRATEURS (%d) :%n", generateurs.size()));
        
        // En-tête du tableau aligné
        sb.append(String.format("| %-10s | %-10s | %-10s | %-8s |%n", "NOM", "CAPACITÉ", "CHARGE", "TAUX"));
        sb.append(String.format("|------------|------------|------------|----------|%n"));

        if (generateurs.isEmpty()) {
            sb.append("\t(Aucun générateur)\n");
        } else {
            for (Generateur g : generateurs) {
                // Formate les données
                String capaciteStr = String.format("%.1fkW", g.getCapaciteMax());
                String chargeStr = String.format("%.1fkW", g.getChargeActuelle());
                String tauxStr = String.format("%.2f%%", g.getTauxUtilisation() * 100);
                
                // Ligne du générateur
                sb.append(String.format("| %-10s | %-10s | %-10s | %-8s |%n",
                    g.getNom(),
                    capaciteStr,
                    chargeStr,
                    tauxStr
                ));

                // Maisons associées
                if (g.getMaisonsConnectees().isEmpty()) {
                    sb.append(String.format("|  -> (Aucune maison connectée)%n"));
                } else {
                    for (Maison m : g.getMaisonsConnectees()) {
                        sb.append(String.format("|  -> %-10s (%s, %.1fkW)%n",
                            m.getNom(),
                            m.getType().name(), 
                            m.getConsommation()
                        ));
                    }
                }
                // Séparateur
                sb.append(String.format("|------------|------------|------------|----------|%n"));
            }
        }
        
        // Maisons non connectées
        List<Maison> nonConnectees = maisonsNonConnectees();
        if (!nonConnectees.isEmpty()) {
            sb.append("\nMAISONS NON CONNECTÉES (" + nonConnectees.size() + ") :\n");
            for (Maison m : nonConnectees) {
                sb.append(String.format("\t- %s (%s, %.1fkW)%n", 
                    m.getNom(), 
                    m.getType().name(), 
                    m.getConsommation()));
            }
        }
        
        return sb.toString();
    }
    
    
    // --- METHODES DE RECHERCHE ---
    
    
    /**
     * Cherche un générateur par son nom.
     *
     * @param nom Le nom du générateur à trouver.
     * @return L'objet {@link Generateur} ou {@code null} s'il n'est pas trouvé.
     */
    public Generateur chercherGenerateur(String nom){
    	for (Generateur g : generateurs){
    		if (g.getNom().equals(nom))
    			return g;
    	}
    	return null; // Non trouvé
    }
    
    /**
     * Cherche une maison par son nom.
     *
     * @param nom Le nom de la maison à trouver.
     * @return L'objet {@link Maison} ou {@code null} si elle n'est pas trouvée.
     */
    public Maison chercherMaison(String nom){
    	for (Maison m : maisons){
    		if (m.getNom().equals(nom))
    			return m;
    	}
    	return null; // Non trouvé
    }
    
    
    // --- OPTIMISATIONS RESEAU ---
    
    /**
     * Crée une copie profonde du réseau avec tous ses composants et connexions.
     * Utilisé pour le multithreading des algorithmes d'optimisation.
     *
     * @return Une nouvelle instance indépendante de {@link Reseau}.
     */
    public Reseau copier() {
        Reseau copie = new Reseau();

        // Cloner tous les générateurs
        for (Generateur g : this.generateurs) {
            copie.generateurs.add(new Generateur(g));
        }

        // Cloner toutes les maisons
        for (Maison m : this.maisons) {
            copie.maisons.add(new Maison(m));
        }

        // Reconstruction des connexions
        // On parcourt les maisons ORIGINALES pour voir où elles étaient connectées
        for (Maison mOriginale : this.maisons) {
            Generateur gOriginal = mOriginale.getGenerateurAssocie();

            if (gOriginal != null) {
                // On cherche les ÉQUIVALENTS dans la COPIE (par leur nom)
                Maison mCopie = copie.chercherMaison(mOriginale.getNom());
                Generateur gCopie = copie.chercherGenerateur(gOriginal.getNom());

                if (mCopie != null && gCopie != null) {
                    // On connecte les CLONES entre eux
                    gCopie.ajouterMaison(mCopie);
                    mCopie.setGenerateurAssocie(gCopie);
                }
            }
        }
        
        return copie;
    }
    
    
    // --- ALGORITHMES ---
    
    /**
     * Optimise le réseau avec l'algorithme Naïf (Hill Climbing).
     * 
     * @param k Le nombre d'itérations.
     * @param lambda Le coefficient de pénalité des surcharges.
     */
    public void optimiserNaif(int k, double lambda) {
        new algo.AlgoNaif().optimiser(this, k, lambda);
    }
    
    /**
     * Optimise le réseau avec l'algorithme Recuit Simulé (Simulated Annealing).
     * 
     * @param k Le nombre d'itérations.
     * @param lambda Le coefficient de pénalité des surcharges.
     */
    public void optimiserRecuitSimule(int k, double lambda) {
        // On délègue à la stratégie
        new algo.AlgoRecuitSimule().optimiser(this, k, lambda);
    }
    
    /**
     * Optimise le réseau avec l'algorithme Glouton (Greedy).
     * 
     * @param lambda Le coefficient de pénalité des surcharges.
     */
    public void optimiserReseauGlouton(double lambda) {
        new algo.AlgoGlouton().optimiser(this, 0, lambda);
    }
    
    /**
     * Optimise le réseau avec l'algorithme ILS (Iterated Local Search).
     * 
     * @param k Le nombre d'itérations.
     * @param lambda Le coefficient de pénalité des surcharges.
     */
    public void optimiserILS(int k, double lambda) {
        new algo.AlgoILS().optimiser(this, k, lambda);
    }
    
    /**
     * Optimise le réseau avec l'algorithme LAHC (Late Acceptance Hill Climbing).
     * 
     * @param k Le nombre d'itérations.
     * @param lambda Le coefficient de pénalité des surcharges.
     */
    public void optimiserLAHC(int k, double lambda) {
        new algo.AlgoLAHC().optimiser(this, k, lambda);
    }
}
