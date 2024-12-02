package com.green.greengramver2.feed.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FeedGetRes {
    private long feedId;
    private long writerUserId;
    private String writerNm;
    private String writerPic;
    private String content;
    private String location;
    private String createdAt;
    private int isLike;

    private List<String> pics = new ArrayList<>();
}
