package com.turki.gamefyback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.core.convert.converter.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMongoRepositories(basePackages = "com.turki.gamefyback.repository")
public class MongoConfig {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator().getValidator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
            new LocalDateTimeToDateConverter(),
            new DateToLocalDateTimeConverter()
        ));
    }

    // Stores LocalDateTime as-is without UTC conversion
    static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        @Override
        public Date convert(LocalDateTime source) {
            // Treat the LocalDateTime as being in Tunis timezone
            // ZonedDateTime zoned = source.atZone(ZoneId.of("Africa/Tunis"));
            // Convert to Date without changing the wall time
            // return Date.from(zoned.withZoneSameLocal(ZoneId.of("UTC")).toInstant());
            return Date.from(source.atZone(ZoneId.of("Africa/Tunis")).toInstant());

        }
    }

    // Reads back as LocalDateTime in Tunis time
    static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        @Override
        public LocalDateTime convert(Date source) {
            // Interpret the stored UTC date as Tunis time
            return source.toInstant()
                // .atZone(ZoneId.of("UTC"))
                .atZone(ZoneId.of("Africa/Tunis"))
                .toLocalDateTime();
        }
    }
}