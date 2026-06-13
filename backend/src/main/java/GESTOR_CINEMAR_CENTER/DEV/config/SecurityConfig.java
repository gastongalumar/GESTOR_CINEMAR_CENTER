package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ──────────────────────────────────────────────
                        // 🔓 ENDPOINTS 100% PÚBLICOS (sin token)
                        // ──────────────────────────────────────────────

                        // Swagger / OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Error handler por defecto
                        .requestMatchers("/error").permitAll()
                        // Archivos estáticos subidos (imágenes de películas, logos, fondos)
                        .requestMatchers("/uploads/**").permitAll()
                        // Autenticación: login y registro (no requieren token)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Customización: consultar configuración pública (GET)
                        .requestMatchers(HttpMethod.GET, "/api/customizacion").permitAll()
                        // Películas: consultas GET públicas (cartelera, detalles)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        // Funciones: consultas GET públicas (horarios, disponibilidad)
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").permitAll()
                        // Salas: consultas GET públicas
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").permitAll()
                        // Reservas: consultar ticket, pago asociado, validar ticket
                        // y asientos ocupados NO requieren autenticación
                        .requestMatchers(HttpMethod.GET, "/api/reservas/ticket/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reservas/validar/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reservas/funcion/*/ocupados").permitAll()

                        // ──────────────────────────────────────────────
                        // 🔒 ENDPOINTS SOLO PARA ADMINISTRADORES
                        // ──────────────────────────────────────────────
                        .requestMatchers("/api/pagos/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR")

                        // ──────────────────────────────────────────────
                        // 🛡️ CUALQUIER OTRA COSA → Requiere autenticación
                        //     (sin importar el rol). Si no hay token JWT
                        //     válido, Spring retorna 401 automáticamente.
                        // ──────────────────────────────────────────────
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
