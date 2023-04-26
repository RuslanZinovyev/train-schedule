package com.example.trainschedule.controller;

import com.example.trainschedule.model.Schedule;
import com.example.trainschedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/go-train/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /*
        I've decided to add pagination to improve efficiency in case working with large dataset.
        Better user experience and flexibility.
     */
    @GetMapping
    public Page<Schedule> getAllSchedules(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return scheduleService.findAll(pageable);
    }

    /*
       I've decided to combine 2 potential request under the one endpoint and make departure time optional.
     */
    @GetMapping("/{line}")
    public ResponseEntity<List<Schedule>> getScheduleByLineWithOptionalDeparture(@PathVariable String line,
                                                                                 @RequestParam(required = false) String departure) {
        if (departure == null) {
            List<Schedule> schedules = scheduleService.findByLine(line);
            if (schedules.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(schedules);
        } else {
            Optional<Schedule> schedule = scheduleService.findByLineAndDeparture(line, departure);
            return schedule
                    .map(value -> ResponseEntity.ok(Collections.singletonList(value)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
}
