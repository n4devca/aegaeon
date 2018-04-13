package ca.n4dev.aegaeon.server.view;

import ca.n4dev.aegaeon.api.model.BaseEntity;

/**
 * Selection.java
 *
 * A simple wrapper taking any POJO and adding a field describing if this entity
 * is selected.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 20 - 2018
 */
public class Selection<E extends BaseEntity> {

    private E entity;

    private boolean selected;

    /**
     * Build an empty selection.
     */
    public Selection() {}

    /**
     * Build a selection.
     * @param pEntity The back entity.
     * @param pSelected If this entity is selected.
     */
    public Selection(E pEntity, boolean pSelected) {
        entity = pEntity;
        selected = pSelected;
    }

    /**
     * @return the entity
     */
    public E getEntity() {
        return entity;
    }

    /**
     * @param pEntity the entity to set
     */
    public void setEntity(E pEntity) {
        entity = pEntity;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param pSelected the selected to set
     */
    public void setSelected(boolean pSelected) {
        selected = pSelected;
    }
}
