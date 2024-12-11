package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicMapper feedPicMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final MyFileUtils myFileUtils;

    @Transactional
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {


        feedMapper.insFeed(p);

        // --------------- 파일 등록
        long feedId = p.getFeedId();

        //저장 폴더 만들기, 저장위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

        //랜덤 파일명 저장용  >> feed_pics 테이블에 저장할 때 사용
        List<String> picNameList = new ArrayList<>(pics.size());
        for(MultipartFile pic : pics) {
            //각 파일 랜덤파일명 만들기
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedPicName);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pic, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FeedPicDto feedPicDto = new FeedPicDto();
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);
        feedPicMapper.insFeedPic(feedPicDto);

        return FeedPostRes.builder()
                          .feedId(feedId)
                          .pics(picNameList)
                          .build();
    }

    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        // N + 1 이슈 발생
        List<FeedGetRes> list = feedMapper.selFeedList(p);
        for(FeedGetRes item : list) {
            //피드 당 사진 리스트
            item.setPics(feedPicMapper.selFeedPic(item.getFeedId()));

            //피드 당 댓글 4개
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(item.getFeedId(), 0, 3);
            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq); //0, 4

            FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
            commentGetRes.setCommentList(commentList);
            commentGetRes.setMoreComment( commentList.size() == commentGetReq.getSize() ); //4개면 true, 4개 아니면 false

            if(commentGetRes.isMoreComment()) {
                commentList.remove(commentList.size() - 1);
            }
            item.setComment(commentGetRes);
        }
        return list;
    }

    // select 2번
    public List<FeedGetRes> getFeedList2(FeedGetReq p) {


        return null;
    }

    // select 3번, 피드 5,000개 있음, 페이지 당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(FeedGetReq p) {
        // 피드 리스트
        List<FeedGetRes> list = feedMapper.selFeedList(p);
        //feed_id를 골라내야 한다.
        List<Long> feedIds1 = list.stream().map(FeedGetRes::getFeedId).collect(Collectors.toList());
        List<Long> feedIds2 = list.stream().map(item -> ((FeedGetRes)item).getFeedId()).toList();
        List<Long> feedIds3 = list.stream().map(FeedGetRes::getFeedId).toList();

        List<Long> feedIds = new ArrayList<>(list.size());
        for(FeedGetRes item : list) {
            feedIds.add(item.getFeedId());
        }
        log.info("feedIds : {}", feedIds);

        //피드와 관련된 사진 리스트
        List<FeedPicSel> feedPicList = feedPicMapper.selFeedPicListByFeedIds(feedIds);
        log.info("feedPicList : {}", feedPicList);

        Map<Long, List<String>> picHashMap = new HashMap<>();
        for(FeedPicSel item : feedPicList) {
            long feedId = item.getFeedId();
            if(!picHashMap.containsKey(feedId)) {
                picHashMap.put(feedId, new ArrayList<String>(2));
            }
            List<String> pics = picHashMap.get(feedId);
            pics.add(item.getPic());

        }

        for(FeedGetRes res : list){
            res.setPics(picHashMap.get(res.getFeedId()));
        }
//        int lastIdx = 0;
//        for(FeedGetRes item : list) {
//
//            List<String> pics = new ArrayList<>(2);
//            for(int i = lastIdx; i < feedPicList.size(); i++){
//                FeedPicSel feedPicSel = feedPicList.get(i);
//                if(item.getFeedId() == feedPicSel.getFeedId()) {
//                    pics.add(feedPicSel.getPic());
//                }
//                else{
//                    item.setPics(pics);
//                    lastIdx = i;
//                    break;
//                }
//            }
//        }

        //피드와 관련된 댓글 리스트
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>();
        for(FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if(!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
                commentHashMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId);
            feedCommentGetRes.getCommentList().add(item);
        }

        for(FeedGetRes res : list) {
            res.setPics(picHashMap.get(res.getFeedId()));
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if(feedCommentGetRes == null) {
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }
        log.info("List : {}", list);
        return list;
    }

    @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        //피드 사진 삭제
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);

        //피드 댓글, 좋아요 삭제
        int affectedRows = feedMapper.delFeedLikeAndFeedCommentAndFeedPic(p);
        log.info("affectedRows: {}", affectedRows);

        //피드 삭제
        return feedMapper.delFeed(p);
    }

}
