[![Build Status](https://travis-ci.org/viaacode/fxp_service.png)](https://travis-ci.org/viaacode/fxp_service)

# FXP Service

Microservice that transfers a file between two servers, triggered by RabbitMQ messages.

## Synopsis

The [File eXchange Protocol
(FXP)](https://en.wikipedia.org/wiki/File_eXchange_Protocol) is a method of
data transfer which uses FTP to transfer data from one remote server to another
(inter-server) without routing this data through the client's connection.

### Roles

| Role                | Handle          |
|---------------------|-----------------|
| **Original Dev**    | @hanneslowette  |
|                     | @dietervanhoof  |
| **Principal/Owner** | @maartends      | 
| **Wing(wo)man**     | @violetina      |


## Deployment/Installation

See the Jenkins documentation here: [Setting up a Jenkins pipeline](https://github.com/viaacode/viaa-meta-dev/blob/master/docs/setting-up-a-jenkins-pipeline.md)

### Maven 

You can compile or build the FXP service using either `compile assembly:single`
or `package` (which includes the `compile assembly:single`).

`mvn package` creates the same 'fat' jar as `mvn clean compile
assembly:single`. Build the project using one of the two following:

- `mvn clean compile assembly:single`
- `mvn clean package`

Once built, the artifact can be deployed to the nexus, assuming credentials for
the VIAA repo are set up correctly:
- `mvn deploy`

Consult the [`mvn
deploy`](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html)
documentation.

#### Deploying to different datacenters

Maven doesn't seem to allow the deployment to 2 repositories at the same time. There can only be one `<repository>` within the `<distributionManagement>` tags. If deployment needs to be done to a different DC, the POM should be manually modified to change the target.
If different credentials are required, they must exist within your settings.xml file.

#### Prerequisites

- Maven

## Usage

### Example

```json
{
    "source_host": "test.ftp.com",
    "source_user": "username",
    "source_password": "password",
    "source_file": "original.txt",
    "source_path": "/home/folder/",
    "destination_host": "other.ftp.com",
    "destination_user": "username",
    "destination_password": "password",
    "destination_file": "file.txt",
    "destination_path": "/home/test/",
    "correlation_id": "some_id", (optional),
    "move": "true" (optional)
}
```
