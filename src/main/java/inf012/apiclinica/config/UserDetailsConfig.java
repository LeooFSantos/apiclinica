package inf012.apiclinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

        @Bean
        public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        var admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();

        var paciente = User.builder()
            .username("paciente1")
            .password(passwordEncoder.encode("pac1"))
            .roles("USER")
            .build();

        // Note: no MEDICO user created here by default. Medico accounts are created only after admin approval.

        return new InMemoryUserDetailsManager(admin, paciente);
        }
}
