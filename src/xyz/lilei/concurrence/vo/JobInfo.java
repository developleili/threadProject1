package xyz.lilei.concurrence.vo;

import xyz.lilei.concurrence.CheckJobProcesser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName JobInfo
 * @Description TODO
 * @Author lilei
 * @Date 27/07/2019 13:43
 * @Version 1.0
 **/
public class JobInfo<R> {
    // 区分唯一的工作
    private final String jobName;
    // 工作的任务个数
    private final int jobLength;
    // 工作任务的处理器
    private final ITaskProcesser<?, ?> taskProcesser;
    // 成功处理的任务数
    private AtomicInteger successCount;
    // 已处理的任务数
    private AtomicInteger taskProcesserCount;
    private LinkedBlockingDeque<TaskResult<R>> taskDetailQueue;//拿结果从头拿, 放结果从尾放
    // 工作的完成保存的时间, 超过这个时间从缓存中清除
    private final long expireTime;

    public JobInfo(String jobName, int jobLength, ITaskProcesser<?, ?> taskProcesser,
                  long expireTime) {
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.taskProcesser = taskProcesser;
        this.successCount = new AtomicInteger(0);
        this.taskProcesserCount = new AtomicInteger(0);
        this.taskDetailQueue = new LinkedBlockingDeque<TaskResult<R>>(jobLength);
        this.expireTime = expireTime;
    }

    public ITaskProcesser<?, ?> getTaskProcesser() {
        return taskProcesser;
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getTaskProcesserCount() {
        return taskProcesserCount.get();
    }

    public List<TaskResult<R>> getTaskDetail(){
        List<TaskResult<R>> taskList = new LinkedList<>();
        TaskResult<R> taskResult;
        while ((taskResult = taskDetailQueue.pollFirst())!=null){
            taskList.add(taskResult);
        }
        return taskList;
    }

    // 从业务角度来说只需要保证最终一致性即可所以不需要加锁
    public void addTaskResult(TaskResult<R> result, CheckJobProcesser checkJob){
         if (TaskResultType.Success.equals(result.getResultType())){
             successCount.incrementAndGet();
         }
         taskDetailQueue.addLast(result);
         taskProcesserCount.incrementAndGet();

         if (taskProcesserCount.get() == jobLength){
             checkJob.putJob(jobName, expireTime);
         }
    }

    // 提供工作中失败的次数, 方便调用者调用
    public int getFailCount(){
        return taskProcesserCount.get() - successCount.get();
    }

    // 提供当前任务的总进度
    public String getTotalProcess(){
        return "Success[" + successCount.get() + "]/Current["+
                taskProcesserCount+"]/ Total[" + jobLength +"]";
    }


}
