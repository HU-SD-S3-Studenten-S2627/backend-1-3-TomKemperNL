# Security

Vorige les hebben we een REST API gebouwd voor Players en Lobbies. We konden spelers opvragen, lobbies maken en spelers
laten joinen en leaven.

Alleen hebben we daarbij één vrij groot probleem genegeerd:

**Onze API gelooft op dit moment alles wat de gebruiker zegt.**

Als je bijvoorbeeld een request kunt doen met:

```http
DELETE /players/AliceDeVries
```

dan verwijdert onze applicatie Alice. Maar hoe weet onze backend eigenlijk **wie** dit request doet? En zelfs als we
weten wie het request doet, hoe bepalen we dan of diegene Alice wel **mag** verwijderen?

Daar gaan we deze opdracht mee aan de slag.

We maken daarbij onderscheid tussen twee begrippen:

- **Authentication:** Wie ben jij?
- **Authorization:** Mag jij dit doen?

We gaan eerst zorgen dat een gebruiker kan inloggen en een token krijgt. Daarna gebruiken we Spring Security om dat
token bij volgende requests te controleren. Tot slot gaan we bepalen welke acties een ingelogde gebruiker daadwerkelijk
mag uitvoeren.

> **Let op:** probeer de opdrachten echt in deze volgorde uit te voeren. Vooral bij opdracht 2 is het interessant om
> eerst te kijken wat Spring Security met je applicatie doet, voordat je alles gaat configureren.

---

# Opdracht 1: Registreren & inloggen

Onze applicatie kent al `Player`s, maar heeft nog geen fatsoenlijk concept van een ingelogde gebruiker.

We beginnen daarom met twee endpoints:

```text
POST /auth/register
POST /auth/login
```

Na deze opdracht kan een gebruiker zich registreren en inloggen. Bij een succesvolle login krijgt de gebruiker een
**JWT** terug.

We gebruiken dat token in opdracht 2 om requests te authentiseren.

## 1. Registreren

Maak het volgende endpoint:

**POST /auth/register**

Het endpoint accepteert bijvoorbeeld:

```json
{
  "username": "AliceDeVries",
  "name": "Alice De Vries",
  "email": "alice@example.com",
  "password": "supersecret"
}
```

Maak hiermee een nieuwe gebruiker.

### Requirements

- Een `username` moet uniek zijn.
- Alle verplichte velden moeten gevuld zijn.
- Een leeg of ongeldig request levert een nette `400 Bad Request` op.
- Een bestaande username opnieuw registreren mag niet zomaar lukken.
- Een succesvolle registratie levert `201 Created` op.
- Een wachtwoord mag **nooit als plain text worden opgeslagen**.

Voor dat laatste probleem mag je onderzoeken welke ondersteuning Spring hiervoor biedt.

### Even nadenken

Je huidige `PlayerDTO` bevat waarschijnlijk informatie die je prima naar een client kunt sturen.

Geldt dat ook voor een wachtwoord?

Denk daarom goed na over welke DTO je gebruikt voor:

- een Player teruggeven;
- een gebruiker registreren;
- een gebruiker laten inloggen.

Je hoeft niet ieder object dat door je applicatie reist hetzelfde te maken.

---

## 2. Inloggen

Maak vervolgens:

**POST /auth/login**

Bijvoorbeeld:

```json
{
  "username": "AliceDeVries",
  "password": "supersecret"
}
```

Controleer of:

1. de gebruiker bestaat;
2. het wachtwoord klopt.

Als de combinatie klopt, maak je een JWT en stuur je die terug.

Bijvoorbeeld:

```json
{
  "token": "eyJhbGciOi..."
}
```

Je hebt JWT vorig semester al gebruikt. Kijk dus eerst of je die kennis weer kunt toepassen voordat je naar een
volledige uitwerking zoekt.

Stop minimaal de volgende informatie in je token:

- de identiteit van de gebruiker;
- de rol van de gebruiker.

