package cn.junbao.middleware.sdk.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class ApiTest {

    @Test
    public void test(){
        System.out.println(Integer.parseInt("aaa1"));
        System.out.println(Integer.parseInt("aaa2"));
        System.out.println(Integer.parseInt("aaa6666"));
    }
}
