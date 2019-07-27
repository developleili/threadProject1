package xyz.lilei.concurrence.vo;

/**
 * @ClassName ITaskProcesser
 * @Description TODO
 * @Author lilei
 * @Date 27/07/2019 13:34
 * @Version 1.0
 **/
public interface ITaskProcesser<T, R> {
    TaskResult<R> taskExecute(T data);
}
