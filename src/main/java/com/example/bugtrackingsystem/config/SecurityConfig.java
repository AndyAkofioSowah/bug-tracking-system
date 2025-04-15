package com.example.bugtrackingsystem.config;

import com.example.bugtrackingsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/update")

                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/welcome-bg.jpg", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/register", "/login", "/h2-console/**", "/view-image/**", "/view-video/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard").hasRole("USER")
                        .anyRequest().authenticated()
                )


                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/admin");
                            } else {
                                response.sendRedirect("/dashboard");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }



    @Bean
    public CommandLineRunner createAdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                com.example.bugtrackingsystem.entity.User admin =
                        new com.example.bugtrackingsystem.entity.User("admin", passwordEncoder.encode("adminpassword"), "ADMIN");
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("user") == null) {
                com.example.bugtrackingsystem.entity.User user =
                        new com.example.bugtrackingsystem.entity.User("user", passwordEncoder.encode("userpassword"), "USER");
                userRepository.save(user);
            }
        };
    }



    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            com.example.bugtrackingsystem.entity.User user = userRepository.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().replace("ROLE_", "")) // remove if already prefixed
                    .build();

        };
    }










    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


}
