package com.quiptiq;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.List;
import java.util.Map;

/**
 * Represents a request for population of a given JSON key or tree of keys.
 */
public final class NameRequestHierarchy {
    private final PatriciaTrie<String> requests;
    private final int size;

    public NameRequestHierarchy(String[] names) {
        requests = new PatriciaTrie<>();
        size = names.length;
        for (String name : names) {
            int separatorIdx = name.indexOf(".");
            String segment;
            while (separatorIdx >= 0) {
                segment = name.substring(0, separatorIdx);
                requests.putIfAbsent(segment, null);
                separatorIdx = name.indexOf(".", separatorIdx + 1);
            }
            requests.put(name, name);
        }
    }

    public int size() {
        return size;
    }

    public boolean containsSegment(String segment) {
        return requests.containsKey(segment);
    }

    public boolean containsName(String name) {
        return requests.containsKey(name) && requests.get(name) != null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Given a set of requested property names, builds a trie of requests. The trie is populated
     * with name segments as "marker" keys with null values, and the full name as a key to itself.
     * @param names
     * @return
     */
    public static Trie<String, String> buildRequestHierarchy(String[] names) {
        PatriciaTrie<String> requests = new PatriciaTrie<>();
        for (String name : names) {
            int separatorIdx = name.indexOf(".");
            int lastIdx = separatorIdx;
            String segment;
            while (separatorIdx >= 0) {
                segment = name.substring(0, separatorIdx);
                requests.putIfAbsent(segment, null);
                lastIdx = separatorIdx;
                separatorIdx = name.indexOf(".", lastIdx + 1);
            }
            requests.put(name, name);
        }
        return requests;
    }
}
