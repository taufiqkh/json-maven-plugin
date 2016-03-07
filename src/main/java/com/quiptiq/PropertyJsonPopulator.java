package com.quiptiq;

import javax.json.stream.JsonParser;
import java.util.*;

/**
 * Populates a {@link java.util.Properties} instance with values read from a JSON.
 */
public class PropertyJsonPopulator {
    private static final String SEPARATOR = ".";
    private final NameRequestHierarchy requests;

    public PropertyJsonPopulator(String[] names) {
        this.requests = new NameRequestHierarchy(names);
    }

    public void populate(Properties properties, String propertyPrefix, JsonParser parser)
            throws JsonParseException, JsonKeyRequestException {
        PropertyPopulator populator = (subKey, value) ->
                properties.put(propertyPrefix + SEPARATOR + subKey, value);
        // Only root object and array are supported by the Json parser, not values
        readValue(parser, populator, "");
    }

    private interface PropertyPopulator {
        void put(String subKey, String value);
    }

    private void readObject(JsonParser parser, PropertyPopulator populator, String keyPrefix)
            throws JsonParseException, JsonKeyRequestException {
        for (JsonParser.Event event = ensureNext(parser);
             event != JsonParser.Event.END_OBJECT;
             event = ensureNext(parser)) {
            switch (event) {
                case KEY_NAME:
                    String key = keyPrefix + parser.getString();
                    if (requests.containsSegment(key)) {
                        if (requests.containsName(key)) {
                            populator.put(key, readValue(parser));
                        } else {
                            readValue(parser, populator, key + SEPARATOR);
                        }
                    } else {
                        skipValue(parser);
                    }
                    break;
                default:
                    throw new RuntimeException(
                            "Unable to process object, unexpected element found: " + event);
            }
        }
    }

    private JsonParser.Event ensureNext(JsonParser parser) {
        if (!parser.hasNext()) {
            throw new RuntimeException("Reached end of JSON before expected");
        }
        return parser.next();
    }

    private void skipValue(JsonParser parser) throws JsonParseException {
        JsonParser.Event event = ensureNext(parser);
        switch (event) {
            case START_OBJECT:
                skipObject(parser);
                break;
            case START_ARRAY:
                skipArray(parser);
                break;
            case END_OBJECT:
                throw new JsonParseException("Found object end with no matching start");
            case END_ARRAY:
                throw new JsonParseException("Found array end with no matching start");
            case KEY_NAME:
                throw new JsonParseException("Found key name while reading value");
        }
    }

    private String readValue(JsonParser parser) throws JsonParseException, JsonKeyRequestException {
        JsonParser.Event event = ensureNext(parser);
        switch (event) {
            case START_OBJECT:
                throw new JsonKeyRequestException(
                        "Expected json value for saving key, found object " + parser.getString());
            case START_ARRAY:
                throw new JsonKeyRequestException(
                        "Expected json value for saving key, found array " + parser.getString());
            case KEY_NAME:
                throw new JsonParseException("Expected json value, unexpectedly found key name: " +
                        parser.getString());
            case END_ARRAY:
            case END_OBJECT:
                throw new JsonParseException("Expected json value, unexpectedly found " +
                        event.name());
            default:
                return parser.getString();
        }
    }

    private void readValue(JsonParser parser, PropertyPopulator populator, String keyPrefix)
            throws JsonParseException, JsonKeyRequestException {
        JsonParser.Event event = ensureNext(parser);
        switch (event) {
            case START_OBJECT:
                readObject(parser, populator, keyPrefix);
                break;
            case START_ARRAY:
                readArray(parser, populator, keyPrefix);
                break;
            case KEY_NAME:
                throw new JsonParseException("Expected json value, unexpectedly found key name: " +
                        parser.getString());
            case END_ARRAY:
            case END_OBJECT:
                throw new JsonParseException("Expected json value, unexpectedly found " +
                        event.name());
            // No thing to do otherwise, this should only be called when nesting expected
        }
    }

    private void skipObject(JsonParser parser) throws JsonParseException {
        for (JsonParser.Event event = ensureNext(parser);
             event != JsonParser.Event.END_OBJECT;
             event = ensureNext(parser)) {
            switch (event) {
                case KEY_NAME:
                    skipValue(parser);
                    break;
                default:
                    throw new JsonParseException("Unexpected element found while processing object " +
                            event + ": " + parser.getString());
            }
        }
    }

    private void skipArray(JsonParser parser) throws JsonParseException {
        for (JsonParser.Event event = ensureNext(parser);
             event != JsonParser.Event.END_ARRAY;
             event = ensureNext(parser)) {
            switch (event) {
                case START_OBJECT:
                    skipObject(parser);
                    break;
                case END_OBJECT:
                    throw new JsonParseException("Unexpected object end found while processing array");
                case KEY_NAME:
                    throw new JsonParseException(
                            "Unexpectedly found key name while processing array: " +
                                    parser.getString());
                    // otherwise keep skipping
            }
        }
    }

    private void readArray(JsonParser parser, PropertyPopulator populator, String keyPrefix)
            throws JsonParseException, JsonKeyRequestException {
        JsonParser.Event event = ensureNext(parser);
        for (int idx = 0; event != JsonParser.Event.END_ARRAY; idx++, event = ensureNext(parser)) {
            String key = keyPrefix + idx;
            switch (event) {
                case START_OBJECT: {
                    if (requests.containsSegment(key)) {
                        if (requests.containsName(key)) {
                            throw new JsonKeyRequestException(
                                    "Value requested for name " + key + ", found object instead");
                        }
                        readObject(parser, populator, key + SEPARATOR);
                    } else {
                        skipObject(parser);
                    }
                    break;
                }
                case START_ARRAY: {
                    if (requests.containsSegment(key)) {
                        if (requests.containsName(key)) {
                            throw new JsonKeyRequestException(
                                    "Value requested for name " + key + ", found array instead");
                        }
                        readArray(parser, populator, key + SEPARATOR);
                    } else {
                        skipArray(parser);
                    }
                }
                case END_OBJECT:
                    throw new JsonParseException("Unexpected object end found while processing array");
                case KEY_NAME:
                    throw new JsonParseException(
                            "Unexpectedly found key name while processing array: " +
                                    parser.getString());
                default:
                    if (requests.containsSegment(key)) {
                        if (requests.containsName(key)) {
                            populator.put(key, parser.getString());
                        } else {
                            throw new JsonKeyRequestException("Value found at " + key +
                                    " where further nesting expected");
                        }
                    }
                    // else ignore the value and continue
            }
        }
    }
}
