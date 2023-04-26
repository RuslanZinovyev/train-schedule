package com.example.trainschedule.controller;

import com.example.trainschedule.entity.Schedule;
import com.example.trainschedule.dto.ResultDto;
import com.example.trainschedule.service.ScheduleService;
import com.example.trainschedule.service.TimeConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Get all schedules with pagination", description = "Retrieve a paginated list of schedules, sorted by the specified field.")
    @GetMapping
    public Page<Schedule> getAllSchedules(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return scheduleService.findAll(pageable);
    }

    /*
       I've decided to combine 2 potential request under the one endpoint and make departure time optional.
     */
    @Operation(summary = "Get schedules by line with optional departure time", description = "Retrieve schedules for a specific line, filtered by departure time if provided.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Schedules found",
                            content = @Content(schema = @Schema(implementation = Schedule.class))),
                    @ApiResponse(responseCode = "404", description = "Line not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid departure time format")
            })
    @GetMapping("/{line}")
    public ResponseEntity<ResultDto<List<Schedule>>> getScheduleByLineWithOptionalDeparture(
            @Parameter(description = "Train line") @PathVariable String line,
            @Parameter(description = "Departure time (optional)") @RequestParam(required = false) String departure) {

        List<Schedule> schedules = scheduleService.findByLine(line);

        if (schedules.isEmpty()) {
            ResultDto<List<Schedule>> errorResponse =
                    new ResultDto<>("There are no schedules for this train line. Make sure than line is correct", schedules);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        if (departure != null) {
            Integer convertedDeparture = timeConversionService.validateAndConvertDepartureTime(departure);
            if (convertedDeparture != null) {
                schedules = schedules.stream()
                        .filter(schedule -> convertedDeparture.equals(schedule.getDeparture()))
                        .collect(Collectors.toList());
            } else {
                ResultDto<List<Schedule>> errorResponse = new ResultDto<>("Time format is not correct.", null);
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        ResultDto<List<Schedule>> successResponse = new ResultDto<>("Schedules", schedules);
        return ResponseEntity.ok(successResponse);
    }
}
