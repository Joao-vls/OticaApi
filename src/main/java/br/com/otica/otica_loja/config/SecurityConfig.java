package br.com.otica.otica_loja.config;

import br.com.otica.otica_loja.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Regras Globais e Públicas (Mais específicas primeiro)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/admin/auth/**", "/api/auth/**", "/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos/**", "/categorias/**", "/marcas/**", "/home/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/avaliacoes/produto/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/webhook").permitAll()

                        // 2. Área do Cliente
                        .requestMatchers("/cliente/**", "/api/cliente/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers("/api/avaliacoes/pode-avaliar/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers("/api/pagamentos/**").hasAnyRole("CLIENTE", "ADMIN")

                        // 3. Regras Específicas do PDV / Balcão (ATENDENTE, GERENTE, ADMIN)
                        // Garante que o atendente consiga criar pedido e buscar produtos
                        .requestMatchers(HttpMethod.POST, "/admin/pedidos/fisico").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")
                        .requestMatchers(HttpMethod.GET, "/admin/pedidos/pdv/busca").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")
                        .requestMatchers(HttpMethod.GET, "/admin/produtos/pdv/busca").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")

                        // 4. Regras Genéricas da Área Administrativa
                        // GET liberado para Admin, Gerente e Atendente (visualização)
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")

                        // Modificações gerais (POST, PUT, PATCH, DELETE) travadas para ADMIN por padrão
                        // Se um atendente precisar de permissão em outro lugar (ex: alterar status), deve ser criada uma regra específica ACIMA desta linha.
                        .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")

                        // 5. Qualquer outra requisição precisa de autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}