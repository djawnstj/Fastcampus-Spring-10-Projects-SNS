package com.fastcampus.sns.fixture;

import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        final UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        final PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);

        return result;
    }

}
