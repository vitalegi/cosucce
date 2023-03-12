package it.vitalegi.budget;

import it.vitalegi.budget.it.TelegramProxyMock;
import it.vitalegi.budget.user.service.TelegramProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class TestConfiguration {

    @Bean
    @Primary
    public TelegramProxy telegramProxyMock() {
        return new TelegramProxyMock();
    }
}
