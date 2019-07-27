package xyz.lilei.concurrence;

import com.sun.org.apache.bcel.internal.generic.NEW;
import xyz.lilei.concurrence.vo.ItemVo;

import java.util.concurrent.DelayQueue;

/**
 * @ClassName CheckJobProcesser
 * @Description TODO 任务完成后, 在一定的时间供查询, 之后为释放资源节约内存, 需要定期清理过期的任务
 * @Author lilei
 * @Date 27/07/2019 17:43
 * @Version 1.0
 **/
public class CheckJobProcesser {
    // 存放已完成等待过期任务的队列
    private static DelayQueue<ItemVo<String>> queue
            = new DelayQueue<ItemVo<String>>();

    // 处理到期任务的线程
    private static class FetchJob implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    ItemVo<String> item = queue.take();
                    String jobName = (String) item.getDate();
                    PendingJobPool.getMap().remove(jobName);
                    System.out.println(jobName + "is out of date, remove from map!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 单例模式
    private CheckJobProcesser() {}

    private static class JobProcesserHolder{
        public static CheckJobProcesser processer = new CheckJobProcesser();
    }

    public static CheckJobProcesser getInstance(){
        return JobProcesserHolder.processer;
    }

    // 任务完成后,放入队列, 经过expireTime时间后, 从框架移除
    public void putJob(String jobName,long expireTime){
        ItemVo<String> item = new ItemVo<>(expireTime, jobName);
        queue.offer(item);
        System.out.println("Job["+jobName+"已经放入了过期检查缓存, 过期时长: "+expireTime);
    }

    static {
        Thread thread = new Thread(new FetchJob());
        thread.setDaemon(true);
        thread.start();
        System.out.println("开启任务过期检查守护线程");

    }
}
