package live.process;

import com.longlian.mq.process.SeriesCourseTimeUpdateProcess;
import com.longlian.mq.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 生成
 * Created by lh on 2017-02-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SeriesCourseTimeTest {
    private static Logger log = LoggerFactory.getLogger(SeriesCourseTimeTest.class);
    @Autowired
    private CourseService courseService;
    @Autowired
    private SeriesCourseTimeUpdateProcess seriesCourseTimeUpdateProcess;


    @Test
    public void testProcess() throws Exception {
        //sseriesCourseTimeUpdateProcess.process(1545l);
    }

}
