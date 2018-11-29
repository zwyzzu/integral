package yixia.lib.core.util;

/**
 * 任务队列处理器
 * 默认是单任务列表；
 */
public interface QueueHandler {
    /**
     * 执行任务.
     *
     * @param runnable
     */
    void execute(Runnable runnable);

    /**
     * 设置队列超时，timeout时间范围内如果列表为空，则释放Handler
     *
     * @param timeout
     * @return
     */
    QueueHandler setTimeout(long timeout);

    /**
     * 设置多任务支持，默认为false
     *
     * @param supportMultiTask 是否支持多任务
     * @return for Build
     */
    QueueHandler setMultiTaskEnable(boolean supportMultiTask);

    /**
     * 退出，释放HandlerThread
     */
    void quit();
}
