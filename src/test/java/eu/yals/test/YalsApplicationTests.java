package eu.yals.test;

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
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
public class YalsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
