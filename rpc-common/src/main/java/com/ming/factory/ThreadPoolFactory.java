package com.ming.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author ming
 * @data 2021/6/15 17:24
 */

public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 16;

    private static final int MAX_CORE_POOL_SIZE = 64;

    private static final int KEEP_ALIVE_TIME = 2;

    private static final int BLOCKING_QUEUE_CAPACITY = 64;

    private static final RejectedExecutionHandler DEFAULT_REJECT_POLICY = new ThreadPoolExecutor.AbortPolicy();


    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static final Map<String, ExecutorService> THREAD_POOL_MAP = new ConcurrentHashMap<>();

    public ThreadPoolFactory() {
    }


    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createThreadPool(threadNamePrefix,false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix,Boolean daemon){
        //如果存在线程池就获取，没有就创建新的
        ExecutorService executorService = THREAD_POOL_MAP.computeIfAbsent(threadNamePrefix,k -> createThreadPool(threadNamePrefix,daemon));

        if (executorService.isShutdown() || executorService.isTerminated()) {
            THREAD_POOL_MAP.remove(threadNamePrefix);
            executorService = createThreadPool(threadNamePrefix, daemon);
            THREAD_POOL_MAP.put(threadNamePrefix,executorService);
        }
        return executorService;
    }

    private static ExecutorService createThreadPool(String threadNamePrefix,Boolean daemon){
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix,daemon);
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_CORE_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                DEFAULT_REJECT_POLICY
        );
    }

    public static void shutdownAllThreadPool(){
        LOGGER.info("关闭所有线程池中...");
        THREAD_POOL_MAP.entrySet().stream().parallel().forEach(entry -> {
            ExecutorService service = entry.getValue();
            service.shutdown();//关闭线程池，未完成任务会进行
            LOGGER.info("关闭线程池 [{}]:[{}]",entry.getKey(),service.isTerminated());
            try {
                service.awaitTermination(10,TimeUnit.SECONDS);//等待一段时间后，线程池关闭
            } catch (InterruptedException e) {
                LOGGER.error("线程池关闭失败!");
                service.shutdownNow();//立即关闭，并返回未执行完的任务
            }
        });
    }

    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix,Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }



}
