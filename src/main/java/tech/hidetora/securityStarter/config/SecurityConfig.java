package tech.hidetora.securityStarter.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.hidetora.securityStarter.Filter.JwtAuthenticationFilter;
import tech.hidetora.securityStarter.handler.CustomAccessDeniedHandler;
import tech.hidetora.securityStarter.handler.CustomAuthenticationHandler;
import tech.hidetora.securityStarter.utils.AuthoritiesConstants;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
//                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(authz ->
                                // prettier-ignore
                                authz
                                        .requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")).permitAll()
                                        .requestMatchers(mvc.pattern("/")).permitAll()
                                        .requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")).permitAll()
                                        .requestMatchers(mvc.pattern("/app/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/i18n/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/content/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/api-docs/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/account/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/h2-console/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/api/v1/auth/authorities")).hasAuthority(AuthoritiesConstants.ADMIN)
                                        .requestMatchers(mvc.pattern("/api/v1/users/**")).permitAll()
                                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                                        .requestMatchers(mvc.pattern("/api/v1/auth/reset-password/init")).permitAll()
                                        .requestMatchers(mvc.pattern("/api/v1/auth/reset-password/finish")).permitAll()
                                        .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                                        .requestMatchers(mvc.pattern("/api/**")).authenticated()
                                        .requestMatchers(mvc.pattern("/v3/api-docs/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/management/health")).permitAll()
                                        .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                                        .requestMatchers(mvc.pattern("/management/info")).permitAll()
                                        .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                                        .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//                                .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling(exceptions ->
//                        exceptions
//                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
//                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
//                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(ex-> ex
                        .authenticationEntryPoint(new CustomAuthenticationHandler())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );
//        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT))) {
//            http.authorizeHttpRequests(authz -> authz.requestMatchers(antMatcher("/h2-console/**")).permitAll());
//        }
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.addExposedHeader("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        UrlBasedCorsConfigurationSource corsConfigurationSource=new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return corsConfigurationSource;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
