package jungmo.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 기본적으로 실행할 스레드 개수
        executor.setMaxPoolSize(10);   // 최대 스레드 개수
        executor.setQueueCapacity(100); // 큐 크기 (초과 시 요청은 대기)
        executor.setThreadNamePrefix("Notification-Thread-"); // 스레드 이름 설정
        executor.initialize();
        return executor;
    }
}

