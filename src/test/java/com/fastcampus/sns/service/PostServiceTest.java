package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.PostEntityFixture;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;
    
    @Test
    public void 포스트_작성이_성공한_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        
        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        //then
        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

    @Test
    public void 포스트_작성_시_요청한_유저가_존재하지_않는_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    public void 포스트_수정이_성공한_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        //then
        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    public void 포스트_수정_시_포스트가_존재하지_않는_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    public void 포스트_수정_시_권한이_없는_경우() throws Exception {
        // given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    public void 포스트_삭제_성공한_경우() throws Exception {
        // given
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        //then
        Assertions.assertDoesNotThrow(() -> postService.delete(userName, postId));
    }

    @Test
    public void 포스트_삭제_시_포스트가_존재하지_않는_경우() throws Exception {
        // given
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    public void 포스트_삭제_시_권한이_없는_경우() throws Exception {
        // given
        String userName = "userName";
        Integer postId = 1;

        // when
        final PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        final UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    public void 피드_목록_요청이_성공한_경우() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        //then
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }


    @Test
    public void 내_피드_목록_요청이_성공한_경우() throws Exception {
        final Pageable pageable = mock(Pageable.class);
        final UserEntity user = mock(UserEntity.class);

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());

        //then
        Assertions.assertDoesNotThrow(() -> postService.myList("", pageable));
    }

}
