package com.example.trainschedule.controller;

import com.example.trainschedule.model.Schedule;
import com.example.trainschedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public void getScheduleByLine() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleService.findByLine("Lakeshore")).thenReturn(scheduleList);

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("Lakeshore", null);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
        verify(scheduleService, times(1)).findByLine("Lakeshore");
    }

    @Test
    public void getScheduleByLine_notFound() {
        when(scheduleService.findByLine("InvalidLine")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("InvalidLine", null);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCode().value());
        verify(scheduleService, times(1)).findByLine("InvalidLine");
    }

    @Test
    public void getScheduleByLineAndDeparture() {
        Schedule schedule = new Schedule(15L, "Kitchener", 1215, 1300);
        when(scheduleService.findByLineAndDeparture("Kitchener", "1215")).thenReturn(Optional.of(schedule));

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("Kitchener", "1215");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(schedule, responseEntity.getBody().get(0));
        verify(scheduleService, times(1)).findByLineAndDeparture("Kitchener", "1215");
    }

    @Test
    public void getScheduleByLineAndDeparture_notFound() {
        when(scheduleService.findByLineAndDeparture("InvalidLine", "1215")).thenReturn(Optional.empty());

        ResponseEntity<List<Schedule>> responseEntity = scheduleController.getScheduleByLineWithOptionalDeparture("InvalidLine", "1215");

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCode().value());
        verify(scheduleService, times(1)).findByLineAndDeparture("InvalidLine", "1215");
    }
}