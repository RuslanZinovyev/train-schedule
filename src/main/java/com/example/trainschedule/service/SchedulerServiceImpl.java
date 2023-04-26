package com.example.trainschedule.service;

import com.example.trainschedule.model.Schedule;
import com.example.trainschedule.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class SchedulerServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public SchedulerServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Cacheable("schedules")
    public Page<Schedule> findAll(Pageable pageable) {
        return scheduleRepository.findAll(pageable);
    }

    @Override
    @Cacheable("schedulesByLine")
    public List<Schedule> findByLine(String line) {
        return scheduleRepository.findByLine(line);
    }

    @Override
    @Cacheable(value = "schedulesByLineAndDeparture", key = "#line + #departure")
    public Optional<Schedule> findByLineAndDeparture(String line, String departure) {
        Integer convertedDeparture = validateAndConvertDepartureTime(departure);

        if (convertedDeparture == null) {
            throw new IllegalArgumentException("Invalid departure time format");
        }

        return scheduleRepository.findByLineAndDeparture(line, convertedDeparture);
    }

    private Integer validateAndConvertDepartureTime(String departure) {
        if (departure == null || departure.isEmpty()) {
            return null;
        }

        // Check for 24-hour format
        if (Character.isDigit(departure.charAt(0))) {
            try {
                int militaryTime = Integer.parseInt(departure);
                int hours = militaryTime / 100;
                int minutes = militaryTime % 100;

                if (hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60) {
                    return militaryTime;
                }
            } catch (NumberFormatException exception) {
                log.error("Invalid number format: ", exception);
            }
        }

        // Check for 12-hour format
        if (departure.toLowerCase().endsWith("am") || departure.toLowerCase().endsWith("pm")) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("h:mma")
                        .toFormatter(Locale.US);
                LocalTime localTime = LocalTime.parse(departure.toLowerCase(), formatter);
                return localTime.getHour() * 100 + localTime.getMinute();
            } catch (DateTimeParseException exception) {
                log.error("Invalid time format: ", exception);
            }
        }

        return null;
    }
}
