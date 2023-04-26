package com.example.trainschedule.controller;

import com.example.trainschedule.entity.Schedule;
import com.example.trainschedule.service.ScheduleService;
import com.example.trainschedule.service.TimeConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/go-train-api/v1/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final TimeConversionService timeConversionService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService,
                              TimeConversionService timeConversionService) {
        this.scheduleService = scheduleService;
        this.timeConversionService = timeConversionService;
    }

    /*
        I've decided to add pagination to improve efficiency in case working with large dataset,
        better user experience and flexibility.
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

        List<Schedule> schedules = scheduleService.findByLine(line);

        if (schedules.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (departure != null) {
            Integer convertedDeparture = timeConversionService.validateAndConvertDepartureTime(departure);
            if (convertedDeparture != null) {
                schedules = schedules.stream()
                        .filter(schedule -> convertedDeparture.equals(schedule.getDeparture()))
                        .collect(Collectors.toList());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(schedules);
    }
}
