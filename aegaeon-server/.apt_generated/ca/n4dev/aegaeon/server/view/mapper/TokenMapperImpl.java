package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.server.view.TokenView;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2017-12-12T07:49:00-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
public class TokenMapperImpl implements TokenMapper {

    @Override
    public TokenView toView(AccessToken pAccessToken) {
        if ( pAccessToken == null ) {
            return null;
        }

        TokenView tokenView = new TokenView();

        tokenView.setId( pAccessToken.getId() );
        tokenView.setScopes( pAccessToken.getScopes() );
        tokenView.setToken( pAccessToken.getToken() );
        if ( pAccessToken.getTokenType() != null ) {
            tokenView.setTokenType( pAccessToken.getTokenType().name() );
        }
        tokenView.setValidUntil( pAccessToken.getValidUntil() );

        return tokenView;
    }

    @Override
    public TokenView toView(IdToken pIdToken) {
        if ( pIdToken == null ) {
            return null;
        }

        TokenView tokenView = new TokenView();

        tokenView.setId( pIdToken.getId() );
        tokenView.setScopes( pIdToken.getScopes() );
        tokenView.setToken( pIdToken.getToken() );
        if ( pIdToken.getTokenType() != null ) {
            tokenView.setTokenType( pIdToken.getTokenType().name() );
        }
        tokenView.setValidUntil( pIdToken.getValidUntil() );

        return tokenView;
    }

    @Override
    public TokenView toView(RefreshToken pRefreshToken) {
        if ( pRefreshToken == null ) {
            return null;
        }

        TokenView tokenView = new TokenView();

        tokenView.setId( pRefreshToken.getId() );
        tokenView.setScopes( pRefreshToken.getScopes() );
        tokenView.setToken( pRefreshToken.getToken() );
        if ( pRefreshToken.getTokenType() != null ) {
            tokenView.setTokenType( pRefreshToken.getTokenType().name() );
        }
        tokenView.setValidUntil( pRefreshToken.getValidUntil() );

        return tokenView;
    }

    @Override
    public AccessToken toAccessToken(TokenView pTokenView) {
        if ( pTokenView == null ) {
            return null;
        }

        AccessToken accessToken = new AccessToken();

        accessToken.setId( pTokenView.getId() );
        accessToken.setScopes( pTokenView.getScopes() );
        accessToken.setToken( pTokenView.getToken() );
        accessToken.setValidUntil( pTokenView.getValidUntil() );

        return accessToken;
    }

    @Override
    public IdToken toIdToken(TokenView pTokenView) {
        if ( pTokenView == null ) {
            return null;
        }

        IdToken idToken = new IdToken();

        idToken.setId( pTokenView.getId() );
        idToken.setScopes( pTokenView.getScopes() );
        idToken.setToken( pTokenView.getToken() );
        idToken.setValidUntil( pTokenView.getValidUntil() );

        return idToken;
    }

    @Override
    public RefreshToken toRefreshToken(TokenView pTokenView) {
        if ( pTokenView == null ) {
            return null;
        }

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setId( pTokenView.getId() );
        refreshToken.setScopes( pTokenView.getScopes() );
        refreshToken.setToken( pTokenView.getToken() );
        refreshToken.setValidUntil( pTokenView.getValidUntil() );

        return refreshToken;
    }
}
