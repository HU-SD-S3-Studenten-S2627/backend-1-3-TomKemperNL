# Security

We gaan stap-voor-stap Spring security introduceren.
Eerst zorgen we dat je een JWT-token kan maken, daarna zorgen we dat je zo'n token nodig hebt om te authenticeren.
Tot slot voegen we authorizatie-concepten toe (mag je dit vanuit je rol, of vanuit je eigen identiteit?).

## 1. Inloggen

Laten we starten met een inlog-endpoint:
(Als je een leuk idee hebt om dit resource-oriented te doen... leef je uit! We zijn benieuwd.)

**POST /players/login**

En gebruik een body als bijvoorbeeld:

```json
{
  "username": "AliceDeVries",
  "password": "AliceDeVriespwd"
}
```

We willen even niet moeilijk doen met daadwerkelijke wachtwoord-opslag etc. Dus voor nu gaan we er vanuit dat iedereens
wachtwoord `{gebruikersnaam} + "pwd"` is (of zoiets idioots, als het maar niet -exact- hetzelfde, en niet ingewikkeld is).

Als de gebruiker bestaat, en het wachtwoord "klopt", dan maken we een JWT en stuur je die terug.
Dit werkt exact hetzelfde als in S2, zelfde libraries ook.
Je kunt evt. spieken in de repository: https://github.com/HU-SD-S3/s2-in-s3/tree/security.

Voor nu kun je dit gewoon in de Controller doen als je wil, een aparte class er voor maken mag ook.
Een response als volgt is prima:

```json
{
  "token": "eyJhbGciOi..."
}
```

Stop minimaal de volgende informatie in je token:

- de identiteit van de gebruiker (als `subject`);
- een expiration-date (zeg over een dag, niet te kort iig. dat test irritant)
- (opdracht 3 voegt hier nog een rol aan toe)

