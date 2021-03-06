# File Tags

A spring boot web application for tagging files.


## Requirements

* Java SDK 11+
* Maven 3.6.x ([http://maven.apache.org](http://maven.apache.org))
* Optional: Lombok ([https://projectlombok.org/](https://projectlombok.org/)) (only if you use an IDE like Eclipse)


## IDE Support

To use these projects in an IDE you will need the [project Lombok](https://projectlombok.org/) agent. Full instructions can be found in the Lombok website. The sign that you need to do this is a lot of compiler errors to do with missing methods and fields.


## Build the installation package

1. Install Java and create a `JAVA_HOME` environment variable that points to the location of your JDK

	`set PATH=C:\develop\lib\jdk-11.0.2\bin;%PATH%`

	`set JAVA_HOME=C:\develop\lib\jdk-11.0.2`

2. Install Maven and create Maven environment variables

	`set PATH=C:\develop\lib\apache-maven-3.6.2\bin;%PATH%`

	`set M2_HOME=C:\develop\lib\apache-maven-3.6.2`

	`set M2=%M2_HOME%\bin`

3. Run `mvn clean package` in the root directory of the application

4. Navigate to `distribution/target` where you find the `FileTags_V[VERSION].zip`


## Run

1. Create a installation directory

	`cd [PATH_TO_APPS]`

	`mkdir file-tags`

2. Extract the installation package `FileTags_V[VERSION].zip`

3. (Optional) Edit the file `bin/run.conf.example` and rename it to `bin/run.conf` to configure Java parameters (Windows: `bin/run.conf.bat.example` to `bin/run.conf.bat`)

4. (Optional) Edit the file `config/application.yml.example` and rename it to `config/application.yml` to configure application parameters

5. Navigate to the installation directory and start the application

	`cd [PATH_TO_APPS]/file-tags`

	`bin/run`

6. Browse to [http://localhost:4080/](http://localhost:4080/)


## Enable links to local files in Mozilla Firefox

1. Open [about:config](about:config) in Firefox

2. Add the following key-value pairs:

	capability.policy.policynames=localfilelinks
	capability.policy.localfilelinks.sites=http://localhost:4080
	capability.policy.localfilelinks.checkloaduri.enabled=allAccess
