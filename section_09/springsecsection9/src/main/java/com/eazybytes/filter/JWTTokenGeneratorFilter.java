package com.eazybytes.filter;

/*
Questo filtro particolare viene utilizzato per generare un token JWT (JSON Web Token) dopo l'autenticazione dell'utente.
 */

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Questa riga recupera l'oggetto Authentication corrente dal contesto di sicurezza.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Qui inizia un blocco if che controlla se l'autenticazione non è nulla, cioè se l'utente è autenticato.
        if (null != authentication) {
            //Questa riga genera una chiave segreta per firmare il token JWT, usando una chiave segreta predefinita e l'algoritmo HMAC SHA.
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            /*
            Le righe seguenti costruiscono il token JWT con varie affermazioni,
            inclusi l'emittente, il soggetto, il nome utente, le autorità,
            l'ora di emissione e l'ora di scadenza.
            Alla fine, il token viene firmato con la chiave segreta
            e trasformato in una stringa compatta.
             */
            String jwt = Jwts.builder().setIssuer("Eazy Bank").setSubject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 30000000))
                    .signWith(key).compact();
            // Questa riga imposta l'header di risposta con il token JWT.
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        // Questa riga permette alla richiesta di passare al filtro successivo nella catena.
        filterChain.doFilter(request, response);
    }

    /*
    Questo filtro deve essere eseguito solo durante la fase di login, ovvero quando faccio la richiesta /user
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user");
    }

    /*
    Questo è un metodo helper privato che prende una collezione di autorità
    e le trasforma in una stringa.
    Le righe seguenti raccolgono tutte le autorità dell'utente in un Set
    e le uniscono in una singola stringa separata da virgole.
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

}
