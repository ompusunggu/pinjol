package com.project.loan;

import com.project.loan.configuration.LoanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(value = {LoanProperties.class})
@SpringBootApplication
public class PinjolApplication {

  public static void main(String[] args) {
    SpringApplication.run(PinjolApplication.class, args);
  }

}
