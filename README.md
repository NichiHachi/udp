# Application UDP

Cette application est une implémentation simple d'un serveur et d'un client utilisant le protocole UDP. Elle permet aux clients de se connecter au serveur, d'envoyer des messages privés ou à tous les utilisateurs connectés, et de lister les utilisateurs connectés.

## Fonctionnalités

- **Serveur** :
  - Gère les connexions des clients.
  - Permet l'envoi de messages privés ou à tous les utilisateurs connectés.
  - Fournit une liste des utilisateurs connectés.

- **Client** :
  - Se connecte au serveur avec un nom d'utilisateur.
  - Envoie des messages privés ou à tous les utilisateurs.
  - Affiche la liste des utilisateurs connectés.

## Prérequis

- **Java 21** ou version supérieure.
- **Gradle**.

## Structure du projet

- `app/src/main/java/udp/Server.java` : Implémentation du serveur.
- `app/src/main/java/udp/Client.java` : Implémentation du client.

## Instructions pour exécuter l'application

### 1. Lancer le serveur

1. Ouvrez un terminal.
2. Exécutez la commande suivante pour démarrer le serveur :
   ```sh
   ./gradlew runServer
    ``` 

### 2. Lancer le client
1. Ouvrez un nouveau terminal
2. Exécutez la commande suivante pour démarrer le client :
   ```sh
   ./gradlew runClient
    ``` 

### 3. Utilisation du client
- `list` : Affiche la liste des utilisateurs connectés.
- `msg` : Envoie un message privé à un utilisateur ou un message à tous les utilisateurs (en mettant `all` en pseudo).
- `exit` : Déconnecte le client du serveur.
- `help` : Affiche les commandes disponibles.

    