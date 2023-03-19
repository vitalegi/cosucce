package it.vitalegi.cosucce.spando.dto;

import it.vitalegi.cosucce.spando.constant.SpandoDay;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SpandoEntry {

    UUID entryId;
    LocalDate date;
    SpandoDay type;
}
