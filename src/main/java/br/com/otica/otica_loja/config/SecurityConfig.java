package br.com.otica.otica_loja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita o CSRF porque em APIs REST locais geralmente não precisamos dele
                .csrf(csrf -> csrf.disable())
                // Configura as regras de autorização de requisições
                .authorizeHttpRequests(auth -> auth
                        // Libera totalmente qualquer rota que comece com /home/
                        .requestMatchers("/home/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        // Qualquer outra rota exigirá autenticação (se você quiser travar o resto)
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}