Bijvoorbeeld:

```text
username = AliceDeVries
role = USER
```

### Test je token

Log in en kopieer het token uit de response.

Bekijk het token en bepaal:

- Waar staan de claims?
- Kun je de `username` terugvinden?
- Kun je de rol terugvinden?
- Heeft het token een verloopdatum?
- Kan iemand de inhoud van een JWT lezen?
- Zo ja, waarom kunnen we de inhoud dan toch vertrouwen?

> **Checkpoint:** Na opdracht 1 kun je inloggen en heb je een token.
>
> Dat betekent **nog niet** dat onze andere endpoints nu beveiligd zijn.

Commit je werk voordat je verdergaat.

---

# Opdracht 2: Authentication met Spring Security

We hebben inmiddels een token, maar onze `LobbyController` en `PlayerController` doen er nog helemaal niets mee.

Dat gaan we oplossen met **Spring Security**.

## 1. Voeg Spring Security toe

Voeg de Spring Security dependency toe aan je `pom.xml`.

Start daarna je applicatie opnieuw en probeer bijvoorbeeld:

```http
GET /players
```

en:

```http
GET /lobbies
```

### Niet direct oplossen!

Kijk eerst goed wat er gebeurt.

- Krijg je dezelfde response als eerst?
- Welke HTTP-status krijg je?
- Wordt je `PlayerController` überhaupt nog aangeroepen?
- Wat staat er in de console?

Blijkbaar doet Spring Security al iets, terwijl wij nog nauwelijks iets geconfigureerd hebben.

**Waar in de reis van HTTP-request naar `@RestController` zou dit kunnen gebeuren?**

---

## 2. SecurityFilterChain

Maak een configuratie voor Spring Security.

We willen voorlopig de volgende regels:

### Publiek

Deze endpoints moeten zonder ingelogde gebruiker beschikbaar zijn:

```text
POST /auth/register
POST /auth/login
```

Dat is logisch: je kunt moeilijk eerst ingelogd moeten zijn om in te kunnen loggen.

### Beveiligd

Alle andere endpoints vereisen een ingelogde gebruiker.

Dus bijvoorbeeld:

```text
GET    /players
GET    /players/{username}
GET    /lobbies
POST   /lobbies
DELETE /lobbies/{id}
...
```

mogen niet meer zomaar door iedereen gebruikt worden.

Maak hiervoor een `SecurityFilterChain`.

Zoek daarbij onder andere uit wat deze begrippen betekenen:

```text
requestMatchers(...)
permitAll()
authenticated()
anyRequest()
```

### Test

Doe opnieuw:

```http
GET /players
```

zonder token.

Doe daarna hetzelfde request met:

```http
Authorization: Bearer <jouw-token>
```

**Werkt het request met token al?**

Waarschijnlijk nog niet.

En dat is eigenlijk heel logisch: we sturen wel een token mee, maar wie heeft Spring verteld wat het met dat token moet
doen?

---

# Opdracht 2b: Bouw een JWT-filter

Nu komt het belangrijkste gedeelte van deze opdracht.

We gaan een **filter** toevoegen.

Je hebt HTTP-filters eerder gezien bij Jersey. Het idee is nog ongeveer hetzelfde:

```text
HTTP request
     ↓
 Filter
     ↓
 Filter
     ↓
 Filter
     ↓
@RestController
```

Iedere filter krijgt de kans om iets met het request te doen voordat het request bij onze controller terechtkomt.

Een filter kan bijvoorbeeld:

- een header bekijken;
- een token controleren;
- informatie over de gebruiker opslaan;
- een request weigeren;
- het request doorgeven aan de volgende filter.

## Maak een JWT Authentication Filter

Maak een filter dat voor ieder beveiligd request de `Authorization`-header controleert.

Een request ziet er bijvoorbeeld zo uit:

```http
GET /players/AliceDeVries
Authorization: Bearer eyJhbGciOi...
```

