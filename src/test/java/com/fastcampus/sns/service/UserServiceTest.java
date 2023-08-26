package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    public void 회원가입이_정상적으로_동작하는_경우() throws Exception {
        // given
        final String userName = "userName";
        final String password = "password";

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password, 1));

        //then
        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    public void 회원가입_시_userName_으로_회원가입_한_유저가_이미_있는_경우() throws Exception {
        // given
        final String userName = "userName";
        final String password = "password";

        final UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    public void 로그인이_정상적으로_동작하는_경우() throws Exception {
        // given
        final String userName = "userName";
        final String password = "password";

        final UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    public void 로그인_시_userName_으로_회원가입한_유저가_없는_경우() throws Exception {
        // given
        final String userName = "userName";
        final String password = "password";

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    public void 로그인_시_패스워드가_틀린_경우() throws Exception {
        // given
        final String userName = "userName";
        final String password = "password";
        final String wrongPassword = "wrongPassword";

        final UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // when
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        //then
        final SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }

}
