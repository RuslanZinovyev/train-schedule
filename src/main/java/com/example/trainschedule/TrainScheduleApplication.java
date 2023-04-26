package com.example.trainschedule;

import com.example.trainschedule.entity.Schedule;
import com.example.trainschedule.repository.ScheduleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class TrainScheduleApplication {

    private final ResourceLoader resourceLoader;

    public TrainScheduleApplication(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public static void main(String[] args) {
        SpringApplication.run(TrainScheduleApplication.class, args);
    }

    /*
        I've created this method to seed the database with pre-configured values in JSON format
     */
    @Bean
    CommandLineRunner init(ScheduleRepository scheduleRepository) {
        return args -> {
            Resource resource = resourceLoader.getResource("classpath:schedules.json");
            ObjectMapper objectMapper = new ObjectMapper();

            try (InputStream inputStream = resource.getInputStream()) {
                List<Schedule> schedules = objectMapper.readValue(inputStream, new TypeReference<>() {});
                scheduleRepository.saveAll(schedules);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

}
