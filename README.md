# Optimisation du Réseau de Distribution d'Électricité

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/UI-JavaFX-orange)](https://openjfx.io/)
[![Eclipse IDE](https://img.shields.io/badge/IDE-Eclipse-2C2255)](https://www.eclipse.org/)
[![JUnit](https://img.shields.io/badge/Tests-JUnit5-green)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/license-MIT-yellow)](LICENSE)
[![Université Paris Cité](https://img.shields.io/badge/Université-Paris%20Cité-red)](https://u-paris.fr/)

---
 
## Description

Ce projet propose une application Java/JavaFX permettant d'optimiser la distribution d'électricité dans un réseau composé de maisons et de générateurs. L'objectif est d'affecter chaque maison à un générateur de manière à minimiser simultanément la somme des distances entre les maisons et leurs générateurs, et les pénalités de surcharge des générateurs.

Cinq algorithmes d'optimisation sont disponibles et comparables via une interface graphique interactive.
 
---

## Auteurs

- **Essamni Ayman**
- **Takenne Mekem Simeon**
- **Zerouali Amine**

*Projet réalisé dans le cadre du cours PAA à Université Paris Cité (Décembre 2025)*

---

## Prérequis

- **Java JDK 17** ou supérieur
- Les bibliothèques **JavaFX** (incluses dans le dossier `/libs`)

---

## Structure du projet

```
projet/
│
├── src/                    # Code source Java
│   ├── app/                # Point d'entrée (Interface.java)
│   ├── vue/                # Composants JavaFX (écrans, contrôleurs)
│   ├── modele/             # Modèles métier (Maison, Generateur, Reseau)
│   ├── io/                 # Lecture/écriture des fichiers réseau .txt
│   └── algo/               # Algorithmes d'optimisation
│
├── libs/                   # Bibliothèques JavaFX et JUnit5 (.jar)
├── resources/              # Feuille de style CSS (style.css)
├── doc/                    # Documentation Javadoc générée
├── test/                   # Tests unitaires
├── Instances/              # Fichiers réseaux exemples (.txt)
│
├── run.bat                 # Script de lancement Windows
└── run.sh                  # Script de lancement macOS / Linux
```

---

## Modélisation Mathématique

La résolution repose sur la minimisation de la fonction objectif suivante :

**f(S) = Σ d(m, g_m) + λ × Σ p(g)**

| Symbole | Définition |
|---------|-----------|
| `M` | Ensemble des maisons |
| `G` | Ensemble des générateurs |
| `g_m` | Générateur affecté à la maison `m` |
| `d(m, g)` | Distance euclidienne entre maison et générateur |
| `p(g)` | Pénalité de surcharge du générateur `g` |
| `λ` | Coefficient de pénalité (réglable) |

---

## Algorithmes d'Optimisation

### 1. Hill Climbing (Algorithme Naïf)

**Principe :** Recherche locale itérative acceptant uniquement les solutions strictement améliorantes.

**Fonctionnement détaillé :**

> **État initial :** Part de la configuration actuelle du réseau

> **Boucle d'optimisation (k itérations) :**
> - Sélectionne aléatoirement une maison `m`
> - Sélectionne aléatoirement un générateur destination `g_dest`
> - Si `g_dest` ≠ générateur actuel :
>   - Effectue le transfert : `g_ancien → g_dest`
>   - Calcule le nouveau coût
>   - **Critère d'acceptation :** `coût_nouveau < coût_actuel` → garde le changement
>   - Sinon → annulation du transfert (rollback)

**Stratégie d'exploration :** Voisinage par transfert simple (1 maison déplacée)

| Avantages | Limites |
|---|---|
| Très rapide, simple à implémenter | Bloqué dans les minima locaux |
| Efficace sur petites instances | Pas d'échappatoire |

---

### 2. Recuit Simulé (Simulated Annealing)

**Principe :** Métaheuristique inspirée du processus physique de recuit métallurgique. Accepte temporairement des solutions dégradantes pour échapper aux minima locaux.

**Fonctionnement détaillé :**

> **Initialisation :**
> - Température initiale : `T = 1000.0`
> - Coefficient de refroidissement : `α = 0.99`

> **Boucle principale (k itérations) :**
> - Sélectionne aléatoirement une maison et un générateur destination
> - Effectue le transfert
> - Calcule la variation de coût : `Δ = coût_nouveau - coût_actuel`

> **Critère d'acceptation probabiliste :**
> - Si `Δ < 0` (amélioration) → **toujours accepté**
> - Si `Δ ≥ 0` (dégradation) → accepté avec probabilité **P = exp(-Δ / T)**
> - Refroidissement : `T = α × T` à chaque itération

**Comportement dynamique :**

| Phase | Température | Comportement |
|-------|------------|--------------|
| Début | Élevée | Exploration agressive |
| Milieu | Moyenne | Équilibre exploration/exploitation |
| Fin | Faible | Convergence fine (≈ Hill Climbing) |

| Avantages | Limites |
|---|---|
| Échappe aux minima locaux | Sensible au paramétrage (T₀, α) |
| Solutions de haute qualité | Plus lent que Hill Climbing |

---

### 3. Algorithme Glouton (Greedy Algorithm)

**Principe :** Algorithme constructif qui reconstruit le réseau de zéro en affectant chaque maison au meilleur générateur disponible.

**Fonctionnement détaillé :**

> **Phase de déconnexion :** Toutes les maisons sont déconnectées de leurs générateurs

> **Tri par consommation :** Les maisons sont triées par ordre de consommation décroissante

> **Affectation gloutonne :** Pour chaque maison (de la plus consommatrice à la moins consommatrice) :
> - Teste virtuellement l'ajout à chaque générateur
> - Calcule le coût total résultant (distance + pénalité λ)
> - Sélectionne le générateur minimisant ce coût
> - Valide l'affectation définitivement

**Complexité :** O(n × m × g) où n = maisons, m = générateurs, g = coût du calcul total

| Avantages | Limites |
|---|---|
| Solution initiale de qualité, rapide, déterministe | Décisions irréversibles, pas d'exploration alternative |

---

### 4. Iterative Local Search (ILS)

**Principe :** Alterne entre phases de recherche locale et de perturbations aléatoires.

**Fonctionnement détaillé :**

> **Initialisation :**
> - Descente initiale avec Hill Climbing (1000 itérations)
> - Sauvegarde du meilleur réseau global

> **Boucle ILS (k/100 cycles) :**

>> **Phase 1 — Perturbation (The Kick) :**
>> - Force du kick : `max(2, n/10)` maisons déplacées aléatoirement
>> - But : sortir violemment du bassin d'attraction du minimum local

>> **Phase 2 — Réparation :**
>> - Hill Climbing intensif (500 itérations)

>> **Phase 3 — Acceptation :**
>> - Si `coût_actuel < coût_meilleur_global` → mise à jour du meilleur
>> - Sinon → restauration complète du meilleur réseau connu (rollback global)

**Stratégie de redémarrage :** Restart depuis le meilleur connu (pas de mémoire des échecs)

| Avantages | Limites |
|---|---|
| Équilibre recherche/perturbations, exploration structurée | Nombre de cycles limité, dépend de la qualité de la recherche locale |

---

### 5. Late Acceptance Hill Climbing (LAHC)

**Principe :** Variante du Hill Climbing utilisant une mémoire des coûts passés pour des critères d'acceptation plus flexibles.

**Fonctionnement détaillé :**

> **Initialisation :**
> - Longueur historique : `L = 500` (taille de la fenêtre mémoire)
> - Création d'un tableau circulaire `historiqueCouts[L]` rempli avec `coût_actuel`

> **Boucle principale (k itérations) :**
> - Sélectionne aléatoirement une maison et un générateur
> - Effectue le transfert et calcule `coût_nouveau`

> **Critère d'acceptation double :**
> - Index circulaire : `idx = i % L`
> - Récupère `coût_référence = historiqueCouts[idx]` (coût d'il y a L itérations)
> - **Accepte si :** `coût_nouveau ≤ coût_actuel` **OU** `coût_nouveau ≤ coût_référence`
> - Met à jour l'historique : `historiqueCouts[idx] = coût_actuel`

**Influence du paramètre L :**

| Valeur de L | Comportement |
|-------------|-------------|
| Petit (50–100) | Convergence rapide, mémoire courte |
| Moyen (500) | Équilibre exploration/exploitation |
| Grand (5000+) | Exploration prolongée, convergence lente |

| Avantages | Limites |
|---|---|
| Très robuste, pas de paramètres thermiques | Nécessite calibration de L selon la taille du problème |
| Excellentes performances sur réseaux denses | — |

---

### Comparaison des algorithmes

| Algorithme | Vitesse | Qualité | Minima locaux | Paramètres |
|------------|---------|---------|---------------|------------|
| Hill Climbing | Très rapide | Faible | Bloqué | Aucun |
| Glouton | Rapide | Moyenne | Bloqué | Aucun |
| Recuit Simulé | Lent | Très haute | Échappe | T₀, α |
| ILS | Moyen | Haute | Échappe | Force du kick |
| LAHC | Rapide | Très haute | Échappe | L |

---

## Paramétrage

| Paramètre | Description | Valeur par défaut |
|-----------|-------------|-------------------|
| **λ (lambda)** | Élevé = respect strict des capacités. Faible = distances privilégiées. | `10.0` |
| **Itérations** | Nombre d'itérations pour les algorithmes de recherche locale | `100 000` |

---

## Format des fichiers réseau

Les fichiers `.txt` du dossier `/Instances` suivent ce format :

```
generateur(gen1,60).
generateur(gen2,45).
maison(maison1,NORMAL).
maison(maison2,BASSE).
maison(maison3,FORTE).
connexion(gen1,maison1).
connexion(gen2,maison2).
```

Les catégories de consommation disponibles sont `BASSE` (10 kW), `NORMAL` (20 kW) et `FORTE` (40 kW).

---

## Lancement

**Classe principale :** `app.Interface`

L'application lance toujours l'interface graphique JavaFX.

### Sans arguments

```bash
# macOS / Linux
./run.sh

# Windows
run.bat
```

L'application démarre sur l'écran d'accueil où vous pouvez charger un fichier réseau, configurer les paramètres et choisir l'algorithme.

### Avec fichier en argument

```bash
./run.sh chemin_fichier [valeur_lambda]
```

| Paramètre | Description | Obligatoire |
|-----------|-------------|-------------|
| `chemin_fichier` | Chemin vers le fichier `.txt` du réseau | Oui |
| `valeur_lambda` | Coefficient de pénalité | Non (défaut : `10.0`) |

**Exemples :**

```bash
# Lambda par défaut
./run.sh Instances/reseau_A.txt

# Lambda personnalisé
./run.sh Instances/reseau_A.txt 12.5

# Windows
run.bat Instances\reseau_A.txt 12.5
```

> En cas d'erreur de chargement (fichier introuvable, format incorrect), l'application bascule automatiquement sur l'écran d'accueil.

---

## Sauvegarde des résultats

Une fois l'optimisation terminée, cliquer sur **« Sauvegarder la solution… »** depuis le menu des opérations.

Une boîte de dialogue système s'ouvre pour choisir l'emplacement et le nom du fichier, puis enregistre le réseau optimisé au format `.txt`.

Le fichier sauvegardé contient l'ensemble du réseau : générateurs, maisons et connexions générateur → maison.

---

*Projet académique — tous droits réservés.*
