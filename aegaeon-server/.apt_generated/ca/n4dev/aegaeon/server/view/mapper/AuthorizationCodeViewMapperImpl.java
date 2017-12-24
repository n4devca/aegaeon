package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2017-12-21T19:36:43-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
@Component
public class AuthorizationCodeViewMapperImpl implements AuthorizationCodeViewMapper {

    @Override
    public AuthorizationCodeView toView(AuthorizationCode pAuthorizationCode) {
        if ( pAuthorizationCode == null ) {
            return null;
        }

        AuthorizationCodeView authorizationCodeView = new AuthorizationCodeView();

        String name = pAuthorizationCodeUserName( pAuthorizationCode );
        if ( name != null ) {
            authorizationCodeView.setUserName( name );
        }
        String publicId = pAuthorizationCodeClientPublicId( pAuthorizationCode );
        if ( publicId != null ) {
            authorizationCodeView.setClientId( publicId );
        }
        authorizationCodeView.setCode( pAuthorizationCode.getCode() );

        return authorizationCodeView;
    }

    private String pAuthorizationCodeUserName(AuthorizationCode authorizationCode) {
        if ( authorizationCode == null ) {
            return null;
        }
        User user = authorizationCode.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String pAuthorizationCodeClientPublicId(AuthorizationCode authorizationCode) {
        if ( authorizationCode == null ) {
            return null;
        }
        Client client = authorizationCode.getClient();
        if ( client == null ) {
            return null;
        }
        String publicId = client.getPublicId();
        if ( publicId == null ) {
            return null;
        }
        return publicId;
    }
}