Je filter moet grofweg het volgende doen:

1. Lees de `Authorization`-header.
2. Controleer of deze met `Bearer ` begint.
3. Haal het JWT uit de header.
4. Controleer of het JWT geldig is.
5. Haal de identiteit en rol uit het token.
6. Vertel Spring welke gebruiker geauthentiseerd is.
7. Geef het request door aan de rest van de chain.

Voor stap 6 heb je onder andere de volgende Spring-begrippen nodig:

```text
Authentication
SecurityContext
SecurityContextHolder
```

Probeer uit te zoeken wat de relatie tussen die drie is.

---

## De belangrijke regel

Er is één regel waar je tijdens deze opdracht extra goed op moet letten:

```java
chain.doFilter(request, response);
```

Zet een breakpoint **vlak voor** deze regel.

Zet eventueel ook een breakpoint in je `PlayerController`.

Doe vervolgens:

```http
GET /players
Authorization: Bearer <token>
```

Volg het request met de debugger.

### Beantwoord voor jezelf

- Waar komt het request als eerste binnen?
- Is de controller op dat moment al uitgevoerd?
- Wat zit er in de `SecurityContext` vóór jouw filter?
- Wat zit er na het verwerken van het JWT in de `SecurityContext`?
- Wanneer wordt de controller aangeroepen?

Probeer nu tijdelijk:

```java
chain.doFilter(request, response);
```

uit te schakelen.

Doe hetzelfde request opnieuw.

**Wordt je controller nu nog aangeroepen?**

Kun je uitleggen waarom?

> Zie de FilterChain letterlijk als een ketting. Iedere schakel kan besluiten het request door te geven aan de volgende
> schakel.
>
> `chain.doFilter(...)` betekent dus niet "voer mijn filter uit". Je filter **is** op dat moment al bezig. Het betekent:
> **ga verder met de rest van de ketting**.

Zet de regel daarna natuurlijk weer terug.

---

## Ongeldig token

Test nu minimaal de volgende situaties.

### Zonder token

```http
GET /players
```

### Met geldig token

```http
GET /players
Authorization: Bearer <geldig-token>
```

### Met onzin-token

```http
GET /players
Authorization: Bearer dit.is.geen.geldig.token
```

### Met aangepast token

Neem een geldig JWT en verander handmatig een gedeelte van het token voordat je het meestuurt.

Bedenk bij iedere situatie:

- Wordt het request geauthentiseerd?
- Wordt de controller bereikt?
- Welke HTTP-status verwacht je?

Een beveiligde API hoort bij een request dat niet geauthentiseerd kan worden een nette **401 Unauthorized** terug te
geven.

> **Checkpoint:** Aan het einde van opdracht 2 weet Spring bij ieder beveiligd request wie de gebruiker is.
>
> We hebben nu **authentication**.
>
> We weten alleen nog niet of die gebruiker de gevraagde actie ook daadwerkelijk **mag uitvoeren**.

Commit je werk.

---

# Opdracht 3: Authorization

We kunnen eindelijk onderscheid maken tussen:

> "Wie ben jij?"

en:

> "Mag jij dit doen?"

Dat tweede noemen we **authorization**.

We gaan twee verschillende soorten authorization toevoegen:

1. **Role based:** heeft deze gebruiker de juiste rol?
2. **Resource based:** mag deze gebruiker iets met déze specifieke resource?

---

# Opdracht 3a: ADMIN-only endpoints

Geef je gebruikers minimaal één van deze rollen:

```text
USER
ADMIN
```

Bepaal daarna een aantal handelingen die alleen een administrator mag uitvoeren.

Bijvoorbeeld:

```text
GET    /players
DELETE /players/{username}
```

Beveilig deze endpoints met:

```java
@RolesAllowed("ADMIN")
```

Afhankelijk van je Spring Security-configuratie moet je method security nog aanzetten voordat Spring iets met deze
annotatie doet.

## Test drie situaties

