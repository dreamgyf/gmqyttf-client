# Implementation of the client part of the MQTT protocol

[![License](https://img.shields.io/badge/license-Apache%202-green)](https://github.com/dreamgyf/gmqyttf-client/blob/master/LICENSE)
[![Forks](https://img.shields.io/github/forks/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/network/members)
[![Starts](https://img.shields.io/github/stars/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/stargazers)
[![Issues](https://img.shields.io/github/issues/dreamgyf/gmqyttf-client)](https://github.com/dreamgyf/gmqyttf-client/issues)

### Get Started

1. Dependency

* Maven
  
Add Jcenter repository first
  
```xml
<repository>
      <id>jcenter</id>
      <name>jcenter</name>
      <url>https://jcenter.bintray.com</url>
</repository>
```

then
  
```xml
<dependency>
    <groupId>com.dreamgyf.mqtt</groupId>
    <artifactId>gmqyttf-client</artifactId>
    <version>0.1.0</version>
    <type>pom</type>
</dependency>
```

* Gradle

Add Jcenter repository first

```groovy
repositories {
  jcenter()
}
```

then

```groovy
implementation 'com.dreamgyf.mqtt:gmqyttf-client:0.1.0'
```

