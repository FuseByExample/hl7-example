HL7 Example
==================

Requirements
------------

* Apache Maven 3.x (http://maven.apache.org)
* JBoss Fuse 6.x (http://jboss.org/jbossfuse)

Building Example
----------------

From top level of example project directory, run

    mvn clean install

Running in JBoss Fuse
---------------------

Start JBoss Fuse

    <JBoss Fuse Home>/bin/fuse

From the JBoss Fuse console, enter the following to install the example application

    features:addurl mvn:org.fusebyexample.examples/hl7-example-features/1.0.0-SNAPSHOT/xml/features
    features:install hl7-example-all

To see what is happening within the JBoss Fuse server, you can continuously view the
log (tail) with the following command

    log:tail

File Based Test
-------------------

Copy the file `camel-test.hl7` to `/tmp/`.