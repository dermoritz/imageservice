package de.ml.persitence;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime convert( String source ) {
        return ZonedDateTime.parse( source, DateTimeFormatter.ISO_OFFSET_DATE_TIME );
    }
}
