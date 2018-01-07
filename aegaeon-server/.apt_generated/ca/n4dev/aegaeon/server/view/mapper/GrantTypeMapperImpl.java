package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
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
public class GrantTypeMapperImpl implements GrantTypeMapper {

    @Override
    public SelectableItemView grantTypeToSelectableItemView(GrantType pGrantType) {
        if ( pGrantType == null ) {
            return null;
        }

        SelectableItemView selectableItemView = new SelectableItemView();

        selectableItemView.setName( pGrantType.getCode() );
        selectableItemView.setId( pGrantType.getId() );

        return selectableItemView;
    }

    @Override
    public SelectableItemView clientGrantTypeToSelectableItemView(ClientGrantType pClientGrantType) {
        if ( pClientGrantType == null ) {
            return null;
        }

        SelectableItemView selectableItemView = new SelectableItemView();

        String code = pClientGrantTypeGrantTypeCode( pClientGrantType );
        if ( code != null ) {
            selectableItemView.setName( code );
        }
        Long id = pClientGrantTypeGrantTypeId( pClientGrantType );
        if ( id != null ) {
            selectableItemView.setId( id );
        }
        selectableItemView.setSelected( pClientGrantType.isSelected() );

        return selectableItemView;
    }

    @Override
    public ClientGrantType selectableItemViewToClientGrantType(SelectableItemView pSelectableItemView) {
        if ( pSelectableItemView == null ) {
            return null;
        }

        ClientGrantType clientGrantType = new ClientGrantType();

        clientGrantType.setGrantType( selectableItemViewToGrantType( pSelectableItemView ) );
        clientGrantType.setId( pSelectableItemView.getId() );
        clientGrantType.setSelected( pSelectableItemView.isSelected() );

        return clientGrantType;
    }

    @Override
    public List<ClientGrantType> selectableItemViewsToClientGrantTypes(List<SelectableItemView> pSelectableItemViews) {
        if ( pSelectableItemViews == null ) {
            return null;
        }

        List<ClientGrantType> list = new ArrayList<ClientGrantType>( pSelectableItemViews.size() );
        for ( SelectableItemView selectableItemView : pSelectableItemViews ) {
            list.add( selectableItemViewToClientGrantType( selectableItemView ) );
        }

        return list;
    }

    private String pClientGrantTypeGrantTypeCode(ClientGrantType clientGrantType) {
        if ( clientGrantType == null ) {
            return null;
        }
        GrantType grantType = clientGrantType.getGrantType();
        if ( grantType == null ) {
            return null;
        }
        String code = grantType.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }

    private Long pClientGrantTypeGrantTypeId(ClientGrantType clientGrantType) {
        if ( clientGrantType == null ) {
            return null;
        }
        GrantType grantType = clientGrantType.getGrantType();
        if ( grantType == null ) {
            return null;
        }
        Long id = grantType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected GrantType selectableItemViewToGrantType(SelectableItemView selectableItemView) {
        if ( selectableItemView == null ) {
            return null;
        }

        GrantType grantType = new GrantType();

        grantType.setId( selectableItemView.getId() );
        grantType.setCode( selectableItemView.getName() );

        return grantType;
    }
}
