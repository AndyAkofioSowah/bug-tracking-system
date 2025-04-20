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
                        .requestMatchers("/welcome-bg.jpg", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/register", "/login", "/h2-console/**", "/view-image/**", "/view-video/**").permitAll()
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**", "/dashboard").hasRole("USER")
                        .requestMatchers("/bug/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()

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
    public CommandLineRunner createSampleData(UserRepository userRepository,
                                              PasswordEncoder passwordEncoder,
                                              CompanyRepository companyRepository) {
        return args -> {

            // Admin & Company 1
            if (userRepository.findByEmailWithBugs("admin1@tesco.com") == null) {
                com.example.bugtrackingsystem.entity.User admin1 = new com.example.bugtrackingsystem.entity.User();
                admin1.setUsername("admin1");
                admin1.setEmail("admin1@tesco.com");
                admin1.setPassword(passwordEncoder.encode("Akofio-sowah1!"));
                admin1.setRole("ADMIN");
                userRepository.save(admin1);



                Company company1 = new Company();
                company1.setCompanyName("Tesco");
                company1.setCompanyEmail("admin1@tesco.com"); //  SET THIS
                company1.setAdmin(admin1);
                companyRepository.save(company1);
            }

            // Admin & Company 2
            if (userRepository.findByEmailWithBugs("admin2@sainsburys.com") == null) {
                com.example.bugtrackingsystem.entity.User admin2 = new com.example.bugtrackingsystem.entity.User();
                admin2.setUsername("admin2");
                admin2.setEmail("admin2@sainsbury.com");
                admin2.setPassword(passwordEncoder.encode("Akofio-sowah1!"));
                admin2.setRole("ADMIN");
                userRepository.save(admin2);


                Company company2 = new Company();
                company2.setCompanyName("Sainsbury's");
                company2.setCompanyEmail("admin2@sainsbury.com"); //  SET THIS
                company2.setAdmin(admin2);
                companyRepository.save(company2);
            }

            // Admin & Company 3
            if (userRepository.findByEmailWithBugs("admin3@aldi.com") == null) {
                com.example.bugtrackingsystem.entity.User admin3 = new com.example.bugtrackingsystem.entity.User();
                admin3.setUsername("admin3");
                admin3.setEmail("admin3@aldi.com");
                admin3.setPassword(passwordEncoder.encode("Akofio-sowah1!"));
                admin3.setRole("ADMIN");
                userRepository.save(admin3);


                Company company3 = new Company();
                company3.setCompanyName("Aldi");
                company3.setCompanyEmail("admin3@aldi.com");
                company3.setAdmin(admin3);
                companyRepository.save(company3);
            }

            // Sample user
            if (userRepository.findByEmailWithBugs("user@email.com") == null) {
                com.example.bugtrackingsystem.entity.User user = new com.example.bugtrackingsystem.entity.User();
                user.setUsername("user");
                user.setEmail("user@email.com");
                user.setPassword(passwordEncoder.encode("Akofio-sowah1!"));
                user.setRole("USER");
                userRepository.save(user);
            }
        };
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
