package org.anystub.IT;

import org.anystub.Base;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Kirill on 9/3/2016.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class WorkerTest {


    @Test
    public void xTest() throws IOException {
        SourceSystem mock = Mockito.mock(SourceSystem.class);
        Worker worker = new Worker(mock);
        Mockito.when(mock.get()).thenReturn("mocked answer");
        assertEquals("mocked answer", worker.get());
    }

    @Autowired
    Worker worker;

    @Autowired
    Base base;


    @Test
    public void xxTest() throws IOException {
        base.clear();

        assertEquals("fixed", worker.get());

        assertEquals(1, base.times());
    }

    @Test
    public void randTest() throws IOException {
        base.clear();
        assertTrue("1", -1594594225 == worker.rand());
        assertTrue("2", -1594594225 == worker.rand());
        assertEquals(2L, base.times());

    }


    @TestConfiguration
    static class Conf {

        @Bean
        Base base() {
            return new Base();
        }

        @Bean
        public SourceSystem sourceSystem(Base base) {


            return new SourceSystem("http://localhost:8080") {
                @Override
                public String get() throws IOException {
                    return base.request("root");
                }

                /**
                 * pay attention: the result of the function depends on
                 * internal state of the object, which changed by invocation of the function
                 * @param digit should has the same mean as in super class
                 * @return from upstream or from
                 */
                @Override
                public Integer rand(int digit) {
                    return Integer.valueOf(
                            base.request("rand", Integer.toString(digit)));
                }
            };
        }
    }

}
