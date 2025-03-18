package com.myBusiness.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.corePoolSize:2}")
    private int corePoolSize;

    @Value("${async.maxPoolSize:5}")
    private int maxPoolSize;

    @Value("${async.queueCapacity:500}")
    private int queueCapacity;

    @Value("${async.threadNamePrefix:AsyncThread-}")
    private String threadNamePrefix;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);

        // Establecer una política de rechazo en caso de saturación
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Configura un manejador global para excepciones en métodos @Async.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                // Se puede integrar con un sistema de logging o notificaciones
                System.err.println("Excepción en método asíncrono: " + method.getName());
                ex.printStackTrace();
            }
        };
    }
}
