package it.vitalegi.cosucce.it;

import com.pengrad.telegrambot.UpdatesListener;
import it.vitalegi.cosucce.user.service.TelegramProxy;

public class TelegramProxyMock implements TelegramProxy {

    @Override
    public void sendMessage(long to, String message) {

    }

    @Override
    public void setUpdatesListener(UpdatesListener listener) {

    }
}
