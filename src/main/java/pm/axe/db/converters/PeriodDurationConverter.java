package pm.axe.db.converters;

import org.threeten.extra.PeriodDuration;
import pm.axe.Axe;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts {@link PeriodDuration} object to {@link String} and back.
 */
@Converter(autoApply = true)
public class PeriodDurationConverter implements AttributeConverter<PeriodDuration, String> {
    @Override
    public String convertToDatabaseColumn(final PeriodDuration duration) {
        return duration == null ? null : duration.toString();
    }

    @Override
    public PeriodDuration convertToEntityAttribute(final String dbData) {
        return dbData == null ? Axe.Defaults.LOGIN_SESSION_DURATION : PeriodDuration.parse(dbData);
    }
}
