package xyz.lilei.concurrence.demo;

import xyz.lilei.concurrence.PendingJobPool;
import xyz.lilei.concurrence.utils.SleepTools;
import xyz.lilei.concurrence.vo.TaskResult;

import java.util.List;
import java.util.Random;

/**
 * @ClassName AppTask
 * @Description TODO
 * @Author lilei
 * @Date 27/07/2019 18:40
 * @Version 1.0
 **/
public class AppTask {
    private final static String JOB_NAME = "计算数值";
    private final static int JOB_LENGTH = 1000;

    // 查询任务进度的线程
    private static class QueryResult implements Runnable{

        private PendingJobPool pool;

        public QueryResult(PendingJobPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            // 查询次数
            int i = 0;
            while (i<350){
                List<TaskResult<String>> taskDetail = pool.getTaskDetail(JOB_NAME);
                if (!taskDetail.isEmpty()){
                    System.out.println(pool.getTaskProgess(JOB_NAME));
                    System.out.println(taskDetail);
                }
                SleepTools.ms(100);
                i++;
            }
        }
    }

    public static void main(String[] args) {
        MyTask myTask = new MyTask();
        // 拿到框架的实例
        PendingJobPool pool = PendingJobPool.getInstance();
        pool.registerJob(JOB_NAME, JOB_LENGTH, myTask, 1000*5);
        Random r = new Random();
        for (int i = 0; i < JOB_LENGTH; i++) {
            pool.putTask(JOB_NAME, r.nextInt(1000));
        }
        Thread thread = new Thread(new QueryResult(pool));
        thread.start();
    }
}
