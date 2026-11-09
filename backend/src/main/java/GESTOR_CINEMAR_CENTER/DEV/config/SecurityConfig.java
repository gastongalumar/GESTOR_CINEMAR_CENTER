package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.security.JwtAuthenticationFilter;
import GESTOR_CINEMAR_CENTER.DEV.security.AccesoDenegadoHandler;
import GESTOR_CINEMAR_CENTER.DEV.security.AutenticacionJson;
import GESTOR_CINEMAR_CENTER.DEV.security.oauth2.CustomOAuth2UserService;
import GESTOR_CINEMAR_CENTER.DEV.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
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

    /**
     * Cadena exclusiva del flujo OAuth2 con Google.
     *
     * <p>Sólo intercepta las rutas del handshake OAuth2 y mantiene una sesión
     * HTTP para guardar el authorization request entre la ida y la vuelta a
     * Google. El resto de la aplicación sigue usando la cadena JWT stateless.</p>
     *
     * <p>Los beans OAuth2 se reciben como parámetros del método (y no en el
     * constructor) para evitar un ciclo de beans: {@code CustomOAuth2UserService}
     * depende del {@code PasswordEncoder} definido en esta misma configuración.</p>
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2SecurityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
            ClientRegistrationRepository clientRegistrationRepository,
            @Value("${app.oauth2.failure-uri:http://localhost:4200/login?error=google}") String failureUri) throws Exception {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization");

        // Fuerza a Google a mostrar el selector de cuentas en cada login.
        // Sin esto, si el navegador tiene una sesión de Google activa, la reutiliza
        // y no permite elegir otra cuenta.
        authorizationRequestResolver.setAuthorizationRequestCustomizer(builder ->
                builder.additionalParameters(params -> params.put("prompt", "select_account")));

        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestResolver(authorizationRequestResolver))
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2AuthenticationSuccessHandler)
                        .failureUrl(failureUri)
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
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
                        .requestMatchers(HttpMethod.GET, "/api/customizacion", "/api/customizacion/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/candy-combos/activos").permitAll()

                        // 🔓 PÚBLICOS — catálogo de películas (listados y ficha)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/vigentes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/proximamente").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/{id:\\d+}").permitAll()

                        // 🔓 PÚBLICOS — catálogo de funciones (listados)
                        .requestMatchers(HttpMethod.GET, "/api/funciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/funciones/vigentes").permitAll()

                        // 👑 SOLO SUPERADMIN — registro de administradores / superadmins
                        .requestMatchers(HttpMethod.POST, "/api/auth/registro-admin").hasAuthority("SUPERADMIN")

                        // 👥 AUTENTICADO — filtros de películas
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").authenticated()

                        // 👥 AUTENTICADO — consulta detalle de funciones (por id, por película, ocupados)
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").authenticated()

                        // 👤 SOLO CLIENTE — holds de asientos
                        .requestMatchers(HttpMethod.POST, "/api/holds", "/api/holds/**").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/holds", "/api/holds/**").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/codigos-descuento/validar").hasAuthority("CLIENTE")

                        // 👥 AUTENTICADO — consulta de salas
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").authenticated()

                        // 👤 SOLO CLIENTE — reservas y pagos
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pagos/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagos/crear-preferencia").hasAuthority("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAuthority("CLIENTE")

                        // 👥 CLIENTE o ADMIN — reservas propias y tickets
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mis").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/ticket/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/ticket/*/pago").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pagos/mis").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — CRUD películas (incluye POST /{id}/imagen)
                        .requestMatchers(HttpMethod.POST, "/api/peliculas", "/api/peliculas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas", "/api/peliculas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas", "/api/peliculas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — CRUD funciones
                        .requestMatchers(HttpMethod.POST, "/api/funciones", "/api/funciones/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/funciones", "/api/funciones/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones", "/api/funciones/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — CRUD salas
                        .requestMatchers(HttpMethod.POST, "/api/salas", "/api/salas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/salas", "/api/salas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas", "/api/salas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/candy-combos", "/api/candy-combos/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/candy-combos", "/api/candy-combos/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/candy-combos", "/api/candy-combos/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/candy-combos", "/api/candy-combos/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/codigos-descuento", "/api/codigos-descuento/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/codigos-descuento", "/api/codigos-descuento/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/codigos-descuento", "/api/codigos-descuento/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/codigos-descuento", "/api/codigos-descuento/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — personalización del sitio
                        .requestMatchers(HttpMethod.POST, "/api/customizacion", "/api/customizacion/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/customizacion", "/api/customizacion/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/customizacion", "/api/customizacion/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — moderación de reservas
                        .requestMatchers(HttpMethod.GET, "/api/reservas/admin/todas").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/admin/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/cliente/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/validar/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — estadísticas
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas", "/api/estadisticas/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — consulta de pagos
                        .requestMatchers(HttpMethod.GET, "/api/pagos", "/api/pagos/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")

                        // 👤 Perfil propio (cliente o admin)
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/mi-nombre").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/me").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/me/foto").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/me/foto").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPERADMIN")

                        // 👑 SOLO ADMIN — gestión de usuarios
                        .requestMatchers(HttpMethod.GET, "/api/usuarios", "/api/usuarios/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/usuarios/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios", "/api/usuarios/**").hasAnyAuthority("ADMINISTRADOR", "SUPERADMIN")
                        // 👑 SOLO SUPERADMIN — eliminar administradores / superadmins
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios", "/api/usuarios/**").hasAuthority("SUPERADMIN")

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