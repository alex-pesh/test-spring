package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "conversion")
public record MeasureProperties(

	Duration timeInDefaultUnit,
	Duration timeInNano,
	@DurationUnit(ChronoUnit.DAYS)
	Duration timeInDays,

	DataSize sizeInDefaultUnit,
	DataSize sizeInGB,
	@DataSizeUnit(DataUnit.TERABYTES)
	DataSize sizeInTB

) {};