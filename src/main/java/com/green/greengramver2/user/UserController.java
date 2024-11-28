package com.green.greengramver2.user;

import com.green.greengramver2.common.model.ResultResponse;
import com.green.greengramver2.user.model.UserSignInReq;
import com.green.greengramver2.user.model.UserSignInRes;
import com.green.greengramver2.user.model.UserSignUpReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService service;

    @PostMapping("sign-up")
    public ResultResponse<Integer> userSignUp(@RequestPart UserSignUpReq p,
                                              @RequestPart(required = false) MultipartFile pic){
        int result = service.signUp(p, pic);
        return ResultResponse.<Integer>builder()
                .resultMessage("가입 완료")
                .resultData(result)
                .build();
    }

    @PostMapping("sign-in")
    public ResultResponse<UserSignInRes> userSignIn(@RequestBody UserSignInReq p){
        UserSignInRes result = service.signInUser(p);
        return ResultResponse.<UserSignInRes>builder()
                .resultMessage(result.getMessage())
                .resultData(result)
                .build();
    }
}
