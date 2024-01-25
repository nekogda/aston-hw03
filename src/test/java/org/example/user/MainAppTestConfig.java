package org.example.user;

import org.example.user.app.port.out.UserRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("org.example.user.app")
public class MainAppTestConfig {

    @Bean
    @Primary
    public UserRepository userRepositoryMock() {
        return Mockito.mock(UserRepository.class);
    }
}