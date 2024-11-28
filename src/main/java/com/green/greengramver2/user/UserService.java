package com.green.greengramver2.user;

import com.green.greengramver2.common.MyFileUtils;
import com.green.greengramver2.user.model.UserSignInReq;
import com.green.greengramver2.user.model.UserSignInRes;
import com.green.greengramver2.user.model.UserSignUpReq;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;

    public int signUp(UserSignUpReq p, MultipartFile pic){
        String hashedPassword = BCrypt.hashpw(p.getUpw(), BCrypt.gensalt());
        String savedPicName = (pic == null ? null : myFileUtils.makeRandomFileName(pic));
        p.setUpw(hashedPassword);
        p.setPic(savedPicName);

        int result = mapper.insUser(p);

        if(pic == null){
            return result;
        }

        String middlePath = String.format("user/%d", p.getUserId());
        myFileUtils.makeFolders(middlePath);

        String lastPath = String.format("%s/%s", middlePath, savedPicName);
        try{
            myFileUtils.transferTo(pic, lastPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public UserSignInRes signInUser(UserSignInReq p){
        UserSignInRes res = mapper.selUserByUid(p.getUid());

        if(res == null){
            res = new UserSignInRes();
            res.setMessage("아이디가 틀렸습니다.");
            return res;
        }
        else if(!BCrypt.checkpw(p.getUpw(),res.getUpw())){
            res = new UserSignInRes();
            res.setMessage("비밀번호가 틀렸습니다.");
            return res;
        }
        res.setMessage("성공");
        return res;
    }
}
