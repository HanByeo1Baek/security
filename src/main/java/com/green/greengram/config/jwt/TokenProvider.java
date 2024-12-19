package com.green.greengram.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;


@Service
public class TokenProvider {
    private final ObjectMapper objectMapper; // JackSon 라이브러리
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenProvider(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperties.getSecretKey()));
    }

    // JWT 생성
    public String generateToken(JwtUser jwtUser, Duration expiredAt) {
        Date now = new Date();
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));
    }

    private String makeToken(JwtUser jwtUser, Date expiry) {
        //JWT 암호화
        JwtBuilder builder = Jwts.builder();
        JwtBuilder.BuilderHeader header = builder.header();
        header.type("JWT");

        builder.issuer(jwtProperties.getIssuer());

        return Jwts.builder()
                .header().add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(expiry)
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    private String makeClaimByUserToString(JwtUser jwtUser){
        //객체 자체를 JWT 에 담고 싶어서 객체를 직렬화
        //jwtUser 에 담고 있는 데이터를 JSON 형태의 문자열로 변환
        try{
            return objectMapper.writeValueAsString(jwtUser);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String token) {
        try{
            //JWT 복호화
            getClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //Spring Security 에서 인증 처리를 해주어야 한다. 그때 Authentication 객체가 필요.
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        //return new UsernamePasswordAuthenticationToken(cliams, null, authorities);
        return userDetails == null ?
                null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails getUserDetailsFromToken(String token){
        Claims claims = getClaims(token);
        String json = (String)claims.get("signedUser");
        JwtUser jwtUser = null;
        try {
            jwtUser = objectMapper.readValue(json, JwtUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

}