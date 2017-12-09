package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.server.controller.dto.SelectableItemDto;
import ca.n4dev.aegaeon.server.view.ClientDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2017-12-09T14:40:46-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public ClientDto clientToClientDto(Client pClient) {
        if ( pClient == null ) {
            return null;
        }

        ClientDto clientDto = new ClientDto();

        clientDto.setProviderType( pClient.getProviderName() );
        clientDto.setAccessTokenSeconds( pClient.getAccessTokenSeconds() );
        clientDto.setContacts( clientContactListToStringList( pClient.getContacts() ) );
        clientDto.setDescription( pClient.getDescription() );
        clientDto.setId( pClient.getId() );
        clientDto.setIdTokenSeconds( pClient.getIdTokenSeconds() );
        clientDto.setLogoUrl( pClient.getLogoUrl() );
        clientDto.setName( pClient.getName() );
        clientDto.setPublicId( pClient.getPublicId() );
        clientDto.setRedirections( clientRedirectionListToStringList( pClient.getRedirections() ) );
        clientDto.setRefreshTokenSeconds( pClient.getRefreshTokenSeconds() );
        clientDto.setScopes( clientScopeListToSelectableItemDtoList( pClient.getScopes() ) );
        clientDto.setSecret( pClient.getSecret() );

        return clientDto;
    }

    @Override
    public SelectableItemDto grantTypesToGrants(GrantType pGrantType) {
        if ( pGrantType == null ) {
            return null;
        }

        SelectableItemDto selectableItemDto = new SelectableItemDto();

        selectableItemDto.setName( pGrantType.getCode() );
        selectableItemDto.setId( pGrantType.getId() );

        return selectableItemDto;
    }

    @Override
    public SelectableItemDto clientScopeToSelectableItem(ClientScope pClientScope) {
        if ( pClientScope == null ) {
            return null;
        }

        SelectableItemDto selectableItemDto = new SelectableItemDto();

        String name = pClientScopeScopeName( pClientScope );
        if ( name != null ) {
            selectableItemDto.setName( name );
        }
        Long id = pClientScopeScopeId( pClientScope );
        if ( id != null ) {
            selectableItemDto.setId( id );
        }

        return selectableItemDto;
    }

    protected List<String> clientContactListToStringList(List<ClientContact> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( ClientContact clientContact : list ) {
            list1.add( contactEmailToString( clientContact ) );
        }

        return list1;
    }

    protected List<String> clientRedirectionListToStringList(List<ClientRedirection> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( ClientRedirection clientRedirection : list ) {
            list1.add( redirectionsUrlToString( clientRedirection ) );
        }

        return list1;
    }

    protected List<SelectableItemDto> clientScopeListToSelectableItemDtoList(List<ClientScope> list) {
        if ( list == null ) {
            return null;
        }

        List<SelectableItemDto> list1 = new ArrayList<SelectableItemDto>( list.size() );
        for ( ClientScope clientScope : list ) {
            list1.add( clientScopeToSelectableItem( clientScope ) );
        }

        return list1;
    }

    private String pClientScopeScopeName(ClientScope clientScope) {
        if ( clientScope == null ) {
            return null;
        }
        Scope scope = clientScope.getScope();
        if ( scope == null ) {
            return null;
        }
        String name = scope.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long pClientScopeScopeId(ClientScope clientScope) {
        if ( clientScope == null ) {
            return null;
        }
        Scope scope = clientScope.getScope();
        if ( scope == null ) {
            return null;
        }
        Long id = scope.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
