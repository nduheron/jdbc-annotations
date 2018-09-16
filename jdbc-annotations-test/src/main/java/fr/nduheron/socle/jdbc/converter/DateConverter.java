package fr.nduheron.socle.jdbc.converter;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DateConverter implements Converter<Date, LocalDate> {

	@Override
	public LocalDate convert(Date source) {
		if (source != null) {
			return source.toLocalDate();
		}
		return null;
	}

}
