package ca.n4dev.aegaeon.server.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.api.repository.AuthorityRepository;
import ca.n4dev.aegaeon.api.repository.UserInfoRepository;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.api.validation.PasswordEvaluator;
import ca.n4dev.aegaeon.server.utils.StringRandomizer;
import ca.n4dev.aegaeon.server.view.UserView;
import ca.n4dev.aegaeon.server.view.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * UserServiceUnitTest.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 22 - 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceUnitTest {

    @Mock OpenIdEventLogger openIdEventLogger;
    @Mock UserMapper userMapper;
    @Mock PasswordEvaluator passwordEvaluator;

    @Mock UserInfoRepository userInfoRepository;
    @Mock AuthorityRepository authorityRepository;
    @Mock UserInfoTypeService userInfoTypeService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserRepository userRepository;


    @InjectMocks UserService userService;
    //@Captor ArgumentCaptor<User> userCaptor;

    @Test
    public void findOneSuccess() {

        User user = buildUser();
        UserView userView = buildUserView();

        when(userRepository.findByUserName(any())).thenReturn(user);
        when(userInfoRepository.findByUserId(any())).thenReturn(buildUserInfo());
        when(userMapper.toView(any(), any())).thenReturn(userView);

        // when(myMock.myFunction(anyString())).thenAnswer(i -> i.getArguments()[0]);

        Assert.assertEquals(userView, userService.findOne(1L));
    }

    private User buildUser() {
        User user = new User(StringRandomizer.getInstance().getRandomString(64));

        user.setId(1L);
        user.setEnabled(true);
        user.setUserName("user-unit@domain.tld");
        return user;
    }

    private List<UserInfo> buildUserInfo() {
        return new ArrayList<>();
    }

    private UserView buildUserView() {
        UserView userView = new UserView();


        return userView;
    }
}
