package com.example.trainschedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Line cannot be null or empty")
    private String line;

    @Min(value = 0, message = "Departure time must be a positive integer")
    private Integer departure;

    @Min(value = 0, message = "Arrival time must be a positive integer")
    private Integer arrival;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(id, schedule.id) && Objects.equals(line, schedule.line) && Objects.equals(departure, schedule.departure) && Objects.equals(arrival, schedule.arrival);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, departure, arrival);
    }
}
