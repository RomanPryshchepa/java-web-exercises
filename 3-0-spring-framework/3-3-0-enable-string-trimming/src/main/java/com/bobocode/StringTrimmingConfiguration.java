package com.bobocode;

import org.springframework.context.annotation.Bean;

public class StringTrimmingConfiguration {
    @Bean
    public TrimmedAnnotationBeanPostProcessor getTrimmedAnnotationBeanPostProcessor() {
        return new TrimmedAnnotationBeanPostProcessor();
    }
}