Log in en kopieer het token uit de response. Gooi het Bearer token in [http://jwt.io] en kijk of daar staat wat je verwacht.

---

## 2. Authentication met Spring Security

We hebben inmiddels een token, maar onze `LobbyController` en `PlayerController` doen er nog helemaal niets mee.
Dat gaan we oplossen met **Spring Security**.

### 2a. Voeg Spring Security toe

Voeg de Spring Security dependency toe aan je `pom.xml`. Deze staat al uitgecomment klaar.

Start daarna je applicatie opnieuw en probeer bijvoorbeeld:

```http
GET http://localhost:8080/players
```

> Niet direct oplossen!
> Kijk eerst goed wat er gebeurt.
> - Krijg je dezelfde response als eerst?
> - Welke HTTP-status krijg je?
> - Wordt je `PlayerController` überhaupt nog aangeroepen?
> - Voeg eens `logging.level.org.springframework.security=DEBUG` toe aan je application.properties (en herstart!).
> - Wat staat er in de console?
> - Open [http://localhost:8080/players](http://localhost:8080/players) ook eens in je browser
> - Je kunt nu inloggen met username `user` en als wachtwoord die gekke string die in je console staat

Blijkbaar doet Spring Security al van alles, terwijl wij nog nauwelijks iets geconfigureerd hebben! Standaard staan Forms en Basic
authenticatie aan.

### 2b. SecurityFilterChain

Maak een configuratie voor Spring Security:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http){
        return http
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .build();
    }
}
```
Deze class kun je voorlopig gewoon naast de BackendApplication plaatsen. Deze code verandert nagenoeg niets aan wat we al hadden,
maar geeft ons in elk geval een plek om onze eigen meningen 'ertussen te proppen'.

> (voor de volledigheid: we hebben niet meer die gekke inlogpagina, en HTTP basic auth werkt ook niet meer, maar
> dat was zowieso al een verrassing vorige oefening)

Zorg nu dat `/players/login` voor iedereen beschikbaar is. (`permitAll()` in de chain)

Je zult daarbij uit moeten zoeken wat de volgende begrippen allemaal betekenen:

```text
requestMatchers(...)
permitAll()
authenticated()
anyRequest()
```
Het eindresultaat is dat we kunnen inloggen, maar daarna doet onze JWT nog helemaal niets...
Dit zit nog niet in het framework gelinked.

### Opdracht 2c: Bouw een JWT-filter

Nu komt het technisch ingewikkeldste gedeelte van deze opdracht.
We gaan een **filter** toevoegen aan die security-filter-chain.

Maak een `JwtFilter extends OncePerRequestFilter` dat voor ieder beveiligd request de `Authorization`-header controleert.
Deze kun je aan de chain toevoegen met `http.addFilterAfter(jouwFilter, LogoutFilter.class);`: dat zet jouw filter
vrij vroeg in de chain.

Vervolgens kun je het filter gaan testen met een request zoals:

```http
GET /players/AliceDeVries
Authorization: Bearer eyJhbGciOi...
```

Je filter moet grofweg het volgende doen:

1. Lees de `Authorization`-header.
2. Controleer of deze met `Bearer ` begint.
3. Haal het JWT uit de header.
4. Controleer of het JWT geldig is.
5. Haal de identiteit uit het token.
6. Vertel Spring welke gebruiker geauthentiseerd is.
7. Geef het request door aan de rest van de chain.

In je filter moeten we ervoor zorgen dat we 'inhaken op de authenticatie-bits' van het Spring Framework.
Dat kunnen we met de static `SecurityContextHolder.getContext().setAuthentication(...)` method.

Zo'n authentication-implementatie (`Authentication` is een interface) is zeer vergelijkbaar met de Jersey `SecurityContext`-interface.
(verwarrend genoeg is de Spring `SecurityContext` een andere dan de Jersey `SecurityContext`... andere packages, dus totaal
ander ding)

Kijk ook eens wat er gebeurt als je vergeet `chain.doFilter(request, response);` onderaan je filter aan te roepen.
Wat gaat er mis? Kun je uitleggen waarom?
In Jersey hoefde dit niet, wat zou het voordeel zijn van de chain-aanpak boven de iteratieve aanpak van Jersey?

Test nu minimaal de volgende situaties:

* Het aanroepen van een authenticated endpoint zonder of met een ongeldig token
* Het aanroepen van een authenticated endpoint met een geldig token

## 3. Authorization

We kunnen eindelijk onderscheid maken tussen:

> "Wie ben jij?"

en:

> "Mag jij dit doen?"

Dat tweede noemen we **authorization**.

# 3a: ADMIN-only endpoints

Kies 1 van de gebruikers die je hebt, en zorg dat in je login endpoint deze de Rol Admin krijgt in z'n JWT token.
Zorg ook dat je filter dit correct aan de SecurityContext toevoegt.
In Spring is een Rol een `SimpleGrantedAuthority` die start met "ROLE_".

Vervolgens zijn er (minstens) twee manieren om met rollen te werken:

1. Je kunt in de filterchain ipv. iets als `permitAll()` ook een `hasRole()` aanroepen. Let er op dat je dan NIET "ROLE_" in de rolnaam moet gebruiken.
2. Je kunt de welbekende @RolesAllowed-annotatie gebruiken. Daarvoor moet je op je SecurityContext een attribuut zetten: `@EnableMethodSecurity(jsr250Enabled = true)`

# Opdracht 3b: "Ben jij het zelf?"

Een rol is niet altijd voldoende.
Stel Alice doet:

```http
DELETE /lobbies/0/players/AliceDeVries
```

Dat lijkt redelijk.
Maar wat als Alice doet:

```http
DELETE /lobbies/0/players/BobChunker
```

Alice en Bob hebben allebei de rol `USER`. Dus een rol-check gaat niet helpen.

Je kunt in Spring extra informatie vragen door aan de controller method een extra `Authentication currentUser` parameter
toe te voegen. Met een sjiek woord noemen we dit 'Method Injection', er wordt extra informatie doorgegeven aan de controller
method. Aan die extra parameter kun je allerlei extra informatie vragen.

> In de voorbeeldcode gebruiken we nog een extra laagje over die Authentication heen, een MethodArgumentResolver. Dat
> is ietsje netter, maar niet essentieel.

Naast het leaven van een Lobby zijn er een heleboel plekken waar een het kunnen uitlezen van de ingelogde gebruiker
nuttig is. Dus zorg dat je de ingelogde gebruiker gebruikt bij:

* Het maken van een lobby, die wordt gehost door de ingelogde gebruiker
* Het joinen van een lobby, dan hoef je geen body meer mee te geven
* (en misschien nog een paar anderen?)