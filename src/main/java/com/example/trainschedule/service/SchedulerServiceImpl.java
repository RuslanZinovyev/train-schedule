package com.example.trainschedule.service;

import com.example.trainschedule.entity.Schedule;
import com.example.trainschedule.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SchedulerServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeConversionService timeConversionService;

    @Autowired
    public SchedulerServiceImpl(ScheduleRepository scheduleRepository,
                                TimeConversionService timeConversionService) {
        this.scheduleRepository = scheduleRepository;
        this.timeConversionService = timeConversionService;
    }

    @Override
    @Cacheable("schedules")
    public Page<Schedule> findAll(Pageable pageable) {
        log.info("Get all schedules on page = {}", pageable.getPageNumber());
        return scheduleRepository.findAll(pageable);
    }

    @Override
    @Cacheable("schedulesByLine")
    public List<Schedule> findByLine(String line) {
        log.info("Fetching schedules from the database for line: {}", line);
        return scheduleRepository.findByLine(line);
    }

    @Override
    @Cacheable(value = "schedulesByLineAndDeparture", key = "#line + #departure")
    public Optional<Schedule> findByLineAndDeparture(String line, String departure) {
        log.info("Fetching schedules from the database for line: {} and optional departute {}", line, departure);
        Integer convertedDeparture = timeConversionService.validateAndConvertDepartureTime(departure);

        if (convertedDeparture == null) {
            throw new IllegalArgumentException("Invalid departure time format");
        }

        return scheduleRepository.findByLineAndDeparture(line, convertedDeparture);
    }
}
