package xyz.lilei.concurrence.vo;

/**
 * @ClassName TaskResult
 * @Description TODO 任务处理返回的结果实体类
 * @Author lilei
 * @Date 27/07/2019 13:34
 * @Version 1.0
 **/
public class TaskResult<R> {
    private final TaskResultType resultType;
    private final R returnValue;//方法的业务结果数据
    private final String reason;// 方法的失败原因

    public TaskResult(TaskResultType resultType, R returnValue, String reason) {
        this.resultType = resultType;
        this.returnValue = returnValue;
        this.reason = reason;
    }

    public TaskResult(TaskResultType resultType, R returnValue) {
        this.resultType = resultType;
        this.returnValue = returnValue;
        this.reason = "Success";
    }

    public TaskResultType getResultType() {
        return resultType;
    }

    public R getReturnValue() {
        return returnValue;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "resultType=" + resultType +
                ", returnValue=" + returnValue +
                ", reason='" + reason + '\'' +
                '}';
    }
}
