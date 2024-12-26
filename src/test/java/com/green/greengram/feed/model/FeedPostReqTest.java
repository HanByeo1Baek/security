package com.green.greengram.feed.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedPostReqTest {
    @Test
    void test() {
        FeedPostReq req = new FeedPostReq();
        req.setLocation(null);

        System.out.println(req.getLocation());
    }
}