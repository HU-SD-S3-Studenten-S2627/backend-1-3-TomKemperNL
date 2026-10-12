---
layout: mermaid
---

# Oefenopdracht OOP (30min)

In deze oefenopdracht willen we weer even een klein beetje oefenen met Java en OOP.

Je kunt een nieuw Project aanmaken in IntelliJ via ```New Project / Java / Maven```

## Schets

```mermaid
classDiagram
    direction RL

    class AvailableGame {
        <<enumeration>>

        Blackjack (1-10 spelers)
        Uno (3-6 spelers)
        Love Letter (2-4 spelers)
        Werewolves (8-20 spelers)
    }

    class GameState {
        <<enumeration>>

        InLobby
        Started
    }

    class Lobby {
        - int minPlayers
        - Player host
        - List~Player~ players
        - AvailableGame chosenGame
        ...

        +join(Player actingPlayer) void
        +start(Player actingPlayer) void
    }
    note for Lobby "Op de plek van de puntjes moet je naar eigen inzicht velden toevoegen"

    class Player {
        -String username
        -String fullName
        -String email
    }
    note for Player "Je mag aannemen dat username uniek is"

    note "getters/setters zijn in de basis weggelaten. Voeg toe naar smaak, zoals zout & peper."

    Lobby "1" --> "*" Player
    Lobby ..> AvailableGame

    note "Niet alle benodigde relaties zijn getekend. Voeg toe naar smaak..."

```

## Requirements

* De Host is ook altijd 1 van de players
* Alleen de Host mag de game starten, en alleen als aan het min. aantal spelers is voldaan
* Er mogen niet meer dan het max. aantal spelers joinen
* Nadat het spel is gestart mogen spelers niet meer joinen
* Er mogen geen dubbele spelers (met dezelfde username) in de lobby

Klaar? Hand omhoog, docent checkt het even.
Was het goed? Top, jij bent nu ook (even) docent.
