package it.vitalegi.cosucce.budget.service;

import it.vitalegi.cosucce.budget.mapper.BoardMapper;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.repository.BoardCategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class BoardCategoryService {
    BoardCategoryRepository boardCategoryRepository;
    BoardMapper boardMapper;

    public List<BoardCategory> getBoardCategories(UUID boardId) {
        return boardCategoryRepository.findAllByBoardId(boardId).stream().map(boardMapper::toCategory).toList();
    }
}
