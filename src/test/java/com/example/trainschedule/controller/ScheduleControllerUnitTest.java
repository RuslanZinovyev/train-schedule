package com.example.trainschedule.controller;

import com.example.trainschedule.entity.Schedule;
import com.example.trainschedule.service.ScheduleService;
import com.example.trainschedule.service.TimeConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerUnitTest {
    @InjectMocks
    private ScheduleController scheduleController;

    @Mock
    private ScheduleService scheduleService;
    @Mock
    private TimeConversionService timeConversionService;

    @Test
    public void getAllSchedules() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        Page<Schedule> schedulePage = new PageImpl<>(scheduleList);
        when(scheduleService.findAll(any(Pageable.class))).thenReturn(schedulePage);

        Page<Schedule> result = scheduleController.getAllSchedules(0, 5, "id");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(scheduleService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void getScheduleByLineWithOptionalDepartureNoDeparture() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleService.findByLine("Lakeshore")).thenReturn(scheduleList);

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("Lakeshore", null);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
        verify(scheduleService, times(1)).findByLine("Lakeshore");
        verify(timeConversionService, never()).validateAndConvertDepartureTime(anyString());
    }

    @Test
    public void getScheduleByLineWithOptionalDepartureWithDeparture() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleService.findByLine("Lakeshore")).thenReturn(scheduleList);
        when(timeConversionService.validateAndConvertDepartureTime("800")).thenReturn(800);

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("Lakeshore", "800");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(schedule1, responseEntity.getBody().get(0));
        verify(scheduleService, times(1)).findByLine("Lakeshore");
        verify(timeConversionService, times(1)).validateAndConvertDepartureTime("800");
    }

    @Test
    public void getScheduleByLineWithOptionalDepartureNotFound() {
        when(scheduleService.findByLine("InvalidLine")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("InvalidLine", null);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(scheduleService, times(1)).findByLine("InvalidLine");
        verify(timeConversionService, never()).validateAndConvertDepartureTime(anyString());
    }

    @Test
    public void getScheduleByLineWithOptionalDepartureInvalidDeparture() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleService.findByLine("Lakeshore")).thenReturn(scheduleList);
        when(timeConversionService.validateAndConvertDepartureTime("invalid")).thenReturn(null);

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("Lakeshore", "invalid");

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCode().value());
        verify(scheduleService, times(1)).findByLine("Lakeshore");
        verify(timeConversionService, times(1)).validateAndConvertDepartureTime("invalid");
    }

}