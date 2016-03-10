package com.quiptiq.json;

import com.quiptiq.JsonRead;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test the read plugin
 */
public class JsonReadTest {
        @Rule
        public MojoRule rule = new MojoRule();

        @Rule
        public TestResources testResources = new TestResources();

        private File getBaseDir(String project) throws IOException {
            File baseDir = testResources.getBasedir(project);
            assertNotNull(baseDir);
            assertTrue(baseDir.exists());
            assertTrue(baseDir.isDirectory());
            return baseDir;
        }

        /**
         * @throws Exception
         */
        @Test
        public void testMojoGoal() throws Exception
        {
            File baseDir = getBaseDir("basic-valid");

            File pom = new File(baseDir, "pom.xml");
            assertTrue(pom.exists());

            JsonRead mojo = (JsonRead) rule.lookupMojo( "read", pom );

            assertNotNull(mojo);

            // Create the project by hand, as the plugin test harness doesn't seem to be injecting
            // values into annotations
            final MavenProject mvnProject = new MavenProject();
            mvnProject.setFile(pom);
            rule.setVariableValueToObject(mojo, "project", mvnProject);
            mojo.execute();
            Properties properties = mvnProject.getProperties();
            assertEquals("Test Name", properties.getProperty("json.output.name"));
            assertEquals("child", properties.getProperty("json.output.second.node"));
        }
}
