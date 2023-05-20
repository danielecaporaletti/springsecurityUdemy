package com.eazybytes.filter;

/*
Questa classe `JWTTokenValidatorFilter` è un filtro che si occupa della validazione di un token JWT (JSON Web Token).
Viene applicato per ogni richiesta HTTP (da qui l'estensione di `OncePerRequestFilter`)
e verifica la validità del token JWT presente nell'header della richiesta.
 */

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter  extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Questa riga recupera il token JWT dall'header della richiesta HTTP.
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        // Qui inizia un blocco if che controlla se il token JWT non è nullo, cioè se esiste un token JWT nell'header della richiesta.
        if (null != jwt) {
            /*
            Le righe seguenti tentano di validare il token JWT.
            Questo include la generazione di una chiave segreta,
            la decodifica del token, il recupero delle rivendicazioni (claims) dal token,
            il recupero del nome utente e delle autorità dalle rivendicazioni,
            la creazione di un oggetto Authentication
            e l'impostazione dell'oggetto Authentication nel contesto di sicurezza.
             */
            try {
                SecretKey key = Keys.hmacShaKeyFor(
                        SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();
                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("authorities");
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!");
            }

        }
        // Questa riga permette alla richiesta di passare al filtro successivo nella catena.
        filterChain.doFilter(request, response);
    }

    /*
    Questo dice che questo filtro va eseguito per tutte le api, tranne durante l'operazione di login, quindi quando facciamo il get di /user
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/user");
    }

}
