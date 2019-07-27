package xyz.lilei.concurrence.vo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ItemVo
 * @Description TODO 存放到队列的元素
 *
 * @Author lilei
 * @Date 23/07/2019 23:30
 * @Version 1.0
 **/
public class ItemVo<T> implements Delayed {

    private long activeTime; // 到期时间, 单位毫秒
    private T data;

    public ItemVo(long activeTime, T data) {
        super();
        this.activeTime = TimeUnit.NANOSECONDS.convert(activeTime
                , TimeUnit.MILLISECONDS)+System.nanoTime();// 将传入的时长转化为超时的时刻
        this.data = data;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public T getData() {
        return data;
    }

    // 返回元素的剩余时间
    @Override
    public long getDelay(TimeUnit unit) {
        long d = unit.convert(this.activeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        return d;
    }

    //按照剩余时间进行排序
    @Override
    public int compareTo(Delayed o) {
        long d = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (d==0)?0:((d>0)?1: -1);
    }
}
