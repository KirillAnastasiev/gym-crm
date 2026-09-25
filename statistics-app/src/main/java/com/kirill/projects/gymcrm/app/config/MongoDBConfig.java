package com.kirill.projects.gymcrm.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Configuration
public class MongoDBConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                List.of(
                        new YearToIntegerConverter(),
                        new IntegerToYearConverter(),
                        new MonthToIntegerConverter(),
                        new IntegerToMonthConverter(),
                        new DurationToLongConverter(),
                        new LongToDurationConverter()
                )
        );
    }

    @WritingConverter
    private static class MonthToIntegerConverter implements Converter<Month, Integer> {
        @Override
        public Integer convert(Month source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    private static class IntegerToMonthConverter implements Converter<Integer, Month> {
        @Override
        public Month convert(Integer source) {
            if (source != null && source >= 1 && source <= 12) {
                return Month.of(source);
            }
            throw new IllegalArgumentException("Invalid month value: " + source);
        }
    }

    @WritingConverter
    private static class YearToIntegerConverter implements Converter<Year, Integer> {
        @Override
        public Integer convert(Year source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    private static class IntegerToYearConverter implements Converter<Integer, Year> {
        @Override
        public Year convert(Integer source) {
            return Year.of(source);
        }
    }

    @WritingConverter
    private static class DurationToLongConverter implements Converter<Duration, Long> {
        @Override
        public Long convert(Duration source) {
            return source.toMillis();
        }
    }

    @ReadingConverter
    private static class LongToDurationConverter implements Converter<Long, Duration> {
        @Override
        public Duration convert(Long source) {
            return Duration.ofMillis(source);
        }
    }

}
