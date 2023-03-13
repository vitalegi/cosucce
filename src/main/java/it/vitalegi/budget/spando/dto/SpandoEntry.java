package it.vitalegi.budget.spando.dto;

import it.vitalegi.budget.spando.constant.SpandoDay;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SpandoEntry {

    UUID entryId;
    LocalDate date;
    SpandoDay type;
}
