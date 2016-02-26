package com.quiptiq;

import java.util.List;
import java.util.Map;

/**
 * Represents a request for population of a given JSON key or tree of keys.
 */
public class NameRequest {
    private String name;

    private Map<String, NameRequest> children;

    public NameRequest(String name) {
        this.name = name;
        this.children = null;
    }

    public String getName() {
        return name;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public Map<String, NameRequest> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return name == null;
    }
}
