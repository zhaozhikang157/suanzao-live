package live.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class GenerateServiceTest {
    private static Logger log = LoggerFactory.getLogger(GenerateServiceTest.class);
    ExecutorService threadPool = Executors.newFixedThreadPool(1);

    @Test
    public void testGenerate() throws Exception {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("2200");
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("2200");
            }
        });
    }

}
