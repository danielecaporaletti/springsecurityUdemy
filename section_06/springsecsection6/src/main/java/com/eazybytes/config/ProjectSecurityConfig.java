package com.eazybytes.config;

import com.eazybytes.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //Create a new object of `CsrfTokenRequestAttributeHandler` to manage CSFR token in request
        /*
        Questo è un componente di Spring Security che viene utilizzato per gestire gli attributi di richiesta del token CSRF.
        Quando un token CSRF viene creato, questo gestore lo inserisce come attributo nella richiesta HTTP,
        in modo che possa essere recuperato in un secondo momento per la validazione.
         */
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        //Put the attribute of CSFR request as "_csrf"
        /*
        Quando il token CSRF viene aggiunto alla richiesta HTTP, viene salvato con il nome "_csrf".
        Questo significa che quando la richiesta raggiunge il server, il token CSRF può essere recuperato dall'attributo di richiesta "_csrf" per la validazione.
        Questo metodo consente di personalizzare il nome dell'attributo di richiesta utilizzato per memorizzare il token CSRF.
        Se non si imposta un nome personalizzato, Spring Security utilizzerà un nome predefinito.
         */
        requestHandler.setCsrfRequestAttributeName("_csrf");

        //configure HTTP secure policy
        /*
        Evita il salvataggio esplicito del contesto di sicurezza.
         */
        http.securityContext().requireExplicitSave(false)
                /*
                Questa riga configura la politica di creazione della sessione a "ALWAYS",
                il che significa che una sessione sarà sempre creata se non esiste.
                 */
                .and().sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                //CORS config
                /*
                Configura CORS (Cross-Origin Resource Sharing).
                Questo blocco di codice consente richieste cross-domain solo da "http://localhost:xxxx" e consente tutti i metodi e headers.
                 */
                .cors().configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) { //Questo metodo viene definito per restituire un oggetto `CorsConfiguration` in base alla richiesta HTTP fornita.
                        CorsConfiguration config = new CorsConfiguration();  //Qui viene creato un nuovo oggetto `CorsConfiguration`. Questo oggetto contiene i dettagli delle configurazioni CORS che saranno applicate.
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); //Questo metodo imposta l'origine CORS consentita. In questo caso, solo le richieste provenienti da "http://localhost:xxxx" saranno accettate.
                        config.setAllowedMethods(Collections.singletonList("*")); //Questo metodo imposta i metodi HTTP consentiti. L'asterisco "*" significa che tutti i metodi (GET, POST, PUT, DELETE, ecc.) sono consentiti.
                        config.setAllowCredentials(true); //Questo metodo abilita l'invio dei cookie del sito e delle credenziali HTTP nelle richieste cross-site.
                        config.setAllowedHeaders(Collections.singletonList("*")); //Questo metodo imposta quali headers HTTP possono essere inclusi nelle richieste. L'asterisco "*" significa che tutti gli headers sono consentiti.
                        config.setMaxAge(3600L); //Questo metodo imposta l'età massima del risultato di una preflight request (una richiesta di tipo OPTIONS che viene inviata prima della richiesta effettiva per verificare se la richiesta effettiva è sicura da inviare). In questo caso, è impostata a 3600 secondi, o un'ora.
                        return config; //Infine, l'oggetto `CorsConfiguration` configurato viene restituito.
                    }
                })
                /*
                Questo frammento di codice configura la protezione CSRF dell'applicazione.
                Viene impostato un gestore di richieste di token CSRF e vengono specificati
                i percorsi di richiesta ("/contact", "/register") che dovrebbero essere ignorati dalla protezione CSRF.
                 */
                .and().csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/contact", "/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) //viene utilizzato un `CookieCsrfTokenRepository` con HttpOnly impostato su false. Questo significa che i token CSRF vengono memorizzati nei cookie e possono essere acceduti tramite JavaScript.
                        .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) //Questa riga aggiunge un nuovo filtro, `CsrfCookieFilter`, nella catena dei filtri di sicurezza, dopo il `BasicAuthenticationFilter`. Questo filtro è responsabile della gestione dei cookie CSRF.
                //Authorisation of controller
                /*
                Questo codice configura le autorizzazioni per vari percorsi dell'applicazione.
                Le richieste a "/myAccount", "/myBalance", "/myLoans", "/myCards", "/user" richiedono che l'utente sia autenticato.
                Le richieste a "/notices", "/contact", "/register" sono permesse a tutti, indipendentemente dal fatto che l'utente sia autenticato o meno.
                 */
                .authorizeHttpRequests()
                        .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/register").permitAll()
                //Basic form login from Spring Security
                /*
                Queste righe configurano l'applicazione per supportare sia l'autenticazione tramite form di login
                che l'autenticazione HTTP Basic.
                L'autenticazione tramite form di login è un metodo di autenticazione in cui l'utente fornisce le credenziali attraverso un form HTML.
                L'autenticazione HTTP Basic è un metodo di autenticazione in cui le credenziali dell'utente vengono inviate come parte dell'intestazione della richiesta HTTP.
                 */
                .and().formLogin()
                .and().httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
