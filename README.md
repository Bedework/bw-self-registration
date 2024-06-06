# bw-self-registration [![Build Status](https://travis-ci.org/Bedework/bw-self-registration.svg)](https://travis-ci.org/Bedework/bw-self-registration)

This project provides a self registration service for [Bedework](https://www.apereo.org/projects/bedework).

## Requirements

1. JDK 17
2. Maven 3

## Building Locally

> mvn clean install

## Releasing

Releases of this fork are published to Maven Central via Sonatype.

To create a release, you must have:

1. Permissions to publish to the `org.bedework` groupId.
2. `gpg` installed with a published key (release artifacts are signed).

To perform a new release use the release script:

> ./bedework/build/quickstart/linux/util-scripts/release.sh <module-name> "<release-version>" "<new-version>-SNAPSHOT"

When prompted, indicate all updates are committed

For full details, see [Sonatype's documentation for using Maven to publish releases](http://central.sonatype.org/pages/apache-maven.html).

## Release Notes
### 4.0.3
* First successful release 
  
### 4.0.4
* Update library versions
* Fix encoding of password. Ldap requires the prefix
* hawtio can't cope with spaces in mbean info for parameters

### 4.0.5
* Update library versions

### 4.0.6
* Update library versions
* Logging changes

### 4.0.7
* Update library versions

### 4.0.9
* Update library versions
* Switch to PooledHttpClient

### 4.0.10
* Update library versions

### 4.0.11
* Update library versions

### 5.0.0
* Use bedework-parent for builds.
* Update library versions
* Update bw-util version to use new exception handling.
  Also update for new schema build code.

### 5.0.1
* ehcache needs a name.
* Simplify the configuration utilities.
* Turn selfreg into war

#### 5.0.2
* Upgrade library versions
* Fix needed to deal with util.hibernate bug relating to static sessionFactory variable.
  
