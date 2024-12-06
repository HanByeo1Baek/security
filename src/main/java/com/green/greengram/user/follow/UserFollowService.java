package com.green.greengram.user.follow;

import com.green.greengram.user.follow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowMapper mapper;

    public int postUserFollow(UserFollowReq p){
        return mapper.insUserFollow(p);
    }

    public int deleteUserFollow(UserFollowReq p){
        return mapper.delUserFollow(p);
    }
}
