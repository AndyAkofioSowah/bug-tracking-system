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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


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
                        .requestMatchers("/overseer/**").hasAuthority("OVERSEER")
                        .requestMatchers("/welcome-bg.jpg", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/register", "/login", "/h2-console/**", "/view-image/**", "/view-video/**").permitAll()
                        .requestMatchers("/dashboard/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/dashboard/**", "/dashboard").hasAuthority("USER")
                        .requestMatchers("/bug/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/register", "/login", "/css/**", "/api/verify-company").permitAll()

                        .anyRequest().authenticated()


                )



                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            authentication.getAuthorities().forEach(auth -> {
                                System.out.println(" Logged in with authority: " + auth.getAuthority());
                            });

                            String role = authentication.getAuthorities().iterator().next().getAuthority();

                            if (role.equals("ADMIN")) {
                                response.sendRedirect("/dashboard/admin");
                            } else if (role.equals("OVERSEER")) {
                                response.sendRedirect("/overseer/messages");
                            } else {
                                response.sendRedirect("/dashboard");
                            }
                        })




                        .failureHandler((request, response, exception) -> {
                            System.out.println(" Login failed: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect("/login?error=true");
                        })

                        .permitAll()
                )

                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )




                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // keep POST only
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")

                        .permitAll()
                );





        return http.build();
    }






    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return loginInput -> {
            System.out.println(" Attempting login with email: " + loginInput);
            com.example.bugtrackingsystem.entity.User user = userRepository.findByEmailWithBugs(loginInput);

            if (user == null) {
                System.out.println(" No user found for email: " + loginInput);
                throw new UsernameNotFoundException("User not found: " + loginInput);
            }

            System.out.println(" Found user: " + user.getUsername() + " | Role: " + user.getRole());

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getRole())  //
                    .build();
        };
    }






    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }




}
