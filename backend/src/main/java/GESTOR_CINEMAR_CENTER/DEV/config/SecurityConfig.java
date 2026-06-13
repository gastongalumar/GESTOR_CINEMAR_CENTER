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

                        // 🔓 ENDPOINTS PÚBLICOS
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/customizacion").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()

                        // 👥 CUALQUIER USUARIO AUTENTICADO
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservas/funcion/*/ocupados").authenticated()

                        // 👤 SOLO CLIENTE
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mis").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/ticket/**").hasAuthority("CLIENTE")

                        // 👑 SOLO ADMIN - CRUD
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasAuthority("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasAuthority("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN - MODERACIÓN
                        .requestMatchers(HttpMethod.GET, "/api/reservas/admin/todas").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/cliente/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/**").hasAuthority("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/ocupacion/sala/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/ocupacion/funcion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/ocupacion/pelicula/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/reservas/por-fecha").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/ingresos/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/dashboard/resumen").hasAuthority("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/reservas/validar/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN - USUARIOS
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAuthority("ADMINISTRADOR")

                        // 🚫 CUALQUIER OTRA RUTA
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