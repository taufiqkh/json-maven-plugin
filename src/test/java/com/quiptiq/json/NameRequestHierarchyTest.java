package com.quiptiq.json;

import com.quiptiq.NameRequestHierarchy;
import org.junit.Test;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test population of properties from Json
 */
public class NameRequestHierarchyTest {
    private JsonParser createTestParser(String resourcePath) throws IOException {
        return Json.createParser(
                new FileInputStream(new File("test/resources/" + resourcePath)));
    }

    @Test
    public void testBuildEmptyHierarchy() {
        String[] names = new String[0];
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertNotNull(requests);
        assertTrue(requests.isEmpty());

        assertFalse(requests.containsName("test"));
        assertFalse(requests.containsSegment("test"));
    }

    @Test
    public void testBuildSingleLevelHierarchy() {
        String[] names = new String[] { "test" };
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertEquals(1, requests.size());
        assertTrue(requests.containsName("test"));
        assertTrue(requests.containsSegment("test"));
    }

    @Test
    public void testSubstringOverlap() {
        String[] names = new String[] { "test", "test2" };
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertEquals(2, requests.size());
        assertTrue(requests.containsName("test"));
        assertTrue(requests.containsSegment("test"));
        assertTrue(requests.containsName("test2"));
        assertTrue(requests.containsSegment("test2"));
    }

    @Test
    public void testBuildSingleNodeHierarchy() {
        String[] names = new String[] { "test.foo" };
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertEquals(1, requests.size());
        // "test" should be a segment but not a name
        assertTrue(requests.containsSegment("test"));
        assertFalse(requests.containsName("test"));

        assertTrue(requests.containsSegment("test.foo"));
    }

    @Test
    public void testSiblingHierarchy() {
        String[] names = new String[] { "test.foo", "test.whee" };
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertEquals(2, requests.size());
        assertTrue(requests.containsSegment("test"));
        assertFalse(requests.containsName("test"));
        for (String name : names) {
            assertTrue(requests.containsName(name));
        }
    }

    @Test
    public void testMultiLevelHierarchy() {
        String[] names = new String[] { "test", "foo.bar.baz", "whats.up.doc" };
        NameRequestHierarchy requests = new NameRequestHierarchy(names);
        assertEquals(3, requests.size());
        assertTrue(requests.containsSegment("test"));
        assertTrue(requests.containsSegment("foo"));
        assertTrue(requests.containsSegment("whats.up"));

        for (String name : names) {
            assertTrue(requests.containsName(name));
        }
    }
}
