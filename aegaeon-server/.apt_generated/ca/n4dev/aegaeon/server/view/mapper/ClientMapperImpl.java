package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-01-03T09:24:39-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Autowired
    private ScopeMapper scopeMapper;
    @Autowired
    private GrantTypeMapper grantTypeMapper;

    @Override
    public ClientView clientToClientDto(Client pClient, List<ClientScope> pClientScopes, List<ClientRedirection> pClientRedirections, List<ClientContact> pClientContacts, List<ClientGrantType> pClientGrantTypes) {
        if ( pClient == null && pClientScopes == null && pClientRedirections == null && pClientContacts == null && pClientGrantTypes == null ) {
            return null;
        }

        ClientView clientView = new ClientView();

        if ( pClient != null ) {
            clientView.setProviderType( pClient.getProviderName() );
            clientView.setId( pClient.getId() );
            clientView.setName( pClient.getName() );
            clientView.setPublicId( pClient.getPublicId() );
            clientView.setDescription( pClient.getDescription() );
            clientView.setSecret( pClient.getSecret() );
            clientView.setLogoUrl( pClient.getLogoUrl() );
            clientView.setIdTokenSeconds( pClient.getIdTokenSeconds() );
            clientView.setAccessTokenSeconds( pClient.getAccessTokenSeconds() );
            clientView.setRefreshTokenSeconds( pClient.getRefreshTokenSeconds() );
        }
        if ( pClientScopes != null ) {
            clientView.setScopes( clientScopeListToSelectableItemViewList( pClientScopes ) );
        }
        if ( pClientRedirections != null ) {
            clientView.setRedirections( clientRedirectionListToStringList( pClientRedirections ) );
        }
        if ( pClientContacts != null ) {
            clientView.setContacts( clientContactListToStringList( pClientContacts ) );
        }
        if ( pClientGrantTypes != null ) {
            clientView.setGrants( clientGrantTypeListToSelectableItemViewList( pClientGrantTypes ) );
        }

        return clientView;
    }

    @Override
    public void clientViewToclient(ClientView pClientView, Client pClient) {
        if ( pClientView == null ) {
            return;
        }

        pClient.setProviderName( pClientView.getProviderType() );
        pClient.setAccessTokenSeconds( pClientView.getAccessTokenSeconds() );
        pClient.setDescription( pClientView.getDescription() );
        pClient.setIdTokenSeconds( pClientView.getIdTokenSeconds() );
        pClient.setLogoUrl( pClientView.getLogoUrl() );
        pClient.setName( pClientView.getName() );
        pClient.setPublicId( pClientView.getPublicId() );
        pClient.setRefreshTokenSeconds( pClientView.getRefreshTokenSeconds() );
        pClient.setSecret( pClientView.getSecret() );
    }

    protected List<String> clientRedirectionListToStringList(List<ClientRedirection> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( ClientRedirection clientRedirection : list ) {
            list1.add( redirectionToString( clientRedirection ) );
        }

        return list1;
    }

    protected List<SelectableItemView> clientGrantTypeListToSelectableItemViewList(List<ClientGrantType> list) {
        if ( list == null ) {
            return null;
        }

        List<SelectableItemView> list1 = new ArrayList<SelectableItemView>( list.size() );
        for ( ClientGrantType clientGrantType : list ) {
            list1.add( grantTypeMapper.clientGrantTypeToSelectableItemView( clientGrantType ) );
        }

        return list1;
    }

    protected List<SelectableItemView> clientScopeListToSelectableItemViewList(List<ClientScope> list) {
        if ( list == null ) {
            return null;
        }

        List<SelectableItemView> list1 = new ArrayList<SelectableItemView>( list.size() );
        for ( ClientScope clientScope : list ) {
            list1.add( scopeMapper.scopeToScopeView( clientScope ) );
        }

        return list1;
    }

    protected List<String> clientContactListToStringList(List<ClientContact> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( ClientContact clientContact : list ) {
            list1.add( contactToString( clientContact ) );
        }

        return list1;
    }
}
