package com.example.trainschedule.service;

import com.example.trainschedule.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    Page<Schedule> findAll(Pageable pageable);
    List<Schedule> findByLine(String line);
    Optional<Schedule> findByLineAndDeparture(String line, String departure);
}
