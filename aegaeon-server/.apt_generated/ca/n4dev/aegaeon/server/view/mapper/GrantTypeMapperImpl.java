package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2017-12-11T07:24:43-0500",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
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
}
