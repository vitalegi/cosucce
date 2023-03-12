package it.vitalegi.budget.it;

import com.pengrad.telegrambot.UpdatesListener;
import it.vitalegi.budget.user.service.TelegramProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

public class TelegramProxyMock implements TelegramProxy {

    @Override
    public void sendMessage(long to, String message) {

    }

    @Override
    public void setUpdatesListener(UpdatesListener listener) {

    }
}
