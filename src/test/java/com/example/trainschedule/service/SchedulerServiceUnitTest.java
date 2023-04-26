package com.example.trainschedule.service;

import com.example.trainschedule.model.Schedule;
import com.example.trainschedule.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceUnitTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private SchedulerServiceImpl schedulerServiceImpl;

    @Test
    public void findAll() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        Page<Schedule> schedulePage = new PageImpl<>(scheduleList);
        when(scheduleRepository.findAll(any(Pageable.class))).thenReturn(schedulePage);

        Page<Schedule> result = schedulerServiceImpl.findAll(PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(scheduleRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void findByLine() {
        Schedule schedule1 = new Schedule(1L, "Lakeshore", 800, 900);
        Schedule schedule2 = new Schedule(2L, "Lakeshore", 1000, 1100);
        List<Schedule> scheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleRepository.findByLine("Lakeshore")).thenReturn(scheduleList);

        List<Schedule> result = schedulerServiceImpl.findByLine("Lakeshore");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(scheduleRepository, times(1)).findByLine("Lakeshore");
    }

    @Test
    public void findByLineAndDeparture() {
        Schedule schedule = new Schedule(1L, "Lakeshore", 800, 900);

        when(scheduleRepository.findByLineAndDeparture("Lakeshore", 800)).thenReturn(Optional.of(schedule));

        Optional<Schedule> result = schedulerServiceImpl.findByLineAndDeparture("Lakeshore", "800");

        assertTrue(result.isPresent());
        assertEquals(schedule.getId(), result.get().getId());
        verify(scheduleRepository, times(1)).findByLineAndDeparture("Lakeshore", 800);
    }

    @Test
    public void findByLineAndDeparture_notFound() {
        when(scheduleRepository.findByLineAndDeparture("NonExistentLine", 800)).thenReturn(Optional.empty());

        Optional<Schedule> result = schedulerServiceImpl.findByLineAndDeparture("NonExistentLine", "800");

        assertFalse(result.isPresent());
        verify(scheduleRepository, times(1)).findByLineAndDeparture("NonExistentLine", 800);
    }

}