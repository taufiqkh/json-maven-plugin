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
import java.util.Map;
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
     * Location of the file.
     */
    @Parameter(required = true)
    private File inputFile;

    /**
     * Property to which the json is assigned.
     */
    @Parameter
    private String outputProperty = "json.output";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter
    private String[] names;

    @Parameter
    private String name;

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
            if (name == null && names == null) {
                throw new MojoExecutionException(
                        "Name or names of json elements to be saved should be configured");
            } else if (name != null && names != null) {
                throw new MojoExecutionException("Cannot specify both name and names");
            }
            String[] namesToRetrieve;
            if (name != null) {
                namesToRetrieve = new String[]{name};
            } else {
                namesToRetrieve = names;
            }
            JsonParser parser = Json.createParser(fileInputStream);
            Properties properties = project.getProperties();
            PropertyJsonPopulator jsonPopulator = new PropertyJsonPopulator(namesToRetrieve);
            try {
                jsonPopulator.populate(properties, outputProperty, parser);
            } catch (JsonKeyRequestException e) {
                throw new MojoFailureException("File didn't match key request", e);
            } catch (JsonParseException e) {
                throw new MojoFailureException("Error parsing JSON", e);
            }
        }
        catch (FileNotFoundException e) {
            throw new MojoExecutionException("Can't find json input file "
                    + inputFile.getAbsolutePath());
        }
        catch (IOException e) {
            throw new MojoExecutionException("Error reading json input file "
                    + inputFile.getAbsolutePath(), e);
        }
    }
}
