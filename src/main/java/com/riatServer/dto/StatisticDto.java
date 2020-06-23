package com.riatServer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatisticDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime date;
    String name;

    public StatisticDto toStatisticDto(){
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setDate(date);
        statisticDto.setName(name);
        return statisticDto;
    }

    public static StatisticDto fromStatisticDto(LocalDateTime date, String name){
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setName(name);
        statisticDto.setDate(date);
        return statisticDto;
    }
}