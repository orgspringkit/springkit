## Introduction
The document describes a component for handling cross-origin resource sharing (CORS) in a Spring Boot environment, highlighting the simplicity of enabling CORS by merely incorporating a specific JAR package into the project.

## How To Import

### Maven
Add into `pom.xml`, as follow:
```xml
<dependency>
  <groupId>org.springkit.kits</groupId>
  <artifactId>springweb-cors</artifactId>
  <version>0.0.1</version>
<dependency>
```

## How To Configurate
By default, cross-origin requests are allowed from any source. To impose restrictions, you can add configurations in the src/main/resources/cors.properties file.

```bash
cors.hosts=
```

### The Configuration File Priority
configuration in outside > configuration in this jar

### The Configuration File Describes
- cors.allow-credentials
  The item only set be `true|false`(insensitive). if it is be set, it will add `Access-Control-Allow-Credentials: true|false` into response headers.
  
  Usually, the request not contains a Cookie and Http Authentication Info by default. When it's true, the server can accept but in xhr script,  must be added as follow:

  ```
  var xhr = new XMLHttpRequest();
  xhr.withCredentials = true;
  ```
  
- cors.hosts
  It uses a comma `,`, to separate multiple values. Its purpose is to indicate which cross-origin requests are allowed by `Origin` header in request.
  Example, `cors.hosts=http://192.168.1.1,http://192.168.1.2` means only two sites can send CORS request.
  ==Warning, it's all match and distinguishes between HTTP and HTTPS （the last no write '/'）==

- cors.url
  Which url should be effected.   When using prefix mode match,  it will be written as `/prefix/*`.  ==no contain context-path==
  If null or `/*`, it means match any url.

- cors.max-age
  The item is CORS effective time. 86400 is default value.

#### Internal Configuration
It is always effective, and others can override it
The content as:
```bash
cors.allow-credentials=true
cors.url=/*
```

## How To Use

### SpringBoot Framework
No any opertaion.

### Spring Framework
In `src/main/resource/webapp/WEB-INF/web.xml`, add Filter as follow:

```xml
<filter>
  <filter-name>cors</filter-name>
  <filter-class>org.springkit.web.cors.CorsResponseFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>cors</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```
