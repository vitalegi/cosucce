package it.vitalegi.cosucce.user.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import it.vitalegi.cosucce.metrics.Performance;
import it.vitalegi.cosucce.metrics.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Performance(Type.SERVICE)
@Service
public class TelegramProxyImpl implements TelegramProxy {

    protected TelegramBot bot;
    @Value("${TELEGRAM_TOKEN}")
    String token;

    @Override
    public void sendMessage(long to, String message) {
        SendMessage request = new SendMessage(to, message);
        bot.execute(request);
    }

    @Override
    public void setUpdatesListener(UpdatesListener listener) {
        bot.setUpdatesListener(listener);
    }

    @PostConstruct
    protected void init() {
        log.info("Initialize Telegram Bot");
        bot = new TelegramBot(token);
        log.info("Initialized");
    }
}
