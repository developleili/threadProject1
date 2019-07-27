package xyz.lilei.concurrence.demo;

import xyz.lilei.concurrence.PendingJobPool;
import xyz.lilei.concurrence.utils.SleepTools;
import xyz.lilei.concurrence.vo.ITaskProcesser;
import xyz.lilei.concurrence.vo.TaskResult;
import xyz.lilei.concurrence.vo.TaskResultType;

import java.util.Random;

/**
 * @ClassName MyTask
 * @Description TODO
 * @Author lilei
 * @Date 27/07/2019 18:27
 * @Version 1.0
 **/
public class MyTask implements ITaskProcesser<Integer, Integer> {

    @Override
    public TaskResult<Integer> taskExecute(Integer data) {
        Random r = new Random();
        int flag = r.nextInt(500);
        SleepTools.ms(flag);
        if (flag < 300){//正常处理的情况
            Integer returnVale = data.intValue() + flag;
            return new TaskResult<>(TaskResultType.Success, returnVale);
        }else if (flag > 301 && flag <= 400){
            return new TaskResult<Integer>(TaskResultType.Failure,-1, "Failure");
        }else{
            try {
                throw new Exception("发生异常了!! ");
            } catch (Exception e) {
                return new TaskResult<>(TaskResultType.Exception, -1, e.getMessage());
            }
        }
    }


}
