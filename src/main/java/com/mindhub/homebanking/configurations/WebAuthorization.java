package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization {

    private static final String[] AUTH_WHITELIST = {
            "/web/js/**", "/web/css/**", "/web/img/**"
    } ;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()

                .antMatchers("/web/**").permitAll()
                .antMatchers( HttpMethod.POST, "/api/login", "/api/logout", "/api/clients").permitAll()
                .antMatchers(  "/api/transaction/**",
                        "/api/transaction",
                        "/api/loans",
                        "/api/accounts",
                        "/api/clients/current/accounts")
                .hasAnyAuthority("CLIENT", "ADMIN")
                .antMatchers( HttpMethod.POST, "/api/clients/current/**",
                        "/api/transactions/**",
                        "/api/loans").
                hasAnyAuthority("CLIENT", "ADMIN")

                .antMatchers("/manager.html", "/rest/**").hasAuthority("ADMIN")
                .antMatchers("/h2-console").hasAuthority("ADMIN")

                .antMatchers("/**").hasAuthority("CLIENT");

        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout").deleteCookies("JSESSIONID");


        //disabling frameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();

        //auth failure response - Not authenticated
        http.exceptionHandling().authenticationEntryPoint( (req, res, exc) ->
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        //clear flags for auth when login is ok
        http.formLogin().successHandler( (req, res, auth) ->
                clearAuthenticationAttributes(req));

        //If login fails, just send an auth failure response
        http.formLogin().failureHandler( (req, res, exc) ->
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        //if logout is successful, just send a success response
        http.logout().logoutSuccessHandler( new HttpStatusReturningLogoutSuccessHandler());

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }


}