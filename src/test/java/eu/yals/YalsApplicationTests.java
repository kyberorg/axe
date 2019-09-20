package eu.yals;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Stub test class
 *
 * @since 2.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
public class YalsApplicationTests {

    @Test
    public void contextLoads() {
        log.info("Context loaded");
    }

}
