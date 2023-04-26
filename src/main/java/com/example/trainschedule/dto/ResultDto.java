package com.example.trainschedule.dto;

import lombok.Data;

@Data
public class ResultDto<T> {
    private String message;
    private T data;

    public ResultDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
