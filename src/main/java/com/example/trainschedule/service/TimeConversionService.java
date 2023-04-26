package com.example.trainschedule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/*
   Usually I am trying to avoid returning NULL since it's not a good practice, but in this scenario it's safe since we
   need to return an empty body in this case.
 */
@Slf4j
@Service
public class TimeConversionService {

    public Integer validateAndConvertDepartureTime(String departure) {
        if (departure == null || departure.isEmpty()) {
            return null;
        }

        String departureLowerCase = departure.toLowerCase();
        boolean is12HourFormat = departureLowerCase.endsWith("am") || departureLowerCase.endsWith("pm");

        // Check for 24-hour format
        if (!is12HourFormat && Character.isDigit(departure.charAt(0))) {
            try {
                int militaryTime = Integer.parseInt(departure);
                int hours = militaryTime / 100;
                int minutes = militaryTime % 100;

                if (hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60) {
                    return militaryTime;
                }
            } catch (NumberFormatException exception) {
                log.error("Invalid number format: {}", exception.getMessage());
            }
        }

        // Check for 12-hour format
        if (is12HourFormat) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("h:mma")
                        .toFormatter(Locale.US);
                LocalTime localTime = LocalTime.parse(departureLowerCase, formatter);
                return localTime.getHour() * 100 + localTime.getMinute();
            } catch (DateTimeParseException exception) {
                log.error("Invalid time format: {}", exception.getMessage());
            }
        }

        return null;
    }
}
