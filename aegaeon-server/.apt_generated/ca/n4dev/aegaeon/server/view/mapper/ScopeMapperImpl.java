package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-06-12T14:42:09-0400",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 3.12.2.v20161117-1814, environment: Java 1.8.0_92 (Oracle Corporation)"
)
@Component
public class ScopeMapperImpl implements ScopeMapper {

    @Override
    public SelectableItemView scopeToScopeView(Scope pScope) {
        if ( pScope == null ) {
            return null;
        }

        SelectableItemView selectableItemView = new SelectableItemView();

        selectableItemView.setName( pScope.getName() );
        selectableItemView.setDescription( pScope.getDescription() );
        selectableItemView.setId( pScope.getId() );

        return selectableItemView;
    }

    @Override
    public SelectableItemView scopeToScopeView(ClientScope pClientScope) {
        if ( pClientScope == null ) {
            return null;
        }

        SelectableItemView selectableItemView = new SelectableItemView();

        String name = pClientScopeScopeName( pClientScope );
        if ( name != null ) {
            selectableItemView.setName( name );
        }
        Long id = pClientScopeScopeId( pClientScope );
        if ( id != null ) {
            selectableItemView.setId( id );
        }

        return selectableItemView;
    }

    @Override
    public ClientScope scopeViewToClientScope(SelectableItemView pSelectableItemView) {
        if ( pSelectableItemView == null ) {
            return null;
        }

        ClientScope clientScope = new ClientScope();

        clientScope.setScope( selectableItemViewToScope( pSelectableItemView ) );
        clientScope.setId( pSelectableItemView.getId() );

        return clientScope;
    }

    @Override
    public List<ClientScope> scopeViewsToClientScopes(List<SelectableItemView> pSelectableItemViews) {
        if ( pSelectableItemViews == null ) {
            return null;
        }

        List<ClientScope> list = new ArrayList<ClientScope>( pSelectableItemViews.size() );
        for ( SelectableItemView selectableItemView : pSelectableItemViews ) {
            list.add( scopeViewToClientScope( selectableItemView ) );
        }

        return list;
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

    protected Scope selectableItemViewToScope(SelectableItemView selectableItemView) {
        if ( selectableItemView == null ) {
            return null;
        }

        Scope scope = new Scope();

        scope.setName( selectableItemView.getName() );
        scope.setId( selectableItemView.getId() );

        return scope;
    }
}
