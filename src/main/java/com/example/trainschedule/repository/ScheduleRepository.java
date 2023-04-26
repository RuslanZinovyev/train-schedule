package com.example.trainschedule.repository;

import com.example.trainschedule.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findAll(Pageable pageable);
    List<Schedule> findByLine(String line);
    Optional<Schedule> findByLineAndDeparture(String line, Integer departure);
}
