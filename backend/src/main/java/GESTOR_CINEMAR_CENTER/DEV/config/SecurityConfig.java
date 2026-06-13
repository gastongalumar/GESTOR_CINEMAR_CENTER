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

                        // ══════════════════════════════════════════════
                        // 🔓 ENDPOINTS PÚBLICOS (sin token requerido)
                        //    Swagger, error handler, login/registro,
                        //    y datos de consulta que la UI necesita
                        //    mostrar antes de autenticarse (logo, colores,
                        //    cartelera, horarios)
                        // ══════════════════════════════════════════════
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── Datos públicos de consulta ────────────────
                        // La UI carga logo, colores y nombre del cine
                        // incluso en la pantalla de login
                        .requestMatchers(HttpMethod.GET, "/api/customizacion").permitAll()
                        // Cartelera de películas visible sin login
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        // Horarios de funciones visibles sin login
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").permitAll()
                        // Consulta de salas
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").permitAll()
                        // Asientos ocupados (ver disponibilidad sin login)
                        .requestMatchers(HttpMethod.GET, "/api/reservas/funcion/*/ocupados").permitAll()

                        // ══════════════════════════════════════════════
                        // 👥 ENDPOINTS PARA CLIENTE Y ADMINISTRADOR
                        //    (requieren token JWT + rol CLIENTE o ADMIN)
                        // ══════════════════════════════════════════════

                        // ── Reservas ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mis")
                            .hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservas")
                            .hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/reservas/ticket/**")
                            .hasAnyAuthority("CLIENTE", "ADMINISTRADOR")

                        // ══════════════════════════════════════════════
                        // 🔒 ENDPOINTS SOLO ADMINISTRADORES
                        // ══════════════════════════════════════════════

                        // ── Películas (escritura) ─────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")

                        // ── Funciones (escritura) ─────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasAuthority("ADMINISTRADOR")

                        // ── Salas (escritura) ─────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasAuthority("ADMINISTRADOR")

                        // ── Customización (escritura) ─────────────────
                        .requestMatchers(HttpMethod.POST, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")

                        // ── Validar ticket ────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/reservas/validar/**").hasAuthority("ADMINISTRADOR")

                        // ── Listar reservas por cliente ───────────────
                        .requestMatchers(HttpMethod.GET, "/api/reservas/cliente/**").hasAuthority("ADMINISTRADOR")

                        // ── Pagos ─────────────────────────────────────
                        .requestMatchers("/api/pagos/**").hasAuthority("ADMINISTRADOR")

                        // ── Usuarios ──────────────────────────────────
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR")

                        // ══════════════════════════════════════════════
                        // 🚫 CUALQUIER OTRA RUTA → Denegar acceso (403)
                        // ══════════════════════════════════════════════
                        .anyRequest().denyAll()
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
