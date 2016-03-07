package com.quiptiq.json;

import com.quiptiq.PropertyJsonPopulator;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Tests the population of properties from a Json stream.
 */
public class PropertyJsonPopulatorTest {
    private Properties properties;

    @Before
    public void setUp() {
        properties = new Properties();
    }
    private JsonParser createParser(String json) {
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return Json.createParser(stream);
    }

    private JsonParser createFileParser(String testFileName) throws FileNotFoundException {
        File testFile = new File("src/test/resources/basic-valid/" + testFileName);
        return Json.createParser(new FileInputStream(testFile));
    }

    @Test
    public void singleValue() throws Exception {
        JsonParser parser = createParser("{\"key\": \"test\"}");
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(new String[] { "key" });
        properties.put("existing", "yup");
        jsonPopulator.populate(properties, "myPrefix", parser);
        Set<String> newNames = properties.stringPropertyNames();
        assertEquals(2, newNames.size());
        assertEquals("yup", properties.getProperty("existing"));
        assertEquals("test", properties.getProperty("myPrefix.key"));
    }

    @Test
    public void singleNestedValue() throws Exception {
        JsonParser parser = createParser("{\"key\": { \"nested\": \"test\" }}");
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(
                new String[] {"key.nested"});
        jsonPopulator.populate(properties, "myPrefix", parser);
        assertEquals(1, properties.stringPropertyNames().size());
        assertEquals("test", properties.getProperty("myPrefix.key.nested"));
    }

    @Test
    public void singleArrayString() throws Exception {
        JsonParser parser = createParser("[ \"foo\" ]");
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(new String[] {"0"});
        jsonPopulator.populate(properties, "myPrefix", parser);
        assertEquals(1, properties.stringPropertyNames().size());
        assertEquals("foo", properties.getProperty("myPrefix.0"));
    }

    @Test
    public void arrayInObject() throws Exception {
        JsonParser parser = createFileParser("array-in-object.json");
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(new String[] {
            "test.0",
            "test.2"
        });
        jsonPopulator.populate(properties, "myPrefix", parser);
        assertEquals(2, properties.stringPropertyNames().size());
        assertEquals("1", properties.getProperty("myPrefix.test.0"));
        assertEquals("3", properties.getProperty("myPrefix.test.2"));
    }

    @Test
    public void objectsInArray() throws Exception {
        JsonParser parser = createFileParser("objects-in-array.json");
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(new String[] {
                "0.test",
                "1.test"
        });
        jsonPopulator.populate(properties, "myPrefix", parser);
        assertEquals(2, properties.stringPropertyNames().size());
        assertEquals("pass", properties.getProperty("myPrefix.0.test"));
        assertEquals("pass2", properties.getProperty("myPrefix.1.test"));
    }

    @Test
    public void multiNested() throws Exception {
        JsonParser parser = createFileParser("multi-nested.json");
        String[] nameRequests = new String[] {
                "key",
                "array.0",
                "array.1",
                "object.object-array.0",
                "object.object-array.2",
                "object.object-object.object-object-key",
                "object.nested-key",
                "object2.object2-key",
                "array2.1",
                "array2.3.array2-object-key"
        };
        Properties properties = new Properties();
        PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(nameRequests);
        jsonPopulator.populate(properties, "x", parser);
        assertEquals(nameRequests.length, properties.stringPropertyNames().size());
        assertEquals("value", properties.getProperty("x.key"));
        assertEquals("123", properties.getProperty("x.array.0"));
    }
}
