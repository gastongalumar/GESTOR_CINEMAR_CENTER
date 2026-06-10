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
                        // Autenticación y Documentación (Público)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Customización (Lectura pública, Escritura Admin)
                        .requestMatchers(HttpMethod.GET, "/api/customizacion", "/api/customizacion/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")

                        // Funciones (Lectura pública, Escritura/Eliminación Admin)
                        .requestMatchers(HttpMethod.GET, "/api/funciones", "/api/funciones/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasAuthority("ADMINISTRADOR")

                        // Películas (Lectura Autenticada, Escritura Admin)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas", "/api/peliculas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")

                        // Salas y Asientos (Lectura pública, Escritura Admin)
                        .requestMatchers(HttpMethod.GET, "/api/salas", "/api/salas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasAuthority("ADMINISTRADOR")

                        // Recursos estáticos (Imágenes subidas)
                        .requestMatchers("/uploads/**").permitAll()

                        // Reservas
                        .requestMatchers("/api/reservas/funcion/**").permitAll() // Consulta de asientos libres
                        .requestMatchers("/api/reservas/*/pdf").permitAll() // Descarga de PDF
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("CLIENTE") // Crear reserva (Cliente)
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/ticket/*/pago").hasAuthority("CLIENTE") // Cambiar método pago (Cliente)
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/ticket/*").hasAnyAuthority("CLIENTE", "ADMINISTRADOR") // Cancelar reserva
                        .requestMatchers("/api/reservas/validar/**").hasAuthority("ADMINISTRADOR") // Validar ticket (Admin)
                        .requestMatchers("/api/reservas/cliente/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR") // Ver historial
                        .requestMatchers("/api/reservas/ticket/**").authenticated() // Ver detalles de ticket

                        // Pagos
                        .requestMatchers("/api/pagos/**").hasAuthority("ADMINISTRADOR")

                        // Usuarios
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR")

                        // Administración
                        .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRADOR")

                        // Actuator / Monitoreo
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasAuthority("ADMINISTRADOR")

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