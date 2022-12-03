package de.oth.seproject.clubhub.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;

@Component
public class MonthEnumConverter implements Converter<String, Month> {
    @Override
    public Month convert(String monthNumber) {
        Month month;

        try {
            month = Month.of(Integer.parseInt(monthNumber));
        } catch (NumberFormatException e) {
            month = LocalDate.now().getMonth();
        }

        return month;
    }
}
