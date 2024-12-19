package com.green.greengram.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //통합 테스트때 사용
class TokenProviderTest {
    //테스트는 생성자를 이용한 DI가 불가능
    //DI방법은 명시적, setter메소드, 생성자
    //테스트 때는 필드 주입방식을 사용한다.

    @Autowired //리플렉션 API을 이용해서 setter가 없어도 주입 가능
    private TokenProvider tokenProvider;

    @Test
    public void generateToken() {
        JwtUser jwtUser = new JwtUser();
        jwtUser.setSignedUserId(10);

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        jwtUser.setRoles(roles);

        //When (실행단계)
        String token = tokenProvider.generateToken(jwtUser, Duration.ofHours(3));

        //Then (검증단계)
        assertNotNull(token);

        System.out.println("token: " + token);
    }

    @Test
    void validToken() {
        //1분 (이미 지남)
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMTQyMSwiZXhwIjoxNzM0NDAxNDgxLCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.BToLlWbBs9Bo-kFADXNIXvPhq3o2jz5VtNpbFYCrKVY";

        boolean result = tokenProvider.validToken(token);

        assertFalse(result);
    }

    @Test
    void getAuthentication() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMzAxOSwiZXhwIjoxNzM0NDEzODE5LCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.Sqqbgf1udeZaxtjQpc_Wax7NSCUbPwl49P_cnR6dlH0"; //3시간 짜리

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertNotNull(authentication);
    }
}