package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.dto.AddBoardAccountDto;
import it.vitalegi.cosucce.budget.dto.AddBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.AddBoardDto;
import it.vitalegi.cosucce.budget.dto.AddBoardEntryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardAccountDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardEntryDto;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.model.BoardAccount;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static java.time.Instant.now;

@Service
@ActiveProfiles("test")
public class BudgetUtil {
    public static final BigDecimal AMOUNT1 = new BigDecimal("10");
    public static final BigDecimal AMOUNT2 = new BigDecimal("20.55");

    public static final String DESCRIPTION1 = "DESCRIPTION1";
    public static final String DESCRIPTION2 = "DESCRIPTION2";

    public static final String ETAG1 = "ETAG1";
    public static final String ETAG2 = "ETAG2";
    public static final String ETAG3 = "ETAG3";

    public static final String ICON1 = "ICON1";
    public static final String ICON2 = "ICON2";

    public static final String LABEL1 = "LABEL1";
    public static final String LABEL2 = "LABEL2";

    public static final String NAME1 = "NAME1";
    public static final String NAME2 = "NAME2";

    @Autowired
    BoardService boardService;

    // BOARDS

    public Board.BoardBuilder board1() {
        return Board.builder().boardId(uuid()).name(NAME1).etag(ETAG1).creationDate(now()).lastUpdate(now());
    }

    public Board.BoardBuilder board2() {
        return Board.builder().boardId(uuid()).name(NAME2).etag(ETAG2).creationDate(now()).lastUpdate(now());
    }

    public Board addBoard1(UUID userId) {
        return boardService.addBoard(addBoardDto1().build(), userId);
    }

    public Board addBoard2(UUID userId) {
        return boardService.addBoard(addBoardDto2().build(), userId);
    }

    public AddBoardDto.AddBoardDtoBuilder addBoardDto1() {
        return AddBoardDto.builder().boardId(uuid()).name(NAME1).etag(ETAG1);
    }

    public AddBoardDto.AddBoardDtoBuilder addBoardDto2() {
        return AddBoardDto.builder().boardId(uuid()).name(NAME2).etag(ETAG2);
    }

    public UpdateBoardDto.UpdateBoardDtoBuilder updateBoardDto1() {
        return UpdateBoardDto.builder().name(NAME2).etag(ETAG1).newETag(ETAG2);
    }

    // ACCOUNTS

    public BoardAccount.BoardAccountBuilder account1() {
        return BoardAccount.builder().boardId(uuid()).icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1).creationDate(now()).lastUpdate(now());
    }

    public BoardAccount.BoardAccountBuilder account2() {
        return BoardAccount.builder().boardId(uuid()).icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2).creationDate(now()).lastUpdate(now());
    }

    public AddBoardAccountDto.AddBoardAccountDtoBuilder addBoardAccountDto1() {
        return AddBoardAccountDto.builder().accountId(uuid()).icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1);
    }

    public AddBoardAccountDto.AddBoardAccountDtoBuilder addBoardAccountDto2() {
        return AddBoardAccountDto.builder().accountId(uuid()).icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2);
    }

    public UpdateBoardAccountDto.UpdateBoardAccountDtoBuilder updateBoardAccountDto1() {
        return UpdateBoardAccountDto.builder().icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1).newETag(ETAG2);
    }

    public UpdateBoardAccountDto.UpdateBoardAccountDtoBuilder updateBoardAccountDto2() {
        return UpdateBoardAccountDto.builder().icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2).newETag(ETAG3);
    }

    // CATEGORIES

    public BoardCategory.BoardCategoryBuilder category1() {
        return BoardCategory.builder().boardId(uuid()).icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1).creationDate(now()).lastUpdate(now());
    }

    public BoardCategory.BoardCategoryBuilder category2() {
        return BoardCategory.builder().boardId(uuid()).icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2).creationDate(now()).lastUpdate(now());
    }

    public AddBoardCategoryDto.AddBoardCategoryDtoBuilder addBoardCategoryDto1() {
        return AddBoardCategoryDto.builder().categoryId(uuid()).icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1);
    }

    public AddBoardCategoryDto.AddBoardCategoryDtoBuilder addBoardCategoryDto2() {
        return AddBoardCategoryDto.builder().categoryId(uuid()).icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2);
    }

    public UpdateBoardCategoryDto.UpdateBoardCategoryDtoBuilder updateBoardCategoryDto1() {
        return UpdateBoardCategoryDto.builder().icon(ICON1).label(LABEL1).enabled(true).etag(ETAG1).newETag(ETAG2);
    }

    public UpdateBoardCategoryDto.UpdateBoardCategoryDtoBuilder updateBoardCategoryDto2() {
        return UpdateBoardCategoryDto.builder().icon(ICON2).label(LABEL2).enabled(true).etag(ETAG2).newETag(ETAG3);
    }

    // ENTRIES

    public BoardEntry.BoardEntryBuilder entry1() {
        return BoardEntry.builder().entryId(uuid()).boardId(uuid()).accountId(uuid()).categoryId(uuid()).description(DESCRIPTION1).amount(AMOUNT1).etag(ETAG1).creationDate(now()).lastUpdate(now());
    }

    public BoardEntry.BoardEntryBuilder entry2() {
        return BoardEntry.builder().entryId(uuid()).boardId(uuid()).accountId(uuid()).categoryId(uuid()).description(DESCRIPTION2).amount(AMOUNT2).etag(ETAG2).creationDate(now()).lastUpdate(now());
    }

    public AddBoardEntryDto.AddBoardEntryDtoBuilder addBoardEntryDto1(UUID accountId, UUID categoryId) {
        return AddBoardEntryDto.builder().entryId(uuid()).accountId(accountId).categoryId(categoryId).description(DESCRIPTION1).amount(AMOUNT1).etag(ETAG1);
    }

    public AddBoardEntryDto.AddBoardEntryDtoBuilder addBoardEntryDto2(UUID accountId, UUID categoryId) {
        return AddBoardEntryDto.builder().entryId(uuid()).accountId(accountId).categoryId(categoryId).description(DESCRIPTION2).amount(AMOUNT2).etag(ETAG2);
    }

    public UpdateBoardEntryDto.UpdateBoardEntryDtoBuilder updateBoardEntryDto1() {
        return UpdateBoardEntryDto.builder().description(DESCRIPTION1).amount(AMOUNT1).etag(ETAG1).newETag(ETAG2);
    }

    public UpdateBoardEntryDto.UpdateBoardEntryDtoBuilder updateBoardEntryDto2() {
        return UpdateBoardEntryDto.builder().description(DESCRIPTION2).amount(AMOUNT2).etag(ETAG2).newETag(ETAG3);
    }

    public UUID uuid() {
        return UUID.randomUUID();
    }
}
