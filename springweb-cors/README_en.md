## Introduction

The jar is very quick and easy to use. By doing nothing other than importing it, you can enable the CORS functionality.

## How To Import

### Maven
Add the following to your `pom.xml`:
```xml
<dependency>
  <groupId>org.springkit.kits</groupId>
  <artifactId>springweb-cors</artifactId>
  <version>0.0.1</version>
<dependency>
```

## How To Configure
By default, cross-origin requests are allowed from any source. To impose restrictions, you can add configurations in the src/main/resources/cors.properties file.

```bash
cors.hosts=
```

### The Configuration File Priority
configuration in outside > configuration in this jar

### The Configuration File Describes
- cors.allow-credentials

  The item can only be set to `true` or `false` (case-insensitive). If it is set, `Access-Control-Allow-Credentials: true` or `false` will be added to the response headers.
  
  Usually, requests do not contain Cookies or HTTP Authentication Information by default. When this setting is enabled, the server can accept them, but the following must be added in the XHR script:

  ```
  var xhr = new XMLHttpRequest();
  xhr.withCredentials = true;
  ```
  
- cors.hosts

  It uses a comma `,` to separate multiple values. The purpose is to specify which cross-origin requests are allowed, based on the `Origin` header in the request.
  
 For example, `cors.hosts=http://192.168.1.1,http://192.168.1.2` means that only two sites can send CORS requests.
  ==Warning: it matches all URLs and distinguishes between HTTP and HTTPS (the trailing slash `/` is not included).==

- cors.url

  Which url should be effected.   When using prefix mode match,  it will be written as `/prefix/*`.  ==no contain context-path==
  If null or `/*`, it means match any url.

- cors.max-age

  The item represents the CORS effective time, with 86400 being the default value.

#### Internal Configuration
It is always effective, but it can be overridden by others.
The content is:
```bash
cors.allow-credentials=true
cors.url=/*
```

## How To Use

### SpringBoot Framework
No operations.

### Spring Framework
In `src/main/resources/webapp/WEB-INF/web.xml`, add a Filter as follows:

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
