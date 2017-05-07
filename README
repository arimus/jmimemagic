jMimeMagic (TM) v0.1.5
Copyright (C) 2003-2017, David Castro
Contact:  David Castro <arimus@users.sourceforge.net>

jMimeMagic is a Java library for determining the MIME type of files or streams.

Please see LICENSE in this directory for jMimeMagic licensing information.  See
LICENSE_log4j, LICENSE_oro, LICENSE_xerces, LICENSE_junit respectively for
Log4j, ORO, Xerces2 and JUnit licensing information.  Log4j, ORO, Xerces2 and
JUnit are bundled with jMimeMagic for convenience.

** NOTE **
This API absolutely will change until there is a stable release! Relying on it
to not change is probably NOT a safe bet.  It is an initial release, given as a
(hopefully) better than nothing option.  The plan is for this library to become
much cleaner and well-architected, but only time will tell.  The more you show
interest in this library/nudge me, the more likely that will be the case.
Comments and feedback greatly welcome.


Requirements:
  Java 2 SDK 1.3+
  Apache Maven 1.0.2+
  JUnit 3.8.x
  Jakarta ORO 2.0.x
  Commons Logging 1.0.x
  Log4j 1.2.x
  Xerces 2.4.0 (optional)

Building:
  type 'mvn clean jar:jar'

  should have your jar file in ./target

Testing:
  Log4j setting can be modified in resources/log4j.properties

  Run all unit tests
  ------------------
  edit build.properties and create the line 'maven.test.skip=true'
  type 'maven clean test'
  
  Run test against a particular file
  ----------------------------------
  type 'maven clean run -Dclass=net.sf.jmimemagic.Magic -Dargs=<file to test>'
  - or - simply ./test <file to test> in a unix shell
  (this is similar to the 'file' command in *nix)

Maven:

  To add jMimeMagic as a dependency in a Maven project, you can use the
  following in the dependencies section of your pom.xml.

  <dependency>
      <groupId>net.sf.jmimemagic</groupId>
      <artifactId>jmimemagic</artifactId>
      <version>0.1.3</version>
  </dependency>

Contributions:
  Thanks to the MMBase team (http://www.mmbase.org/) for doing the work of
  creating the original basis for the XML version of the magic file.

  To contribute code or other help, send an email to arimus@users.sourceforge.net
  or submit patches/bug reports/etc on the jMimeMagic project page:

      http://sf.net/projects/jmimemagic/

Notes:
  Remember that you will need the proper libraries (XML Parser/Xerces2, Log4j,
  Commons Logging, and ORO) in the classpath for any applications that use
  jMimeMagic.  If you want to run any of the jUnit tests, then you will also
  need JUnit in the classpath.

Developers:
  David Castro <arimus@users.sourceforge.net>
  Nate Jones <ndjones@users.sourceforge.net>

Problems/questions/suggestions:
  David Castro <arimus@user.sourceforge.net>

