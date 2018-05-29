package ca.n4dev.aegaeon.server.view.mapper;

import java.util.List;

import ca.n4dev.aegaeon.api.model.BaseEntity;
import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import ca.n4dev.aegaeon.server.view.Selection;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * SelectionMapper.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 22 - 2018
 */
@Mapper(componentModel = "spring")
public interface SelectionMapper {

    @Mappings({
        @Mapping(target = "id", source = "entity.id"),
        @Mapping(target = "name", source = "entity.flow")
    })
    SelectableItemView clientAuthFlowToSelectableItemView(Selection<ClientAuthFlow> pSelection);

    @Mappings({
            @Mapping(target = "id", source = "entity.scope.id"),
            @Mapping(target = "name", source = "entity.scope.name"),
    })
    SelectableItemView clientScopeToSelectableItemView(Selection<ClientScope> pSelection);

    @InheritInverseConfiguration
    Selection<ClientScope> viewToClientScope(SelectableItemView pSelectableItemView);

    @InheritInverseConfiguration
    Selection<ClientAuthFlow> viewToClientAuthFlow(SelectableItemView pSelectableItemView);

    List<Selection<ClientScope>> selectableItemViewsToClientScopes(List<SelectableItemView> pSelectableItemViews);

    List<Selection<ClientAuthFlow>> selectableItemViewsToClientAuthFlows(List<SelectableItemView> pSelectableItemViews);

//
//    @AfterMapping
//    default void mapName(Selection<? extends BaseEntity> pSelection, @MappingTarget SelectableItemView pSelectableItemView ) {
//        if (pSelection.getEntity() != null) {
//            BaseEntity entity = pSelection.getEntity();
//            if (entity instanceof ClientAuthFlow) {
//                ClientAuthFlow clientAuthFlow = (ClientAuthFlow) entity;
//                if (clientAuthFlow.getFlow() != null) {
//                    pSelectableItemView.setName(clientAuthFlow.getFlow().toString());
//                }
//            } else if (entity instanceof ClientScope) {
//                ClientScope clientScope = (ClientScope) entity;
//                if (clientScope.getScope() != null) {
//                    pSelectableItemView.setName(clientScope.getScope().getName());
//                }
//            }
//        }
//    }
}
