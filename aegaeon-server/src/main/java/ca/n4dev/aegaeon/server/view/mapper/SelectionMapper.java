package ca.n4dev.aegaeon.server.view.mapper;

import java.util.List;

import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import ca.n4dev.aegaeon.server.view.Selection;
import org.springframework.stereotype.Component;

/**
 * SelectionMapper.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 22 - 2018
 */
@Component
public class SelectionMapper {

    public SelectableItemView clientAuthFlowToSelectableItemView(Selection<ClientAuthFlow> pSelection) {
        SelectableItemView selectableItemView = new SelectableItemView();

        if (pSelection != null) {

            if (pSelection.getEntity() != null) {

                selectableItemView.setId(pSelection.getEntity().getId());

                if (pSelection.getEntity().getFlow() != null) {
                    selectableItemView.setName(pSelection.getEntity().getFlow().toString());
                }
            }

            selectableItemView.setSelected(pSelection.isSelected());
        }


        return selectableItemView;
    }

    public SelectableItemView clientScopeToSelectableItemView(Selection<ClientScope> pSelection) {
        SelectableItemView selectableItemView = new SelectableItemView();

        if (pSelection != null) {

            if (pSelection.getEntity() != null && pSelection.getEntity().getScope() != null) {
                selectableItemView.setId(pSelection.getEntity().getScope().getId());
                selectableItemView.setName(pSelection.getEntity().getScope().getName());

            }
            selectableItemView.setSelected(pSelection.isSelected());
        }


        return selectableItemView;
    }


    public Selection<ClientScope> viewToClientScope(SelectableItemView pSelectableItemView) {
        Selection<ClientScope> selection = new Selection<>();

        if (pSelectableItemView != null) {
            selection.setEntity(new ClientScope());
            selection.getEntity().setScope(new Scope());
            selection.getEntity().getScope().setId(pSelectableItemView.getId());
            selection.getEntity().getScope().setName(pSelectableItemView.getName());
        }

        selection.setSelected(pSelectableItemView.isSelected());

        return selection;
    }


    public Selection<ClientAuthFlow> viewToClientAuthFlow(SelectableItemView pSelectableItemView) {
        Selection<ClientAuthFlow> selection = new Selection<>();

        if (pSelectableItemView != null) {
            selection.setEntity(new ClientAuthFlow());
            selection.getEntity().setId(pSelectableItemView.getId());
            selection.getEntity().setFlow(Flow.from(pSelectableItemView.getName()));
        }

        selection.setSelected(pSelectableItemView.isSelected());

        return selection;
    }

    public List<Selection<ClientScope>> selectableItemViewsToClientScopes(List<SelectableItemView> pSelectableItemViews) {
        return Utils.convert(pSelectableItemViews, this::viewToClientScope);
    }


    public List<Selection<ClientAuthFlow>> selectableItemViewsToClientAuthFlows(List<SelectableItemView> pSelectableItemViews) {
        return Utils.convert(pSelectableItemViews, this::viewToClientAuthFlow);
    }


}
