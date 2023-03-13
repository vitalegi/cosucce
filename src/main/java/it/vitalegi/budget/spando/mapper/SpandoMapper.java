package it.vitalegi.budget.spando.mapper;

import it.vitalegi.budget.spando.constant.SpandoDay;
import it.vitalegi.budget.spando.dto.SpandoEntry;
import it.vitalegi.budget.spando.entity.SpandoEntryEntity;
import org.springframework.stereotype.Service;

@Service
public class SpandoMapper {

    public SpandoEntry map(SpandoEntryEntity entity) {
        SpandoEntry out = new SpandoEntry();
        out.setEntryId(entity.getId());
        out.setDate(entity.getEntryDate());
        out.setType(map(entity.getType()));
        return out;
    }

    public SpandoDay map(String type) {
        if (type == null) {
            return null;
        }
        return SpandoDay.valueOf(type);
    }
}
