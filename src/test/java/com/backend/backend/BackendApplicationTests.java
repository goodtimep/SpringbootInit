package com.backend.backend;

import com.alibaba.fastjson.JSONObject;
import com.backend.backend.common.model.ResponseModel;
import com.backend.backend.model.entity.User;
import com.backend.backend.model.entity.sys.SysRole;
import com.backend.backend.redis.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static com.backend.backend.enums.TokenEnum.REFRESH_TOKEN_EXPIRE_TIME;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendApplicationTests {

    // @Test
    public void contextLoads() {
    }

    @Test
    public void testRedis() {
        User user = new User();
        user.setUserId(1L);

        user.setUserPhone("phone");
        user.setName("name");
        user.setPassword("123");
        RedisUtil.set(user.getUserId().toString(), user);
        User temp = (User) RedisUtil.get(user.getUserId().toString());
        System.out.println(temp.toString());
    }

    @Test
    public void testJson() {
        JSONObject json = new JSONObject();
        String s = json.toJSONString(ResponseModel.fail(HttpStatus.UNAUTHORIZED.value(), "无权访问(Unauthorized):"));
        System.out.println(s);
    }

    @Test
    public void test() {
        System.out.println((String) null);
    }

    // @Test
    public void testRedis2() {
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        System.out.println(Long.parseLong(REFRESH_TOKEN_EXPIRE_TIME.getCode()));
        RedisUtil.set("222", currentTimeMillis, Long.parseLong(REFRESH_TOKEN_EXPIRE_TIME.getCode()) * 1000L);
    }
}
