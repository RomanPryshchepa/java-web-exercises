package com.bobocode;

import com.bobocode.annotation.EnableStringTrimming;
import com.bobocode.annotation.Trimmed;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with
 * {@link Trimmed} annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with
 * {@link Trimmed}. For example if there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java"
 * if parameter is marked with {@link Trimmed} annotation.
 * <p>
 * <p>
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        boolean present = Arrays.stream(bean.getClass().getDeclaredMethods())
                .flatMap(method -> Arrays.stream(method.getParameters()))
                .filter(parameter -> parameter.isAnnotationPresent(Trimmed.class))
                .anyMatch(parameter -> parameter.getType().equals(String.class));
        if (present) {
            MethodInterceptor methodInterceptor = (obj, method, args, proxy) -> {
                Parameter[] params = method.getParameters();
                for (int k = 0; k < params.length; k++) {
                    for (Annotation annotation : params[k].getDeclaredAnnotations()) {
                        if (params[k].getType().equals(String.class)
                                && "Trimmed".equals(annotation.annotationType().getSimpleName())) {
                            args[k] = ((String) args[k]).trim();
                        }
                    }
                }
                return proxy.invokeSuper(obj, args);
            };
            return Enhancer.create(bean.getClass(), methodInterceptor);

        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
