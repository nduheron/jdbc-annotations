package fr.nduheron.socle.jdbc.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpperCaseConverter implements Converter<String, String> {

	@Override
	public String convert(String source) {
		return StringUtils.upperCase(source);
	}

}
