package ca.n4dev.aegaeon.server.view;

import java.util.List;
import java.util.Objects;

/**
 * ScopeView.java
 *
 * Scope view.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 12 - 2019
 */
public class ScopeView {

    private Long id;

    private String name;

    private List<String> claims;

    public ScopeView() {
    }

    public ScopeView(Long pId, String pName) {
        id = pId;
        name = pName;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param pId the id to set
     */
    public void setId(Long pId) {
        id = pId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param pName the name to set
     */
    public void setName(String pName) {
        name = pName;
    }

    /**
     * @return the claims
     */
    public List<String> getClaims() {
        return claims;
    }

    /**
     * @param pClaims the claims to set
     */
    public void setClaims(List<String> pClaims) {
        claims = pClaims;
    }

    @Override
    public boolean equals(Object pO) {
        if (this == pO) {
            return true;
        }
        if (pO == null || getClass() != pO.getClass()) {
            return false;
        }
        ScopeView scopeView = (ScopeView) pO;
        return Objects.equals(id, scopeView.id) &&
                Objects.equals(name, scopeView.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ScopeView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