### Niet ingelogd

```http
GET /players
```

### Ingelogd als USER

```http
GET /players
Authorization: Bearer <user-token>
```

### Ingelogd als ADMIN

```http
GET /players
Authorization: Bearer <admin-token>
```

Bekijk opnieuw goed de HTTP-statuscodes.

Kun je nu aan een medestudent uitleggen wat het verschil is tussen:

```text
401 Unauthorized
```

en:

```text
403 Forbidden
```

## Kleine valkuil

Als:

```java
@RolesAllowed("ADMIN")
```

helemaal niets lijkt te doen, haal de annotatie dan niet onmiddellijk weg.

Onderzoek eerst:

- Staat method security aan?
- Welke authorities heeft de ingelogde gebruiker?
- Waar komen die authorities eigenlijk vandaan?
- Komt de rol uit je database, uit het JWT, of ergens anders?

Gebruik je debugger.

---

# Opdracht 3b: "Ben jij het zelf?"

Een rol is niet altijd voldoende.

Stel Alice doet:

```http
GET /players/AliceDeVries
```

Dat lijkt redelijk.

Maar wat als Alice doet:

```http
GET /players/BobChunker
```

Of wanneer Alice probeert Bob uit een lobby te verwijderen?

```http
DELETE /lobbies/0/players/BobChunker
```

Alice en Bob hebben allebei de rol `USER`.

Een simpele:

```java
@RolesAllowed("USER")
```

kan dus niet bepalen of Alice deze actie op Bob mag uitvoeren.

We moeten niet alleen kijken naar:

```text
ROLE == USER
```

maar naar de combinatie:

```text
ingelogde gebruiker + gevraagde resource + actie
```

---

## Pas je Lobby API aan

Pak hiervoor de resource-oriented endpoints die je vorige les voor het joinen en leaven hebt gemaakt.

Zorg ervoor dat een gewone gebruiker alleen **zichzelf** aan een lobby kan toevoegen of uit een lobby kan verwijderen.

Een request bevat dus niet meer zomaar een username die je blind vertrouwt.

Je backend heeft inmiddels namelijk een veel betrouwbaardere bron:

**de geauthentiseerde gebruiker uit de `SecurityContext`.**

### Voorbeeld

Alice is ingelogd en het JWT vertelt ons:

```text
username = AliceDeVries
role = USER
```

Dan mag Alice een actie voor Alice uitvoeren.

Maar Alice mag niet alleen door een username in een URL of requestbody te veranderen ineens handelen namens:

```text
BobChunker
```

### Requirements

Implementeer minimaal:

- Een gebruiker kan alleen eigen gegevens opvragen, tenzij de gebruiker `ADMIN` is.
- Een gebruiker kan alleen zichzelf aan een lobby toevoegen.
- Een gebruiker kan alleen zichzelf uit een lobby verwijderen.
- Een `ADMIN` mag, waar logisch, acties voor andere gebruikers uitvoeren.
- Als een ingelogde gebruiker een actie probeert waarvoor geen toestemming bestaat, geef je een nette `403 Forbidden`.

---

## Belangrijke ontwerpvraag

Stel dat je controller dit bevat:

```java
if(loggedInUsername.equals(username)){
        // uitvoeren
        }
```

Technisch kun je de opdracht daarmee misschien werkend krijgen.

Maar:

**Hoort deze business/security-regel in de controller?**

Denk terug aan de verantwoordelijkheden van:

```text
Controller
Service / Application layer
Domain
```

Bespreek met een medestudent waar de volgende regel volgens jou thuishoort:

> "Een gewone speler mag alleen zichzelf uit een lobby verwijderen."

Er is niet per se één regel Java-code die we hier zoeken. We zijn vooral geïnteresseerd in je **ontwerpkeuze en
argumentatie**.

---

# Eindcheck

Pak tot slot één concreet request:

```http
DELETE /lobbies/0/players/AliceDeVries
Authorization: Bearer eyJhbGciOi...
```

