package nl.hu.s3.project;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtWut {

    private static final String SECRET = "Dit is een of andere random string die in principe groot genoeg moet zijn om als key te dienen";
    public static void main(String[] args) {
        System.out.println("Wat is een JWT");

        String output =
                Jwts.builder()
                        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), Jwts.SIG.HS512)
                        .subject("Bob")
                        .compact();

        String meng = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCb2IifQ.0dn8-5N8DPji41nENa5EwUa4aOOH32ICHVoHH7ooIQtRVBMAlhlohu1IchpGjuSWs8P7gf4uq_kFLkEt73ZRYA";
        Jws<Claims> bla = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(meng);

        System.out.println(output);
        System.out.println(bla.getBody().getSubject());

        String jwt1 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJUb20ifQ.0dn8-5N8DPji41nENa5EwUa4aOOH32ICHVoHH7ooIQtRVBMAlhlohu1IchpGjuSWs8P7gf4uq_kFLkEt73ZRYA";
        String jwt2 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCb2IifQ.QuCH9A7xsptr3AD657UbkpnE4OgsW8y7d5i-6pzIjjhZ6nXfqCitzf6G2byfaaL41e25Mj3JLkxziFaB-jrA5w";


    }
}


/*
public class JwtService {
    private static final String SECRET = "Dit is een of andere random string die in principe groot genoeg moet zijn om als key te dienen";
    private final JwtParser parser;

    public JwtService() {
        this.parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build();
    }

    public String generateToken(CurrentUser user) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), Jwts.SIG.HS512)
                .issuer("HU-SD-S3")
                .expiration(Date.from(Instant.now().plus(1, DAYS)))
                .subject(user.username())
//                .claim("favoriete eten", "boerenkool") whatever je over deze user zou willen onthouden
                .compact();
    }

    public CurrentUser parseToken(String token) {
        Jws<Claims> claims = parser.parseSignedClaims(token);
        String username = claims.getPayload().getSubject();
//        String favorieteEten = claims.getPayload().get("favoriete eten", String.class);
        return new CurrentUser(username);
    }
}

 */