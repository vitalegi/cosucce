package it.vitalegi.cosucce.board.service;

import it.vitalegi.cosucce.board.dto.BoardEntry;
import it.vitalegi.cosucce.board.repository.BoardUserRepository;
import it.vitalegi.cosucce.user.entity.UserEntity;
import it.vitalegi.cosucce.user.service.UserService;
import it.vitalegi.metrics.Performance;
import it.vitalegi.metrics.Type;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Performance(Type.SERVICE)
@Log4j2
@Service
public class BoardNotificationService {
    @Autowired
    BoardUserRepository boardUserRepository;

    @Autowired
    UserService userService;

    public void notifyAddBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
    }

    public void notifyDeleteBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
    }

    public void notifyUpdateBoardEntry(BoardEntry entry, UserEntity author) {
        String owner = userService.getUserEntity(entry.getOwnerId()).getUsername();
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

}
