package com.green.greengramver2.feed;

import com.green.greengramver2.common.MyFileUtils;
import com.green.greengramver2.feed.model.FeedPostReq;
import com.green.greengramver2.feed.model.FeedPostRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicsMapper feedPicsMapper;
    private final MyFileUtils myFileUtils;

    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p){
        int result = feedMapper.insFeed(p);

        // ---------- 파일 등록
        long feedId = p.getFeedId();

        // 저장 폴더 만들기, 저장위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

        for(MultipartFile pic : pics){
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try{
                myFileUtils.transferTo(pic, filePath);
            }catch(IOException e){
                e.printStackTrace();
            }
//            FeedPostRes res = feedPicsMapper.;
//            res.setFeedId(feedId);
//            res.getPics();
        }

        return null;
    }
}
