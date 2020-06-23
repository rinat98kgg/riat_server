package com.riatServer.config;

import com.riatServer.security.jwt.JwtConfigurer;
import com.riatServer.security.jwt.JwtTokenProvider;
import com.riatServer.ui.views.login.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@SpringComponent
@EnableWebSecurity
@Configuration
@ComponentScan("com.riatServer.security.jwt")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private static final String ADMIN_ENDPOINT = "/api/v1/admin/";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGOUT_SUCCESS_URL = "/login";
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_PROCESSING_URL = "/login";

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomRequestCache requestCache() { //
        return new CustomRequestCache();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                //.httpBasic().disable()
                .requestCache().requestCache(requestCache())
                //.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()

                // Allow all flow internal requests.
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .anyRequest().authenticated()

                // Configure the login page.
                .and().formLogin()
                .loginPage("/" + LoginView.ROUTE).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)

                // Configure logout
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                // Configure remember me cookie
                //.and().rememberMe().key("pssssst").alwaysRemember(true)

                .and().apply(new JwtConfigurer(jwtTokenProvider))
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");


    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // Client-side JS
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/img/**",
                //"/offline.html",

                // icons and images
                "/icons/**",
                "/images/**",
                "/styles/**");

        // (development mode) H2 debugging console
        //"/h2-console/**");
    }
}
