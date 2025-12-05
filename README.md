<p align="center">
    <br />
    <img src="https://raw.githubusercontent.com/kainovaii/TheGuardian-bot/refs/heads/main/src/main/resources/assets/img/logo_simple.png" width="25%">
    <br />
</p>
<p align="center">
    <img src="https://img.shields.io/badge/Version-1.0-purple.svg" />
    <img style="margin-left: 10px;" src="https://img.shields.io/badge/License-MIT-purple.svg" />
</p>

**TheGuardian-bot** est un bot Discord de modération écrit en **Java** utilisant [JDA](https://github.com/DV8FromTheWorld/JDA). Il est conçu pour aider les administrateurs et modérateurs à gérer un serveur Discord de manière efficace, avec des fonctionnalités de modération avancées et un tableau de bord web intégré.

---

## 📄 Table des matières

- 📝 À propos
- ✅ Fonctionnalités
- 🔧 Prérequis
- 🚀 Installation
---

## 📝 À propos

TheGuardian-bot offre :

- Une gestion complète des utilisateurs (kick, ban, mute, warnings)
- Une journalisation des actions de modération
- Un scanner de mots et filtrage basé sur l’API Perspective pour détecter les contenus inappropriés
- Une interface web pour gérer alertes et sanctions
- Des commandes slash intégrées pour une utilisation facile

Le bot supporte plusieurs environnements (DEV / PROD) et charge automatiquement les paramètres depuis un fichier `.env`.

---

## ✅ Fonctionnalités

- **Modération des utilisateurs** : kick, ban, mute, warnings
- **Commandes slash** pour toutes les actions administratives
- **Scanner de mots interdits** : détecte automatiquement les mots offensants et alertes la modération
- **Analyse de toxicité** : grâce à l’API [Perspective](https://perspectiveapi.com/), le bot évalue le score de toxicité des messages
    - Score ≥ 0.5 → alerte générée
    - Score ≥ 0.8 → sanction automatique (mute + enregistrement de pénalité)
- **Logs et alertes** : système d’alertes et journalisation via le serveur web intégré
- **Préchargement du cache des membres** pour un accès rapide aux informations du serveur
- **Présence dynamique** : le bot affiche son activité sur le serveur
- **Gestion de rôles modérateurs** : seuls les rôles autorisés peuvent utiliser les commandes de modération

---

## 🔧 Prérequis

- Java 17 ou supérieur
- Maven ou Gradle (selon ton projet)
- Un bot Discord avec un **token valide**
- Permissions nécessaires sur le serveur Discord : gestion des rôles, kick, ban, lecture et écriture dans les salons

---

## 🚀 Installation

```bash
# Cloner le projet
git clone https://github.com/kainovaii/TheGuardian-bot.git
cd TheGuardian-bot

# Compiler le projet avec Maven
mvn clean install
