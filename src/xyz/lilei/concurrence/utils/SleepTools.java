package xyz.lilei.concurrence.utils;

/**
 * @ClassName SleepTools
 * @Description TODO
 * @Author lilei
 * @Date 20/07/2019 08:51
 * @Version 1.0
 **/
public class SleepTools {

    public static void seconds(long sleepTime){
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void ms(long sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
