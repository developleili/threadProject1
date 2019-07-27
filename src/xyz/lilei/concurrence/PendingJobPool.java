package xyz.lilei.concurrence;

import xyz.lilei.concurrence.vo.ITaskProcesser;
import xyz.lilei.concurrence.vo.JobInfo;
import xyz.lilei.concurrence.vo.TaskResult;
import xyz.lilei.concurrence.vo.TaskResultType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName PendingJobPool
 * @Description TODO 框架的主体类
 * @Author lilei
 * @Date 27/07/2019 16:41
 * @Version 1.0
 **/
public class PendingJobPool {

    // 保守估计
    private static final int THREAD_COUNTS =
            Runtime.getRuntime().availableProcessors();

    private static BlockingQueue<Runnable> taskQueue
            = new ArrayBlockingQueue<>(5000);

    // 自定义线程池
    private static ExecutorService taskExecutor =
            new ThreadPoolExecutor(THREAD_COUNTS, THREAD_COUNTS
            , 60, TimeUnit.SECONDS, taskQueue);

    private static ConcurrentHashMap<String, JobInfo<?>> jobInfoMap =
            new ConcurrentHashMap<>();

    private static CheckJobProcesser checkJob
            = CheckJobProcesser.getInstance();
    public static Map<String, JobInfo<?>> getMap(){
        return jobInfoMap;
    }

    // 单例模式
    private PendingJobPool() {}

    private static class JobPoolHolder{
        public static PendingJobPool pool = new PendingJobPool();
    }

    public static PendingJobPool getInstance(){
        return JobPoolHolder.pool;
    }

    // 注册工作
    public <R>void registerJob(String jobName, int jobLength, ITaskProcesser<?, ?> taskProcesser,
                            long expireTime){
        JobInfo<R> jobInfo = new JobInfo<>(jobName, jobLength, taskProcesser, expireTime);
        if (jobInfoMap.putIfAbsent(jobName, jobInfo )!= null){
            throw new RuntimeException(jobName + "已经注册了!");
        }
    }

    // 根据工作名检索任务
    @SuppressWarnings("unchecked")
    private <R> JobInfo<R> getJob(String jobName){
        JobInfo<R> jobInfo = (JobInfo<R>) jobInfoMap.get(jobName);
        if (null == jobInfo)
            throw new RuntimeException(jobName + "是个非法任务 ");
        return jobInfo;
    }
    // 调用者提交工作中的任务
    public <T,R> void putTask(String jobName, T t){
        JobInfo<R> jobInfo = getJob(jobName);
        PendingTask<T, R> task = new PendingTask<>(jobInfo, t);
        taskExecutor.execute(task);
    }

    // 获得每个任务的处理详情
    public <R>List<TaskResult<R>> getTaskDetail(String jobName){
        JobInfo<R> job = getJob(jobName);
        return job.getTaskDetail();
    }

    // 获得整个工作的处理进度
    public <R>  String getTaskProgess(String jobName){
        JobInfo<R> jobInfo = getJob(jobName);
        return jobInfo.getTotalProcess();
    }

    // 对工作中的线程进行包装, 提交给线程池使用, 并处理的结果, 写入缓存以供查询
    private static class PendingTask<T,R> implements Runnable{

        private JobInfo<R> jobInfo;
        private T processData;

        public PendingTask(JobInfo<R> jobInfo, T processData) {
            this.jobInfo = jobInfo;
            this.processData = processData;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            R r = null;
            ITaskProcesser<T, R> taskProcesser =
                    (ITaskProcesser<T, R>) jobInfo.getTaskProcesser();
            TaskResult<R> result = null;
            try {
                // 调用业务人员自己实现的方法
                result = taskProcesser.taskExecute(processData);
                if (result == null)
                    result = new TaskResult<R>(TaskResultType.Exception, r,"result is null");
                if (result.getResultType() == null){
                    if (result.getReason() == null){
                        result = new TaskResult<R>(TaskResultType.Exception, r,"Reason is null");
                    }else {
                        result = new TaskResult<R>(TaskResultType.Exception, r,"result is not null, but type is null");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new TaskResult<R>(TaskResultType.Exception, r,e.getMessage());
            }finally {
                jobInfo.addTaskResult(result, checkJob);
            }
        }
    }
}
