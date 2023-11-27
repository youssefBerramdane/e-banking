package com.example.springcode.Security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())

                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oath->oath.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth->auth.requestMatchers("/login").permitAll())
                .authorizeHttpRequests(auth->auth.requestMatchers("/home").permitAll())
                .authorizeHttpRequests(auth->auth.requestMatchers("/favicon.ico").permitAll())
                .authorizeHttpRequests(auth->auth.requestMatchers("/registre").permitAll())
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .build();
    }
    @Bean
    public JwtDecoder jwtDecoder(){
        String sekretKey="jdpou89dhf2983023874sdjkfho287r32gls029fk0ifke90fik29209ir91ak93";
        SecretKeySpec secretKeySpec=new SecretKeySpec(sekretKey.getBytes(),"RSA");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
        //return NimbusJwtDecoder.withPublicKey(rsakeyConfig.publicKey()).build();

    }
    @Bean
    public JwtEncoder jwtEncoder(){
        String sekretKey="jdpou89dhf2983023874sdjkfho287r32gls029fk0ifke90fik29209ir91ak93";
        return new NimbusJwtEncoder(new ImmutableSecret<>(sekretKey.getBytes()));

    }
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetails, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetails);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        //corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;

    }
}
