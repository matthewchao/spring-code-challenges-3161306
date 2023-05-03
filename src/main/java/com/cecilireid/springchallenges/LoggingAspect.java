package com.cecilireid.springchallenges;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(pointcut = "execution(CateringJob com.cecilireid.springchallenges.CateringJobController.getCateringJobById(Long))"
            , returning = "cateringJob")
    public void logResponses(CateringJob cateringJob) {
        logger.info("Response: " + cateringJob.toString());
    }

    @Before("execution(CateringJob com.cecilireid.springchallenges.CateringJobController.getCateringJobById(Long)) && args(id)")
    public void logRequest(Long id) {
        logger.info("Request: {}", id);
    }
}
