package it.vitalegi.cosucce.board.service;

import it.vitalegi.cosucce.board.dto.BoardEntry;
import it.vitalegi.cosucce.board.entity.BoardUserEntity;
import it.vitalegi.cosucce.board.repository.BoardUserRepository;
import it.vitalegi.metrics.Performance;
import it.vitalegi.metrics.Type;
import it.vitalegi.cosucce.user.entity.UserEntity;
import it.vitalegi.cosucce.user.service.TelegramProxy;
import it.vitalegi.cosucce.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardNotificationService {
    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    UserService userService;
    @Autowired
    TelegramProxy telegramProxy;

    public void notifyAddBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
        notifyBoardUsers(entry.getBoardId(), "Nuova spesa di " + author.getUsername() + ": " + formatBoardEntry(entry
                , owner));
    }

    public void notifyDeleteBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
        notifyBoardUsers(entry.getBoardId(),
                "Spesa eliminata da " + author.getUsername() + ": " + formatBoardEntry(entry, owner));
    }

    public void notifyUpdateBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
        notifyBoardUsers(entry.getBoardId(),
                "Spesa modificata da " + author.getUsername() + ": " + formatBoardEntry(entry, owner));
    }

    protected String formatAmount(BigDecimal amount) {
        return amount.toPlainString() + "€";
    }

    protected String formatBoardEntry(BoardEntry entry, String entryOwner) {
        String date = formatDate(entry.getDate());
        String amount = formatAmount(entry.getAmount());
        return date + " / " + amount + " / " + entryOwner + " / " + entry.getCategory() + " / " + entry.getDescription() + " / " + entry.getId();
    }

    protected String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    protected void notifyBoardUser(long telegramUserId, String message) {
        try {
            telegramProxy.sendMessage(telegramUserId, message);
        } catch (Exception e) {
            log.error("Failed to send message to " + telegramUserId + ". Message: " + message, e);
        }
    }

    protected void notifyBoardUsers(UUID boardId, String message) {
        List<BoardUserEntity> users = boardUserRepository.findByBoard_Id(boardId);
        users.stream().map(BoardUserEntity::getUser).map(UserEntity::getTelegramUserId).filter(Objects::nonNull)
             .forEach(telegramUserId -> notifyBoardUser(telegramUserId, message));
    }
}
