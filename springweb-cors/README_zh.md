## 说明
跨域组件，spingboot环境中直接引用jar包即可生效

## 引用
```xml
<dependency>
  <groupId>org.springkit.kits</groupId>
  <artifactId>springweb-cors</artifactId>
  <version>0.0.1</version>
<dependency>
```

## 配置
默认是任何都可以跨域请求，如果需要限制请在`src/main/resources/cors.properties`下添加配置

```bash
cors.hosts=
```

### 配置文件优先级
外部配置文件 > jar内置配置文件

### 配置文件说明
- cors.allow-credentials

  此配置项只能设置成true或false（大小写不敏感），如果设置此值则在响应头中添加`Access-Control-Allow-Credentials: true|false`
  
  CORS请求默认不发送Cookie和HTTP认证信息。设置true时，表示服务端可接受。同时在xhr中要加上。
  
  ```
  var xhr = new XMLHttpRequest();
  xhr.withCredentials = true;
  ```
  
- cors.hosts

  此配置项用`,`表过多值分隔，作用是表示哪些跨域可生效，读取请求中的`Origin`的头进行判断。
  比如配置`cors.hosts=http://192.168.1.1,http://192.168.1.2`，则只有这两个站点请求可跨域。
  ==注意是全匹配，区分http和https （最后的/不要写）==
  
- cors.url

  表示应用在哪些URL上，前缀匹配写成`/前缀/*`。==不要写入context-path路径==
  不写或`/*`则表示匹配所有URL。

- cors.max-age

  表示跨域响应的时间（单位秒），默认是86400

#### jar包内置默认配置
```bash
cors.allow-credentials=true
cors.url=/*
```

### SpringBoot
springboot一般下不需要配置

### spring项目
在`src/main/resources/webapp/WEB-INF/web.xml`中添加Filter

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

