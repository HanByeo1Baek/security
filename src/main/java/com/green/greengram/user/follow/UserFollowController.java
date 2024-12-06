package com.green.greengram.user.follow;

import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.user.follow.model.UserFollowReq;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user/follow")
@Tag(name = "5. 유저 팔로우")
public class UserFollowController {
    private final UserFollowService service;

    // 팔로우 신청
    // @RequestBody, 요청을 보내는 자가 body 에 json 형태의 데이터를 담아서 보낸다는 뜻
    @PostMapping
    public ResultResponse<Integer> postUserFollow(@RequestBody UserFollowReq p){
        log.info("UserFollowController > postUserFollow > p: {}", p);
        int result = service.postUserFollow(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("팔로우 신청")
                .resultData(result)
                .build();
    }

    // 팔로우 취소
    // Query String
    @DeleteMapping
    public ResultResponse<Integer> deleteUserFollow(@ParameterObject @ModelAttribute UserFollowReq p){
        log.info("UserFollowController > deleteUserFollow > p: {}", p);
        int result = service.deleteUserFollow(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("팔로우 취소")
                .resultData(result)
                .build();
    }
}
