package it.vitalegi.cosucce.user.service;

import com.pengrad.telegrambot.UpdatesListener;

public interface TelegramProxy {
    void sendMessage(long to, String message);

    void setUpdatesListener(UpdatesListener listener);
}
