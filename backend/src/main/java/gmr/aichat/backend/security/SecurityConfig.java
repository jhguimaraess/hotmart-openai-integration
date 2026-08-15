package gmr.aichat.backend.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/code",
                                "/auth/verify"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/webhook/hotmart"
                        ).permitAll()

                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${jwt.public-key-location}")
            Resource publicKeyResource,

            @Value("${jwt.private-key-location}")
            Resource privateKeyResource
    ) throws IOException {

        RSAPublicKey publicKey;

        try (InputStream inputStream =
                     publicKeyResource.getInputStream()) {

            publicKey =
                    RsaKeyConverters
                            .x509()
                            .convert(inputStream);
        }

        RSAPrivateKey privateKey;

        try (InputStream inputStream =
                     privateKeyResource.getInputStream()) {

            privateKey =
                    RsaKeyConverters
                            .pkcs8()
                            .convert(inputStream);
        }

        RSAKey rsaKey =
                new RSAKey.Builder(publicKey)
                        .privateKey(privateKey)
                        .build();

        var jwkSet =
                new ImmutableJWKSet<SecurityContext>(
                        new JWKSet(rsaKey)
                );

        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.public-key-location}")
            Resource publicKeyResource
    ) throws IOException {

        RSAPublicKey publicKey;

        try (InputStream inputStream =
                     publicKeyResource.getInputStream()) {

            publicKey =
                    RsaKeyConverters
                            .x509()
                            .convert(inputStream);
        }

        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }
}