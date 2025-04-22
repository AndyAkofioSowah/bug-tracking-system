package com.example.bugtrackingsystem.config;

import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.repository.CompanyRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
                        .ignoringRequestMatchers("/h2-console/**")
                        .ignoringRequestMatchers("/dashboard/admin/update")

                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/verify-company").permitAll()
                        .requestMatchers("/welcome-bg.jpg", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/register", "/login", "/h2-console/**", "/view-image/**", "/view-video/**").permitAll()
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**", "/dashboard").hasRole("USER")
                        .requestMatchers("/bug/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/register", "/login", "/css/**", "/api/verify-company").permitAll()

                        .anyRequest().authenticated()


                )



                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/dashboard/admin");
                            } else {
                                response.sendRedirect("/dashboard");
                            }
                        })
                        .permitAll()
                )

                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
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
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return loginInput -> {
            com.example.bugtrackingsystem.entity.User user = userRepository.findByEmailWithBugs(loginInput);  // Email is login key

            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + loginInput);
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())  // Spring expects this as "username"
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }





    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }




}
