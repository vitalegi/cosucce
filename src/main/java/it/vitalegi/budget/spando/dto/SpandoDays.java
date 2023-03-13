package it.vitalegi.budget.spando.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SpandoDays {
    LocalDate from;
    LocalDate to;
}
