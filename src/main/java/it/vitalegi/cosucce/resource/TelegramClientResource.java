package it.vitalegi.cosucce.resource;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import it.vitalegi.cosucce.metrics.Performance;
import it.vitalegi.cosucce.metrics.Type;
import it.vitalegi.cosucce.user.dto.OtpResponse;
import it.vitalegi.cosucce.user.dto.User;
import it.vitalegi.cosucce.user.dto.UserOtp;
import it.vitalegi.cosucce.user.service.TelegramProxy;
import it.vitalegi.cosucce.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Performance(Type.SERVICE)
@Service
public class TelegramClientResource {

    @Autowired
    TelegramProxy telegramProxy;

    @Autowired
    UserService userService;

    @PostConstruct
    protected void init() {
        telegramProxy.setUpdatesListener(this::processAllUpdates);
        log.info("Initialized");
    }

    protected int processAllUpdates(List<Update> updates) {
        updates.forEach(this::processUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    protected void processUpdate(Update update) {
        var message = update.message();
        var authorId = message.from().id();
        String text = message.text();
        registerUser(authorId, text);
    }

    protected void registerUser(long telegramUserId, String otp) {
        OtpResponse otpResponse = userService.useOtp(null, otp.trim());
        if (otpResponse.isStatus()) {
            UserOtp userOtp = otpResponse.getOtp();
            log.info("OTP is valid, connect telegram user {} with user {}", telegramUserId, userOtp.getUserId());
            User user = userService.updateTelegramUserId(userOtp.getUserId(), telegramUserId);
            telegramProxy.sendMessage(telegramUserId, "Account collegato a " + user.getUsername());
        } else {
            telegramProxy.sendMessage(telegramUserId,
                    "Non è stato possibile collegare l'account. Possibili cause " + "d'errore: il codice è scaduto " +
                            "(dura 5 minuti); hai copiato un codice errato; l'utente non è " + "abilitato.");
        }
    }
}
