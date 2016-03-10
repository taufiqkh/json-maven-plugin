# Json Maven Plugin

Maven plugin for reading JSON files.

This plugin is not currently hosted in any maven repositories and will need to be built and mvn installed from a local clone.

## Usage

To include in your project, add to the plugins section of your pom.xml:
```xml
<plugin>
    <groupId>com.quiptiq</groupId>
    <artifactId>json-maven-plugin</artifactId>
    <version>0.1-SNAPSHOT</version>
    <configuration>
        <outputProperty>propertyToStoreOutput</outputProperty>
        <inputFile>path/to/valid.json</inputFile>
        <names>
            <name>firstnodetoread</name>
            <name>secondnode.child</name>
        </names>
    </configuration>
    <executions>
        <execution>
            <phase>validate</phase>
            <goals>
                <goal>read</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

You will need to explicitly specify the phase in which the plugin will execute, as it does not
have a default.

## Configuration

The path to the json file to read is specified in the ```inputFile``` tag in the plugin's
```configuration```. The json file is parsed, and any nodes specified in a ```name``` tag in
the ```names``` section are saved as properties. All properties are stored under the json.output
property, though the name of the property may be set by configuring the value of outputProperty.

For example, given the following valid.json file:
```json
{
  "firstname": "Bob",
  "surname": "Smith",
  "address":
  {
    "streetnumber": 214,
    "street": "jellicoe",
    "suburb": "Chelmer",
    "city": "Brisbane"
  }
}
```

to retrieve the value of firstname, lastname, and the city and store it in the cities property:
 ```xml
<plugins>
    <plugin>
        <groupId>com.quiptiq</groupId>
        <artifactId>json-maven-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <configuration>
            <!-- any requested json will be stored under the cities property -->
            <outputProperty>cities</outputProperty>
            <!-- This is the file containing the json to be read -->
            <inputFile>path/to/valid.json</inputFile>
            <names>
                <name>firstname</name>
                <name>surname</name>
                <name>address.city</name>
            </names>
        </configuration>
        <executions>
            <execution>
                <phase>validate</phase>
                <goals>
                    <goal>read</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
 ```