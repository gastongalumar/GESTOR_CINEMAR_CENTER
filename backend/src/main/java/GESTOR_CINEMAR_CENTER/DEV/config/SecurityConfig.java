package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.security.JwtAuthenticationFilter;
import GESTOR_CINEMAR_CENTER.DEV.security.AccesoDenegadoHandler;
import GESTOR_CINEMAR_CENTER.DEV.security.AutenticacionJson;
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
    private final AccesoDenegadoHandler accesoDenegadoHandler;
    private final AutenticacionJson autenticacionJson;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          AccesoDenegadoHandler accesoDenegadoHandler,
                          AutenticacionJson autenticacionJson) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.accesoDenegadoHandler = accesoDenegadoHandler;
        this.autenticacionJson = autenticacionJson;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accesoDenegadoHandler)
                        .authenticationEntryPoint(autenticacionJson))
                .authorizeHttpRequests(auth -> auth

                        // 🔓 PÚBLICOS — documentación, archivos y auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/customizacion").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()

                        // 🔓 PÚBLICOS — catálogo de películas (listados)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/vigentes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/proximamente").permitAll()

                        // 🔓 PÚBLICOS — catálogo de funciones (listados)
                        .requestMatchers(HttpMethod.GET, "/api/funciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/funciones/vigentes").permitAll()

                        // 👑 SOLO ADMIN — registro de administradores
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro-admin").hasAuthority("ADMINISTRADOR")

                        // 👥 AUTENTICADO — consulta detalle y filtros de películas
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").authenticated()

                        // 👥 AUTENTICADO — consulta detalle de funciones (por id, por película, ocupados)
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").authenticated()

                        // 👥 AUTENTICADO — consulta de salas
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").authenticated()

                        // 👤 SOLO CLIENTE — reservas y pagos
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAuthority("CLIENTE")

                        // 👥 CLIENTE o ADMIN — reservas propias y tickets
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mis").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/ticket/*/pago").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/mis").hasAnyAuthority("CLIENTE", "ADMINISTRADOR")

                        // 👑 SOLO ADMIN — CRUD películas (incluye POST /{id}/imagen)
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — CRUD funciones
                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/funciones/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — CRUD salas
                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/salas/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — personalización del sitio
                        .requestMatchers(HttpMethod.POST, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/customizacion/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — moderación de reservas
                        .requestMatchers(HttpMethod.GET, "/api/reservas/admin/todas").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/cliente/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/validar/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — estadísticas
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — consulta de pagos
                        .requestMatchers(HttpMethod.GET, "/api/pagos/**").hasAuthority("ADMINISTRADOR")

                        // 👑 SOLO ADMIN — gestión de usuarios
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