@echo off
echo Lancement du programme d'optimisation de reseau electrique...
echo.

REM Verification que Java est installe
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Java n'est pas installe ou n'est pas dans le PATH
    echo Veuillez installer Java 17 ou superieur
    pause
    exit /b 1
)

REM Verification que le dossier libs existe
if not exist "libs" (
    echo ERREUR: Le dossier 'libs' est introuvable
    echo Assurez-vous que les bibliotheques JavaFX sont dans le dossier libs/
    pause
    exit /b 1
)

REM Verification que le dossier bin existe
if not exist "bin" (
    echo ERREUR: Le dossier 'bin' est introuvable
    echo Compilez d'abord le projet dans Eclipse
    pause
    exit /b 1
)

REM Lancement de l'application avec chemin vers les bibliotheques natives
java -Djava.library.path=libs -Dprism.order=sw -Dprism.verbose=true --module-path libs --add-modules javafx.controls,javafx.fxml -cp "bin;libs/*" app.Interface %*

REM Gestion du code de sortie
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Le programme s'est termine avec des erreurs
    pause
)