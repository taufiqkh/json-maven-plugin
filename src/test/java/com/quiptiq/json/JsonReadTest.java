package com.quiptiq.json;

import com.quiptiq.JsonRead;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * Test the read plugin
 */
public class JsonReadTest
            extends AbstractMojoTestCase
    {
        /**
         * @see junit.framework.TestCase#setUp()
         */
        protected void setUp() throws Exception
        {
            // required for mojo lookups to work
            super.setUp();
        }

        /**
         * @throws Exception
         */
        public void testMojoGoal() throws Exception
        {
            File testPom = new File( getBasedir(),
                    "src/test/resources/unit/basic-test/pom.xml" );
            assertNotNull(testPom);
            assertTrue(testPom.exists());

            JsonRead mojo = (JsonRead) lookupMojo( "read", testPom );

            assertNotNull(mojo);
        }
}
