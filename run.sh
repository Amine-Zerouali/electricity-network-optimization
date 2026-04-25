#!/bin/bash

echo "Lancement du programme d'optimisation de reseau electrique..."
echo ""

# Verification que Java est installe
if ! command -v java &> /dev/null
then
    echo "ERREUR: Java n'est pas installe ou n'est pas dans le PATH"
    echo "Veuillez installer Java 17 ou superieur"
    exit 1
fi

# Verification que le dossier libs existe
if [ ! -d "libs" ]; then
    echo "ERREUR: Le dossier 'libs' est introuvable"
    echo "Assurez-vous que les bibliotheques JavaFX sont dans le dossier libs/"
    exit 1
fi

# Verification que le dossier bin existe
if [ ! -d "bin" ]; then
    echo "ERREUR: Le dossier 'bin' est introuvable"
    echo "Compilez d'abord le projet dans Eclipse"
    exit 1
fi

# Lancement de l'application avec chemin vers les bibliotheques natives
java -Djava.library.path=libs -Dprism.order=sw -Dprism.verbose=true --module-path libs --add-modules javafx.controls,javafx.fxml -cp "bin:libs/*" app.Interface "$@"

# Gestion du code de sortie
if [ $? -ne 0 ]; then
    echo ""
    echo "Le programme s'est termine avec des erreurs"
    read -p "Appuyez sur Entree pour continuer..."
fi