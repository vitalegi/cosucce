package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import it.vitalegi.cosucce.budget.repository.BoardEntryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class BoardEntryService {
    BoardEntryRepository boardEntryRepository;
    BoardMapper boardMapper;

    public List<BoardEntry> getBoardEntries(UUID boardId) {
        return boardEntryRepository.findAllByBoardId(boardId).stream().map(boardMapper::toEntry).toList();
    }
}
