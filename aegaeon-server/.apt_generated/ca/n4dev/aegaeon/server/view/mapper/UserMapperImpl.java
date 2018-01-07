package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.Authority;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.api.model.UserInfoType;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-01-03T09:24:39-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserView toView(User pUser, List<UserInfo> pUserInfos) {
        if ( pUser == null && pUserInfos == null ) {
            return null;
        }

        UserView userView = new UserView();

        if ( pUser != null ) {
            userView.setPicture( pUser.getPictureUrl() );
            userView.setRoles( authorityListToStringList( pUser.getAuthorities() ) );
            userView.setName( pUser.getName() );
        }
        if ( pUserInfos != null ) {
            userView.setUserInfos( userInfoListToUserInfoViewList( pUserInfos ) );
        }

        return userView;
    }

    @Override
    public User toEntity(UserView pUserView) {
        if ( pUserView == null ) {
            return null;
        }

        User user = new User();

        user.setAuthorities( stringListToAuthorityList( pUserView.getRoles() ) );
        user.setPictureUrl( pUserView.getPicture() );
        user.setId( pUserView.getId() );
        user.setName( pUserView.getName() );

        return user;
    }

    @Override
    public UserInfoView userInfoToView(UserInfo pUserInfo) {
        if ( pUserInfo == null ) {
            return null;
        }

        UserInfoView userInfoView = new UserInfoView();

        userInfoView.setName( pUserInfo.getOtherName() );
        Long id = pUserInfoTypeId( pUserInfo );
        if ( id != null ) {
            userInfoView.setRefTypeId( id );
        }
        String code = pUserInfoTypeCode( pUserInfo );
        if ( code != null ) {
            userInfoView.setCode( code );
        }
        userInfoView.setRefId( pUserInfo.getId() );
        String code1 = pUserInfoTypeParentCode( pUserInfo );
        if ( code1 != null ) {
            userInfoView.setCategory( code1 );
        }
        userInfoView.setValue( pUserInfo.getValue() );

        return userInfoView;
    }

    protected List<UserInfoView> userInfoListToUserInfoViewList(List<UserInfo> list) {
        if ( list == null ) {
            return null;
        }

        List<UserInfoView> list1 = new ArrayList<UserInfoView>( list.size() );
        for ( UserInfo userInfo : list ) {
            list1.add( userInfoToView( userInfo ) );
        }

        return list1;
    }

    protected List<String> authorityListToStringList(List<Authority> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( Authority authority : list ) {
            list1.add( authorityToString( authority ) );
        }

        return list1;
    }

    protected List<Authority> stringListToAuthorityList(List<String> list) {
        if ( list == null ) {
            return null;
        }

        List<Authority> list1 = new ArrayList<Authority>( list.size() );
        for ( String string : list ) {
            list1.add( stringToAuthority( string ) );
        }

        return list1;
    }

    private Long pUserInfoTypeId(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }
        UserInfoType type = userInfo.getType();
        if ( type == null ) {
            return null;
        }
        Long id = type.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String pUserInfoTypeCode(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }
        UserInfoType type = userInfo.getType();
        if ( type == null ) {
            return null;
        }
        String code = type.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private String pUserInfoTypeParentCode(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }
        UserInfoType type = userInfo.getType();
        if ( type == null ) {
            return null;
        }
        UserInfoType parent = type.getParent();
        if ( parent == null ) {
            return null;
        }
        String code = parent.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }
}
