# Oefenopdracht @RestControllers (2x 15-30min)

We gaan voor de Lobby uit de eerdere opdracht een @RestController maken.
Voor nu gebruiken we gewoon een List<> als data-bron in de Lobby. Dat gaan we later verbeteren. Dus:

```java
@RestController
public class PlayerController {

  private final PlayerDAO dao; //Een koppeling met de database
  //...    
}

```

Maar:

```java
@RestController
public class LobbyController {

  private final PlayerDAO dao;
  private List<Lobby> lobbies = new ArrayList<>(); //Puur een in-memory lijstje om te testen
  //...    
}
```

Uiteraard mag je een nette DAO maken voor Lobbys, want dat gaan we toch later van je vragen, maar dan wordt deze oefening wel iets te groot.

## Opdracht 1: PlayerController

Implementeer de volgende endpoints, er staat een *HTTP file* klaar in ```test/nl.hu.s3.project/lobby/presentation/PlayerController.http```.

* **GET /players**
  Return de lijst van alle spelers. We verwachten een response als:
  ```json
  [
     {
        "username": "AliceDeVries",
        "name": "Alice De Vries",
        "email": "alice@example.com"
     },
     ...
  ]
  ```
  **Let op**: in de database heet de kolom `mail` en in het domeinmodel is het `emailAddress`, en toch willen we in de JSON
  response `email` zien. Dat verschil is onderdeel van de oefening, en moet je dus netjes oplossen zonder de database of
  domeinmodel te wijzigen. Denk aan het concept DTO (Data Transfer Object).

* **GET /players/{username}**

  Return een enkele speler, die met de gevraagde username.

  ```json
    {
        "username": "AliceDeVries",
        "name": "Alice De Vries",
        "email": "alice@example.com"
    }
  ```
  Return een nette 404-error als de speler niet bestaat.

* **POST /players**

  Maak een endpoint dat middels @Requestbody een stukje JSON zoals hierboven accepteert, en deze vervolgens aan de lijst toevoegt.
  Return een nette 400-error als 1 van de velden leeg is.
  Return een 201 Created als het wel gelukt is.

* **DELETE /players/{username}**

  Maak een endpoint dat de speler met de gegeven username uit de lijst verwijdert. Return hier een 204 NO CONTENT status.

* **PUT /players/{username}**

  Maak een endpoint dat er voor zorgt dat een speler diens naam & email kan wijzigen, maar niet de username.

  ```json
    {
        "name": "Alice DeVries",
        "email": "alice-2@example.com"
    }
  ```
  Return een nette 404-error als de speler niet bestaat.
  (tricky vraag: Waarom mag je de username niet wijzigen?)

## Opdracht 2: LobbyController

Implementeer de volgende endpoints, er staat een *HTTP file* klaar in ```test/nl.hu.s3.project/lobby/presentation/LobbyController.http```.
Het is handig om in de `StartupRunner` wat extra spelers klaar te zetten, zodat je die niet elke keer eerst hoeft te POSTen.

* **GET /lobbies**

  Return de lijst van alle lobbies.
  Met als Response iets als:

```json
[
  {
    "game": "Blackjack",
    "host": "BobChunker",
    "players": [ "BobChunker", "AliceDeVries", "etc"],
    ...
  }
]
```

> Er is nog geen data op dit moment, dus om dit endpoint nuttig te kunnen testen zul je 'fake it until you make it' moeten
> toepassen. Dat kun je doen door bijv. in de constructor van de LobbyController alvast stiekem 1 lobby in Java-code toe
> te voegen, met een niet-uit-de-database dummy Player als host. Zodra je POST endpoint werkt kun je deze code dan weghalen.

* **GET /lobbies/{id}**

  Vraag een enkele Lobby op.
  Het id is voor nu gewoon de plek in de lijst. Dus GET /lobbies/0 geeft de lobby die je zou krijgen met:

    ```java
    this.lobbies.get(0);
    ```    


* **POST /lobbies**

  Gebruik de @RequestBody om een nieuwe lobby te kunnen maken:
    ```json
    {
        "game": "BLACKJACK",
        "host": "freezeybob"
    }
    ```

  Return het zojuist gemaakte object als `ResponseEntity` (dat is dus uitgebreider dan de requestbody).
  Let op een ResponseStatus van 201 Created, ipv. 200 OK. Daarvoor zul je ook een Spring @-annotatie nodig hebben.
  Als een Host met de username nog niet bestaat, geef dan een 400 terug.
  Als er een spel wordt gekozen dat niet in de enum bestaat, geef dan ook een 400 terug.

* **DELETE /lobbies/{id}**

  Verwijder een enkele Lobby uit de databron (in dit geval onze lijst). Denk aan de 404 als de Lobby niet bestaat.
  (Hier is met de simpelste implementatie iets vreemds aan de hand: wat?)

* **PUT /lobbies/{id}**

  Gebruik de @RequestBody om (alleen!) de game van een Lobby te kunnen wijzigen:
    ```json
    {
        "game": "BLACKJACK"       
    }
    ```

## Opdracht 3: API-Design

We kunnen nu Lobbies aanmaken en verwijderen.
Implementeer een tweetal (**TIJDELIJKE**) endpoints voor het joinen/leaven van een lobby:

* **POST /lobbies/{id}/join**
* **POST /lobbies/{id}/leave**

Deze endpoints accepteren in hun body een object met de ```{ username }``` van een speler.

Dit is een procedure-oriented ontwerp. Commit dit even tussendoor.

Wat is het resource-oriented alternatief?
Vervang je procedure-oriented endpoints door resource-oriented endpoints.

## Extra uitdagingen

* Location Header op Post. Om een POST helemaal netjes te doen voeg je een Header "Location" toe aan de Response, met daarin de url naar de zojuist aangemaakte resource (want meestal kan je een toegekend Id niet raden)

* Is het PUTten van een ander spelletje wel zo simpel? Wat doe je als je bijv. van 16 aspirant weerwolven terug wil naar max 6 Uno spelers?

* Pagination, voeg queryparameters toe, zoals ?pagesize=5&pageStart=10 zodat hele grote hoeveelheden lobbies niet een enorme database-hit worden.

* HatEoAS (Hypermedia as the Engine of Application State), pas je DTOs aan zodat je responses zoveel mogelijk laten zien hoe de applicatie verder gebruikt kan worden.