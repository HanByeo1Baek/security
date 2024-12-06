-- User 테이블
CREATE TABLE USER (
      user_id BIGINT AUTO_INCREMENT PRIMARY KEY
    , uid VARCHAR(30) NOT NULL UNIQUE
    , upw VARCHAR(100) NOT NULL
    , nick_name VARCHAR(30)
    , pic VARCHAR(50)
    , created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    , updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- Feed 테이블
CREATE TABLE feed (
      feed_id BIGINT AUTO_INCREMENT PRIMARY KEY
    , writer_user_id BIGINT NOT NULL
    , contents VARCHAR(1000)
    , location VARCHAR(30)
    , created_at DATETIME NOT NULL DEFAULT current_timestamp()
    , updated_at DATETIME ON UPDATE current_timestamp()
);

ALTER TABLE feed ADD CONSTRAINT FK_FEED_writer_user_id
    FOREIGN KEY(writer_user_id) REFERENCES user(user_id);
-- feed_pics는 feed에 종속되어 있다. (종속관계- 부모/자식)
-- 자식 테이블의 pk는 부모테이블의 pk를 참조한다. (복합키가 된다.)
DROP TABLE feed_pics;
CREATE TABLE feed_pics(
      FOREIGN KEY (feed_id) REFERENCES feed(feed_id)
    , feed_id BIGINT
    , pic VARCHAR(50)
    , created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    , PRIMARY KEY(feed_id, pic)
);


-- 좋아요 테이블
-- user_id, feed_id, created_at
CREATE table feed_like (
      feed_id bigint
    , user_id bigint
    , created_at DATETIME NOT NULL DEFAULT current_timestamp
    , PRIMARY KEY (feed_id, user_id)
    , FOREIGN KEY (feed_id) REFERENCES feed(feed_id)
    , FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 댓글 테이블
-- feed_id, user_id, 내용, created_at, updated_at
-- 대리키(인조 식별자)
CREATE TABLE feed_comments(
      feed_comment_id BIGINT AUTO_INCREMENT PRIMARY KEY
    , feed_id BIGINT NOT NULL
    , user_id BIGINT NOT NULL
    , `COMMENT` VARCHAR(150) NOT NULL
    , created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    , updated_at DATETIME ON UPDATE current_timestamp()
    , FOREIGN KEY (feed_id) REFERENCES feed(feed_id)
    , FOREIGN KEY (user_id) REFERENCES user(user_id)
);



