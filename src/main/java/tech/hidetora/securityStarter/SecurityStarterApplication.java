package tech.hidetora.securityStarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.hidetora.securityStarter.config.JwtTokenParams;
import tech.hidetora.securityStarter.config.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtTokenParams.class, RsaKeyProperties.class})
public class SecurityStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityStarterApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
