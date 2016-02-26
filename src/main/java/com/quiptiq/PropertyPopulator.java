package com.quiptiq;

import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import java.util.*;

/**
 * Populates a {@link java.util.Properties} instance with values read from a JSON.
 */
public class PropertyPopulator {
    public PropertyPopulator(Properties properties, JsonParser parser, String[] names) {
        Map<String, NameRequest> root = buildNameRequests(names);
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_ARRAY:
                default:
                    break;
            }
        }
    }

    public Map<String, NameRequest> buildNameRequests(String[] names) {
        Map<String, NameRequest> children = new HashMap<>();
        for (String name : names) {
            String[] segments = name.split("\\.");
            for (String segment : segments) {
                if (!children.containsKey(segment)) {
                    children.put(segment, new NameRequest(segment));
                }
                children = children.get(segment).getChildren();
            }
        }
        return children;
    }
}
