package com.quiptiq.json;

import com.quiptiq.JsonRead;

import org.apache.maven.Maven;
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
     * As we are not properly running a pom, the mojo needs to be configured "artificially" by
     * explicitly setting its project and configuration.
     * @param project Project with which the mojo will interact
     * @param pom Pom from which configuration is read and the project derived.
     * @return JsonRead mojo configured with the given project and pom
     * @throws Exception if an error occurs while looking up the mojo
     */
    private JsonRead createJsonRead(MavenProject project, File pom) throws Exception {
        assertTrue(pom.exists());

        JsonRead mojo = (JsonRead) rule.lookupMojo( "read", pom );
        assertNotNull(mojo);

        project.setFile(pom);
        rule.setVariableValueToObject(mojo, "project", project);
        rule.configureMojo(mojo, "json-maven-plugin", pom);
        return mojo;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testMojoGoal() throws Exception
    {
        File baseDir = getBaseDir("basic-valid");

        File pom = new File(baseDir, "pom.xml");

        // Create the project by hand, as the plugin test harness doesn't seem to be injecting
        // values into annotations
        final MavenProject mvnProject = new MavenProject();

        JsonRead mojo = createJsonRead(mvnProject, pom);
        mojo.execute();
        Properties properties = mvnProject.getProperties();
        assertEquals("Test Name", properties.getProperty("testOut.name"));
        assertEquals("child", properties.getProperty("testOut.second.node"));
    }
}
