package com.example.securityDemo.config;

import com.example.securityDemo.jwt.AuthEntryPointJwt;
import com.example.securityDemo.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

/*
* This is a basic Spring Security configuration in Java. It's used to protect your web application, so only authenticated users can access it.
* We just get the SecurityFilterChain in SpringBootWebSecurityConfiguration and paste it in our class
* */


@Configuration // Tells Spring: “This class is a configuration class.”
@EnableWebSecurity // Enables Spring Security for your app. Without this, security features won’t be active.
@EnableMethodSecurity // enables method-level security in your Spring Boot application. I want to control who can run certain methods in my code based on their roles or permissions.
public class SecurityConfig {

    /*
    * "I'm declaring a variable named dataSource that will connect to a database."
    * DataSource is a Java interface.
    * It represents a way to connect to a database (like MySQL, PostgreSQL, etc.).
    * */
    @Autowired
    DataSource dataSource;

    // autowired the AuthEntryPointJwt class we created
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /*
    * This method registers a custom JWT filter (AuthTokenFilter) as a Spring Bean so that it can be used in the Spring Security configuration.
    * AuthTokenFilter is a class that checks JWT tokens on every request
    * authenticationJwtTokenFilter is a custom security filter in a Spring Boot application that checks for a JWT token in every incoming request,
    * validates it, and sets the authentication context if the token is valid.
    */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter(); // It returns an instance of AuthTokenFilter, which is your custom filter class that extends:
    }

    @Bean // @Bean means Spring will manage this object.
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception { // SecurityFilterChain is like a security checklist for every request.
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/h2-console/**").permitAll() // if the request im getting is match to the url i provided then permit it all
                .requestMatchers("/signin").permitAll() // if the request im getting is match to the url i provided then permit it all
                .anyRequest().authenticated()); // This says: "All requests need to be authenticated."
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // "Stateless" means: "Don't store any session at all."
        // http.formLogin(withDefaults()); // Enables the login form (a username/password page).
        // http.httpBasic(withDefaults()); // Enables Basic Authentication — a popup in the browser asking for username and password.

        // If there's an unauthorized request (user not logged in or token is invalid), then use my custom handler called unauthorizedHandler to handle the error
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler));
        /*
        * Allow this web page to be shown inside an <iframe>, but only if it comes from the same website (origin).
        * What Is frameOptions()?
        * It controls whether your website can be shown inside an <iframe>.
        * This is important for security reasons, especially to prevent something called clickjacking.
        * */
        http.headers(headers
                -> headers.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.csrf(csrf -> csrf.disable()); // Turn off CSRF security for forms and POST requests.

        /*
        * Before running the built-in UsernamePasswordAuthenticationFilter (which handles form login), run my custom filter first, called authenticationJwtTokenFilter().
        * addFilterBefore	Add a custom filter before another filter
        * authenticationJwtTokenFilter()	Your custom filter for handling JWT
        * UsernamePasswordAuthenticationFilter.class	The default login filter
        */
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build(); // Builds the security configuration and makes it active.
    }

    /*
    * UserDetailsService is a Spring Security interface that loads user details (like username, password, roles).
    * DataSource is basically the connection info to the database.
    * So this method tells Spring: "Use the database to get user information."
    */
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource); // JdbcUserDetailsManager, which is a built-in class that implements UserDetailsService and manages users using a database (through the DataSource)
    }

    /*
    * CommandLineRunner is a special Spring interface that runs code right after the application starts.
    * It takes UserDetailsService (from above) so it can use it to add users.
    * */
    @Bean
    public CommandLineRunner initData(UserDetailsService userDetailsService) {
        /*
        * It casts UserDetailsService to JdbcUserDetailsManager because it knows the implementation is that class.
        * */
        return args -> {
            JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
            UserDetails user1 = User.withUsername("user1")
                    .password(passwordEncoder().encode("password1"))
                    .roles("USER")
                    .build();
            UserDetails admin = User.withUsername("admin")
                    //.password(passwordEncoder().encode("adminPass"))
                    .password(passwordEncoder().encode("adminPass"))
                    .roles("ADMIN")
                    .build();

            JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
            userDetailsManager.createUser(user1);
            userDetailsManager.createUser(admin);
        };
    }

    // hashing method
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    * What is AuthenticationManager?
    * It's a core part of Spring Security.
    * It’s used to authenticate a user based on their username and password.
    *
    * What is AuthenticationConfiguration?
    * Spring Boot automatically configures security-related things for you.
    * AuthenticationConfiguration is a helper provided by Spring that contains the default security setup.
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager(); // Gets the built-in login/authentication handler from Spring
    }
}
