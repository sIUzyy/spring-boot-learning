package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

/*
* This is your security configuration class. You'll define how to handle login, JWT tokens, password encoding, etc.
* @Configuration	This class is a configuration file. It defines beans (like reusable components).
* @EnableWebSecurity	Enables Spring Security in your project. Tells Spring to apply web security rules.
*/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /*
    * Spring will automatically inject your custom UserDetailsServiceImpl class here.
    * This service is responsible for loading user details during login.
    */
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    /*
    * This is a custom class that handles unauthorized access.
    * For example, if someone sends a request without a valid token, this class decides how to respond (like sending a 401 Unauthorized response).
    */
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // user repository
    @Autowired
    UserRepository userRepository;

    // role repository
    @Autowired
    RoleRepository roleRepository;


    /*
    * AuthTokenFilter is a custom filter that intercepts HTTP requests to check for a valid JWT token.
    * It gets called before Spring processes the request, and it decides if the request should go through.
    */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /*
    * This is a built-in Spring class that:
    * Uses your UserDetailsService to load users from the database
    * Uses your PasswordEncoder to check the password
    */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /*
    * The AuthenticationManager is the main engine that handles authentication.
    * It uses the DaoAuthenticationProvider under the hood.
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /*
    * It's a secure way to hash passwords.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }


    /*
    * This method sets up security rules for your whole web app.
    */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF (Cross Site Request Forgery) protection is not needed for most APIs, especially stateless ones.
        http.csrf(csrf -> csrf.disable())
                // If someone tries to access a protected endpoint without logging in, Spring will call unauthorizedHandler.
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // You’re telling Spring: “Don’t use HTTP sessions to store logged-in users.”
                // Instead, every request must include a JWT token. That’s called stateless authentication.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Anyone can access:
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/admin/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .anyRequest().authenticated()
                );
        // Plug in our own authenticationProvider (which uses your UserDetailsService + PasswordEncoder).
        http.authenticationProvider(authenticationProvider());

        // “Before you process the login form or anything else, check the JWT token in the request header.”
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // enable frames for h2 console to show
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));

        // You return the complete security filter setup so Spring can apply it.
        return http.build();
    }

    /*
    * This code is telling Spring Security to completely ignore certain paths — meaning it will not even try to secure them at all.
    * WebSecurityCustomizer: It's a way to customize low-level security, especially for resources that should be completely ignored by Spring Security.
    */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Don’t apply any security to these paths. Let them be freely accessed.
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    /*
    * @Bean: Marks this method as a bean managed by Spring.
    * CommandLineRunner: A special Spring Boot interface that runs code after the app starts.
    * Takes in RoleRepository, UserRepository, and PasswordEncoder as dependencies.
    */
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            /*
            * Tries to find the ROLE_USER in the database.
            * If not found, it creates and saves it.
            */
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            /*
             * Tries to find the ROLE_SELLER in the database.
             * If not found, it creates and saves it.
             */
            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            /*
             * Tries to find the ROLE_ADMIN in the database.
             * If not found, it creates and saves it.
             */
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            // These sets represent which roles each user should have.
            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


           /*
           * Creates user1 only if it doesn't already exist.
           * Same logic is repeated for seller1 and admin.
           * All passwords are encoded using passwordEncoder.
           */
            if (!userRepository.existsByusername("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByusername("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByusername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            /*
            * Finds the user and assigns them the correct roles.
            * Repeats this for seller1 and admin.
            * Using ifPresent() avoids null pointer exceptions.
            */
            userRepository.findByusername("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByusername("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByusername("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }

}
