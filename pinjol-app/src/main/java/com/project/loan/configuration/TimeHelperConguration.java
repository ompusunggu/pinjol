package com.project.loan.configuration;

import com.project.loan.command.helper.TimeHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeHelperConguration {

  @Bean
    public TimeHelper timeHelper() {
        return new TimeHelper();
    }
}
