package eu.bbmri_eric.quality.agent.common;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Production-grade async configuration for event handling.
 *
 * <p>Configures thread pools with reasonable settings for handling asynchronous events:
 * - Core pool size: 5 threads (handles typical concurrent events)
 * - Max pool size: 20 threads (handles traffic spikes)
 * - Queue capacity: 500 tasks (buffer for peak load)
 * - Keep-alive time: 60 seconds (recycles unused threads)
 * - Thread name prefix: "async-event-" (for monitoring and debugging)
 * - Wait for tasks to complete on shutdown: enabled (graceful shutdown)
 * - Await termination: 60 seconds (max wait for shutdown)
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  private static final int CORE_POOL_SIZE = 5;
  private static final int MAX_POOL_SIZE = 20;
  private static final int QUEUE_CAPACITY = 500;
  private static final int KEEP_ALIVE_TIME_SECONDS = 60;
  private static final String THREAD_NAME_PREFIX = "async-event-";
  private static final int AWAIT_TERMINATION_SECONDS = 60;

  /**
   * Creates the primary thread pool executor for async event handling.
   *
   * @return configured ThreadPoolTaskExecutor
   */
  @Bean(name = "asyncEventExecutor")
  public Executor asyncEventExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // Core pool size: number of threads kept alive even if idle
    executor.setCorePoolSize(CORE_POOL_SIZE);

    // Max pool size: maximum number of threads that can be created
    executor.setMaxPoolSize(MAX_POOL_SIZE);

    // Queue capacity: number of tasks that can be queued before blocking
    executor.setQueueCapacity(QUEUE_CAPACITY);

    // Keep-alive time: how long to keep threads alive after task completion
    executor.setKeepAliveSeconds(KEEP_ALIVE_TIME_SECONDS);

    // Thread name prefix for monitoring and debugging
    executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

    // Wait for tasks to complete before shutting down
    executor.setWaitForTasksToCompleteOnShutdown(true);

    // Maximum time to wait for tasks to complete during shutdown
    executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);

    // RejectionPolicy: caller runs policy to handle queue overflow gracefully
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

    executor.initialize();
    return executor;
  }
}
