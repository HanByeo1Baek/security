package com.green.greengramver2.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpReq {
    @JsonIgnore
    private long userId;
    @JsonIgnore
    private String pic;
    @Schema(description = "유저 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uid;
    @Schema(description = "유저 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String upw;
    @Schema(description = "유저 닉네임")
    private String nickName;
}
