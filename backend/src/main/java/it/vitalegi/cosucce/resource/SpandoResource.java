package it.vitalegi.cosucce.resource;

import it.vitalegi.cosucce.spando.dto.SpandoDays;
import it.vitalegi.cosucce.spando.dto.SpandoEntry;
import it.vitalegi.cosucce.spando.service.SpandoService;
import it.vitalegi.metrics.Performance;
import it.vitalegi.metrics.Type;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(path = "/{date}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public SpandoEntry changeSpandoEntry(@PathVariable("date") String date) {
        return spandoService.addOrUpdateSpandoEntry(parseDate(date));
    }

    @DeleteMapping(path = "/{date}")
    public void deleteSpandoEntry(@PathVariable("date") String date) {
        spandoService.deleteSpandoEntry(parseDate(date));
    }

    @GetMapping(path = "/estimate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SpandoDays> getSpandoEstimates() {
        return spandoService.getSpandoEstimates();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SpandoDays> getSpandos() {
        return spandoService.getSpandoDays();
    }

    protected LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
