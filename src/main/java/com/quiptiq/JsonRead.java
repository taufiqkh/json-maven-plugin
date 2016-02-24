package com.quiptiq;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Goal reads json from an input file.
 */
@SuppressWarnings(value = "unused")
@Mojo(name = "read")
public class JsonRead extends AbstractMojo
{
    /* Don't cache the logger, since its construction will be done by IoC after construction */

    /**
     * Location of the file.
     */
    @Parameter(property = "json.inputfile", required = true)
    private File inputFile;

    @Parameter(property = "json.outputproperty")
    private String outputProperty = "json";

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File f = inputFile;

        if (f == null) {
            throw new MojoExecutionException("Null specified for json.inputfile parameter");
        }
        if (!f.exists()) {
            throw new MojoFailureException("Json input file " + f.getName() + " does not exist");
        }

        try (FileInputStream fileInputStream = new FileInputStream(f)) {
            JSONTokener tokener = new JSONTokener(new FileInputStream(f));
            project.getProperties();

        }
        catch (FileNotFoundException e) {
            throw new MojoFailureException("Can't find file " + f.getAbsolutePath());
        }
        catch (IOException e) {
            throw new MojoExecutionException("Error reading file " + f.getAbsolutePath(), e);
        }
    }
}
