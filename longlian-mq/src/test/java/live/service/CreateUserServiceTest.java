package live.service;

import com.longlian.live.service.AppUserCommonService;
import live.util.ParentTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuhan on 2017-07-10.
 */
public class CreateUserServiceTest extends ParentTest {
    private static Logger log = LoggerFactory.getLogger(CreateUserServiceTest.class);
    @Autowired
    AppUserCommonService createUserService;

    @Test
    public void testCreate() throws Exception {
        int threadCount = 10;
       // int splitPoint = 10;
        CountDownLatch endCount = new CountDownLatch(threadCount);
        CountDownLatch beginCount = new CountDownLatch(1);

        Thread[] threads = new Thread[threadCount];
        //起500个线程，秒杀第一个商品
        for(int i= 0;i < threadCount;i++){
            threads[i] = new Thread(new  Runnable() {
                public void run() {
                    try {
                        //等待在一个信号量上，挂起
                        beginCount.await();
                        createUserService.createYunxinUser(10000L,"fdsafdsa","fdsafdas");
                        endCount.countDown();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        long startTime = System.currentTimeMillis();
        //主线程释放开始信号量，并等待结束信号量，这样做保证1000个线程做到完全同时执行，保证测试的正确性
        beginCount.countDown();

        try {
            //主线程等待结束信号量
            endCount.await();
            System.out.println("total cost " + (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
