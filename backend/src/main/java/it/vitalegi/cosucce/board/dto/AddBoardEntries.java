package it.vitalegi.cosucce.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddBoardEntries {

    List<BoardEntry> entries;
}
