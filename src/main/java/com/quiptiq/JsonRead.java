package com.quiptiq;

/*
 * Changes Copyright 2016 Taufiq Hoven
 * Modified from Apache Maven archetype, The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.json.Json;
import javax.json.stream.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Goal reads json from an input file.
 */
@SuppressWarnings(value = "unused")
@Mojo(name = "read")
public class JsonRead extends AbstractMojo
{
    /* Don't cache the logger, since its construction will be done by injection */

    /**
     * Name of the property that will contain the path to the input file
     */
    private static final String PROPERTY_INPUT_FILE = "json.inputFile";

    /**
     * Name of the property that contains the name of the property to which the json is assigned.
     */
    private static final String PROPERTY_OUTPUT = "json.outputProperty";

    /**
     * Default value for the property to which the json is assigned.
     */
    private static final String DEFAULT_OUTPUT_PROPERTY = "json.output";

    /**
     * Location of the file.
     */
    @Parameter(property = PROPERTY_INPUT_FILE, required = true)
    private File inputFile;

    @Parameter(property = PROPERTY_OUTPUT)
    private String outputProperty = DEFAULT_OUTPUT_PROPERTY;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // Maven should prevent nulls, but crazier things have happened
        if (inputFile == null) {
            throw new MojoExecutionException("Null specified for json.inputfile parameter");
        }
        if (!inputFile.exists()) {
            throw new MojoFailureException("Json input file " + inputFile.getAbsolutePath() + " does not exist");
        }

        try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            JsonParser parser = Json.createParser(fileInputStream);
            Properties properties = project.getProperties();
            // TODO: fill out properties in a more fine-grained way
            properties.setProperty(outputProperty, parser.toString());
        }
        catch (FileNotFoundException e) {
            throw new MojoFailureException("Can't find json input file "
                    + inputFile.getAbsolutePath());
        }
        catch (IOException e) {
            throw new MojoExecutionException("Error reading json input file "
                    + inputFile.getAbsolutePath(), e);
        }
    }
}
