package it.vitalegi.budget.resource;

import io.swagger.v3.oas.annotations.Operation;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.spando.dto.SpandoDays;
import it.vitalegi.budget.spando.dto.SpandoEntry;
import it.vitalegi.budget.spando.service.SpandoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Log4j2
@RestController
@RequestMapping("/spando")
@Performance(Type.ENDPOINT)
public class SpandoResource {

    @Autowired
    SpandoService spandoService;

    @Operation(summary = "Update an entry")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SpandoEntry changeSpandoEntry(@RequestBody SpandoEntry entry) {
        return spandoService.addOrUpdateSpandoEntry(entry);
    }

    @Operation(summary = "Delete an entry")
    @DeleteMapping(path = "/{date}")
    public void deleteSpandoEntry(@PathVariable("date") String date) {
        spandoService.deleteSpandoEntry(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Operation(summary = "Retrieve spando periods")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SpandoDays> getSpandos() {
        return spandoService.getSpandoDays();
    }

}