Je moet nu de volledige reis van dit request kunnen uitleggen.

Probeer zonder in je code te kijken antwoord te geven op:

1. Waar komt het HTTP-request binnen?
2. Wanneer wordt jouw JWT-filter uitgevoerd?
3. Waar wordt het JWT vandaan gehaald?
4. Hoe weet je of het JWT betrouwbaar is?
5. Waar wordt de identiteit van de gebruiker opgeslagen?
6. Wat doet `chain.doFilter(request, response)`?
7. Wanneer komt het request bij de `@RestController`?
8. Waar wordt bepaald of deze gebruiker de actie mag uitvoeren?
9. Wanneer verwacht je een `401 Unauthorized`?
10. Wanneer verwacht je een `403 Forbidden`?
11. Wanneer verwacht je een succesvolle `2xx`-response?

Kun je die hele keten uitleggen?

Dan snap je niet alleen hoe je Spring Security aan de praat krijgt, maar ook **wat het framework voor je aan het doen
is**.

---

# Extra uitdagingen

Ben je eerder klaar? Kies dan één of meer van onderstaande uitbreidingen.

## 1. Token expiration

Een JWT hoort niet voor eeuwig geldig te blijven.

Voeg een verloopdatum toe en test expliciet wat er gebeurt met een verlopen token.

Zorg dat een verlopen token niet simpelweg een exception en `500 Internal Server Error` oplevert.

---

## 2. Security zonder username in de URL

Bekijk nog eens endpoints zoals:

```text
DELETE /lobbies/{id}/players/{username}
```

Als de backend via authentication al weet wie de gebruiker is, hebben we `{username}` dan altijd nodig?

Kun je een API ontwerpen waarbij een ingelogde gebruiker expliciet een actie voor **zichzelf** uitvoert, zonder de eigen
username opnieuw mee te sturen?

Vergelijk beide ontwerpen.

---

## 3. Security tests

Tot nu toe testen we veel met het `.http`-bestand.

Voeg geautomatiseerde tests toe voor minimaal:

```text
geen token         → 401
ongeldig token     → 401
USER → USER actie  → toegestaan
USER → ADMIN actie → 403
ADMIN → ADMIN actie → toegestaan
```

Het interessante gedeelte is niet alleen dat je controller werkt.

Je wilt kunnen aantonen dat een gebruiker iets **niet** kan.

---

## 4. Wie vertrouw je?

We krijgen dezelfde informatie soms uit verschillende bronnen.

Bijvoorbeeld:

```text
Request body:
username = BobChunker

JWT:
username = AliceDeVries
```

Welke van deze twee identiteiten vertrouw je?

Wat zou er fout gaan als je voor authorization alleen de username uit de requestbody gebruikt?

Probeer een request te maken waarmee je een slecht beveiligde versie van je eigen API zou kunnen misleiden.

**Fix daarna het probleem.**

---

# Klaar?

Controleer voor je commit minimaal:

- [ ] Een gebruiker kan registreren.
- [ ] Wachtwoorden worden niet als plain text opgeslagen.
- [ ] Een gebruiker kan inloggen.
- [ ] Een succesvolle login geeft een JWT.
- [ ] `/auth/register` en `/auth/login` zijn publiek.
- [ ] Andere endpoints zijn beveiligd.
- [ ] Je JWT-filter valideert het token.
- [ ] Een succesvolle authenticatie komt in de `SecurityContext`.
- [ ] Je begrijpt waarom `chain.doFilter(...)` nodig is.
- [ ] Requests zonder geldige authentication worden geweigerd.
- [ ] ADMIN-only functionaliteit is daadwerkelijk alleen voor admins.
- [ ] Een USER kan niet zomaar handelen namens een andere USER.
- [ ] Je kunt het verschil tussen authentication en authorization uitleggen.
- [ ] Je kunt het verschil tussen `401` en `403` uitleggen.

**Commit je werk.**
