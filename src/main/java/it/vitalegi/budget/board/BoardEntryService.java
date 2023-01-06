package it.vitalegi.budget.board;

import it.vitalegi.budget.board.dto.BoardEntry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardEntryService {
    public UUID addEntry(BoardEntry boardEntry) {
        return UUID.randomUUID();
    }
}
