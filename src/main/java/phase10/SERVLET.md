# Servlet 从零开始完全解析

---

## 目录

1. [什么是 Servlet](#1-什么是servlet)
2. [Servlet 的核心概念](#2-servlet的核心概念)
3. [Servlet 生命周期](#3-servlet生命周期)
4. [第一个 Servlet 程序](#4-第一个servlet程序)
5. [Servlet 核心 API 详解](#5-servlet核心api详解)
6. [请求与响应处理](#6-请求与响应处理)
7. [Servlet 配置方式](#7-servlet配置方式)
8. [会话管理](#8-会话管理)
9. [过滤器 Filter](#9-过滤器filter)
10. [监听器 Listener](#10-监听器listener)
11. [Servlet 3.0+新特性](#11-servlet-30新特性)
12. [完整项目示例](#12-完整项目示例)
13. [常见面试题](#13-常见面试题)

---

## 1. 什么是 Servlet

### 1.1 定义

**Servlet（Server Applet）** 是运行在 Web 服务器上的 Java 程序，用于处理客户端请求并生成动态 Web 内容。

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端 (浏览器)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼ HTTP请求
┌─────────────────────────────────────────────────────────────┐
│                      Web服务器 (Tomcat)                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                  Servlet容器                          │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐          │   │
│  │  │ Servlet1 │  │ Servlet2 │  │ Servlet3 │   ...    │   │
│  │  └──────────┘  └──────────┘  └──────────┘          │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        数据库/其他服务                        │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Servlet 的作用

| 功能     | 说明                                     |
| -------- | ---------------------------------------- |
| 接收请求 | 读取客户端发送的数据（表单、URL 参数等） |
| 处理请求 | 调用业务逻辑、访问数据库                 |
| 生成响应 | 生成 HTML、JSON、XML 等格式的响应        |
| 发送响应 | 将结果返回给客户端                       |

### 1.3 Servlet vs CGI

```
CGI模式：每个请求创建一个新进程
┌────────┐    ┌────────┐    ┌────────┐
│ 请求1   │    │ 请求2   │    │ 请求3   │
└────────┘    └────────┘    └────────┘
     ↓             ↓             ↓
┌────────┐    ┌────────┐    ┌────────┐
│ 进程1   │    │ 进程2   │    │ 进程3   │  ← 资源消耗大
└────────┘    └────────┘    └────────┘

Servlet模式：多线程处理请求
┌────────┐    ┌────────┐    ┌────────┐
│ 请求1   │    │ 请求2   │    │ 请求3   │
└────────┘    └────────┘    └────────┘
     ↓             ↓             ↓
┌─────────────────────────────────────┐
│           Servlet实例（单例）         │
│   ┌────────┐ ┌────────┐ ┌────────┐ │
│   │ 线程1   │ │ 线程2   │ │ 线程3   │ │  ← 资源消耗小
│   └────────┘ └────────┘ └────────┘ │
└─────────────────────────────────────┘
```

---

## 2. Servlet 的核心概念

### 2.1 Servlet 容器

Servlet 容器（也叫 Web 容器）负责管理 Servlet 的生命周期。

**常见的 Servlet 容器：**

- Apache Tomcat（最流行）
- Jetty
- Undertow
- GlassFish

### 2.2 Servlet 体系结构

```
                    ┌─────────────────┐
                    │    Servlet      │  ← 接口
                    │    (接口)        │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  GenericServlet │  ← 抽象类（协议无关）
                    │    (抽象类)      │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   HttpServlet   │  ← 抽象类（HTTP协议）
                    │    (抽象类)      │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   MyServlet     │  ← 我们编写的Servlet
                    │    (具体类)      │
                    └─────────────────┘
```

### 2.3 核心接口和类

```java
// Servlet接口 - 所有Servlet的根接口
public interface Servlet {
    void init(ServletConfig config);      // 初始化
    void service(ServletRequest req, ServletResponse res);  // 服务
    void destroy();                        // 销毁
    ServletConfig getServletConfig();      // 获取配置
    String getServletInfo();               // 获取信息
}
```

---

## 3. Servlet 生命周期

### 3.1 生命周期图解

```
┌──────────────────────────────────────────────────────────────────┐
│                        Servlet 生命周期                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐  │
│   │  加载    │────▶│  实例化  │────▶│  初始化  │────▶│  就绪    │  │
│   │ Loading │     │Instantiate│    │  init() │     │ Ready   │  │
│   └─────────┘     └─────────┘     └─────────┘     └────┬────┘  │
│        ▲                                               │        │
│        │                                               ▼        │
│   容器启动或                                      ┌─────────┐   │
│   首次请求                                       │ service()│   │
│                                                  │ 处理请求  │   │
│   ┌─────────┐                                    └────┬────┘   │
│   │  卸载    │◀────────────────────────────────────────┘        │
│   │ Unload  │         容器关闭或重新加载                         │
│   └────┬────┘                                                   │
│        │                                                        │
│        ▼                                                        │
│   ┌─────────┐                                                   │
│   │ destroy()│                                                   │
│   │  销毁    │                                                   │
│   └─────────┘                                                   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### 3.2 生命周期代码演示

```java
import javax.servlet.*;
import java.io.IOException;

public class LifecycleServlet implements Servlet {

    private ServletConfig config;

    // 1. 构造方法 - 实例化时调用（只调用一次）
    public LifecycleServlet() {
        System.out.println("1. 构造方法被调用 - Servlet实例化");
    }

    // 2. init方法 - 初始化时调用（只调用一次）
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        System.out.println("2. init()方法被调用 - Servlet初始化");
        // 可以在这里进行一些初始化操作
        // 如：加载配置文件、建立数据库连接池等
    }

    // 3. service方法 - 每次请求都会调用
    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        System.out.println("3. service()方法被调用 - 处理请求");
        res.getWriter().write("Hello Servlet!");
    }

    // 4. destroy方法 - 销毁时调用（只调用一次）
    @Override
    public void destroy() {
        System.out.println("4. destroy()方法被调用 - Servlet销毁");
        // 可以在这里进行资源释放
        // 如：关闭数据库连接、释放文件句柄等
    }

    @Override
    public ServletConfig getServletConfig() {
        return this.config;
    }

    @Override
    public String getServletInfo() {
        return "Lifecycle Demo Servlet";
    }
}
```

### 3.3 生命周期时序

| 阶段      | 调用时机           | 调用次数 | 用途         |
| --------- | ------------------ | -------- | ------------ |
| 构造方法  | 首次请求或容器启动 | 1 次     | 创建实例     |
| init()    | 构造方法之后       | 1 次     | 初始化资源   |
| service() | 每次请求           | 多次     | 处理业务逻辑 |
| destroy() | 容器关闭或卸载     | 1 次     | 释放资源     |

---

## 4. 第一个 Servlet 程序

### 4.1 项目结构

```
MyServletProject/
├── pom.xml                          # Maven配置
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── servlet/
        │               └── HelloServlet.java
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml          # 部署描述符
            └── index.html
```

### 4.2 Maven 依赖 (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>servlet-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Servlet API -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>  <!-- 容器已提供，不打包 -->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.1</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4.3 HelloServlet.java

```java
package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 第一个Servlet示例
 * 继承HttpServlet，重写doGet和doPost方法
 */
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // 设置响应内容类型和编码
        response.setContentType("text/html;charset=UTF-8");

        // 获取输出流
        PrintWriter out = response.getWriter();

        // 输出HTML内容
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Hello Servlet</title></head>");
        out.println("<body>");
        out.println("<h1>Hello, Servlet!</h1>");
        out.println("<p>这是我的第一个Servlet程序</p>");
        out.println("<p>当前时间: " + new java.util.Date() + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        // POST请求也调用doGet处理
        doGet(request, response);
    }
}
```

### 4.4 web.xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- Servlet定义 -->
    <servlet>
        <servlet-name>helloServlet</servlet-name>
        <servlet-class>com.example.servlet.HelloServlet</servlet-class>
        <!-- 启动时加载，数值越小优先级越高 -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- Servlet映射 -->
    <servlet-mapping>
        <servlet-name>helloServlet</servlet-name>
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>

</web-app>
```

---

## 5. Servlet 核心 API 详解

### 5.1 ServletConfig（Servlet 配置）

```java
public class ConfigServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // 获取Servlet名称
        String servletName = config.getServletName();
        System.out.println("Servlet名称: " + servletName);

        // 获取初始化参数
        String dbUrl = config.getInitParameter("dbUrl");
        String dbUser = config.getInitParameter("dbUser");
        System.out.println("数据库URL: " + dbUrl);
        System.out.println("数据库用户: " + dbUser);

        // 获取所有初始化参数名
        Enumeration<String> paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String value = config.getInitParameter(name);
            System.out.println(name + " = " + value);
        }

        // 获取ServletContext
        ServletContext context = config.getServletContext();
    }
}
```

**web.xml 中配置初始化参数：**

```xml
<servlet>
    <servlet-name>configServlet</servlet-name>
    <servlet-class>com.example.servlet.ConfigServlet</servlet-class>
    <init-param>
        <param-name>dbUrl</param-name>
        <param-value>jdbc:mysql://localhost:3306/test</param-value>
    </init-param>
    <init-param>
        <param-name>dbUser</param-name>
        <param-value>root</param-value>
    </init-param>
</servlet>
```

### 5.2 ServletContext（应用上下文）

```java
public class ContextServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // 获取ServletContext（整个Web应用共享）
        ServletContext context = getServletContext();

        // 1. 获取全局初始化参数
        String appName = context.getInitParameter("appName");

        // 2. 获取应用的真实路径
        String realPath = context.getRealPath("/WEB-INF/config.properties");

        // 3. 获取资源流
        InputStream is = context.getResourceAsStream("/WEB-INF/config.properties");

        // 4. 应用域属性（全局共享数据）
        context.setAttribute("visitCount", 100);
        Integer count = (Integer) context.getAttribute("visitCount");
        context.removeAttribute("visitCount");

        // 5. 获取MIME类型
        String mimeType = context.getMimeType("test.pdf");  // application/pdf

        // 6. 获取服务器信息
        String serverInfo = context.getServerInfo();  // Apache Tomcat/9.0.x

        // 7. 日志记录
        context.log("这是一条日志信息");
    }
}
```

**全局初始化参数配置：**

```xml
<web-app>
    <!-- 全局参数，所有Servlet共享 -->
    <context-param>
        <param-name>appName</param-name>
        <param-value>我的Web应用</param-value>
    </context-param>

    <context-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </context-param>
</web-app>
```

### 5.3 四大域对象对比

```
┌─────────────────────────────────────────────────────────────────────┐
│                          四大域对象                                   │
├─────────────────┬───────────────┬───────────────┬───────────────────┤
│     域对象       │    作用范围    │    生命周期    │      创建时机      │
├─────────────────┼───────────────┼───────────────┼───────────────────┤
│ PageContext     │   当前页面     │   页面执行期间  │   JSP页面开始      │
├─────────────────┼───────────────┼───────────────┼───────────────────┤
│ HttpServletRequest│  一次请求    │   一次请求期间  │   客户端发起请求   │
├─────────────────┼───────────────┼───────────────┼───────────────────┤
│ HttpSession     │   一次会话     │   会话期间     │   首次getSession  │
├─────────────────┼───────────────┼───────────────┼───────────────────┤
│ ServletContext  │   整个应用     │   应用运行期间  │   服务器启动       │
└─────────────────┴───────────────┴───────────────┴───────────────────┘

作用范围从小到大：PageContext < Request < Session < ServletContext
```

---

## 6. 请求与响应处理

### 6.1 HttpServletRequest 详解

```java
public class RequestDemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // ============ 1. 获取请求行信息 ============
        String method = request.getMethod();           // GET
        String uri = request.getRequestURI();          // /app/demo
        String url = request.getRequestURL().toString(); // http://localhost:8080/app/demo
        String protocol = request.getProtocol();       // HTTP/1.1
        String queryString = request.getQueryString(); // name=john&age=20

        // ============ 2. 获取请求头信息 ============
        String host = request.getHeader("Host");
        String userAgent = request.getHeader("User-Agent");
        String contentType = request.getContentType();

        // 获取所有请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }

        // ============ 3. 获取请求参数 ============
        // 单个参数
        String name = request.getParameter("name");

        // 多个同名参数（如复选框）
        String[] hobbies = request.getParameterValues("hobby");

        // 获取所有参数名
        Enumeration<String> paramNames = request.getParameterNames();

        // 获取参数Map
        Map<String, String[]> paramMap = request.getParameterMap();

        // ============ 4. 获取客户端信息 ============
        String remoteAddr = request.getRemoteAddr();   // 客户端IP
        String remoteHost = request.getRemoteHost();   // 客户端主机名
        int remotePort = request.getRemotePort();      // 客户端端口

        // ============ 5. 获取服务器信息 ============
        String serverName = request.getServerName();   // localhost
        int serverPort = request.getServerPort();       // 8080
        String contextPath = request.getContextPath(); // /app
        String servletPath = request.getServletPath(); // /demo

        // ============ 6. 请求域属性 ============
        request.setAttribute("user", new User("张三", 25));
        Object user = request.getAttribute("user");
        request.removeAttribute("user");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // 设置请求体编码（必须在获取参数之前设置）
        request.setCharacterEncoding("UTF-8");

        // 获取POST参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 获取请求体输入流（用于接收JSON等数据）
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
    }
}
```

### 6.2 HttpServletResponse 详解

```java
public class ResponseDemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // ============ 1. 设置响应状态码 ============
        response.setStatus(200);                      // 正常
        // response.setStatus(404);                   // 未找到
        // response.sendError(500, "服务器内部错误");   // 发送错误

        // ============ 2. 设置响应头 ============
        response.setHeader("Custom-Header", "value");
        response.addHeader("Set-Cookie", "name=value");
        response.setIntHeader("Refresh", 5);          // 5秒后刷新
        response.setDateHeader("Expires", System.currentTimeMillis() + 3600000);

        // ============ 3. 设置响应内容类型和编码 ============
        response.setContentType("text/html;charset=UTF-8");
        // 或者分开设置
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // ============ 4. 输出响应内容 ============
        // 方式一：字符输出流（用于文本）
        PrintWriter out = response.getWriter();
        out.println("<h1>Hello World</h1>");

        // 方式二：字节输出流（用于二进制文件）
        // ServletOutputStream os = response.getOutputStream();
        // os.write(bytes);

        // 注意：getWriter()和getOutputStream()不能同时使用！
    }

    // 重定向示例
    protected void redirectDemo(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {
        // 重定向（客户端重新发起请求）
        response.sendRedirect("/app/target");

        // 或者
        response.setStatus(302);
        response.setHeader("Location", "/app/target");
    }

    // 文件下载示例
    protected void downloadFile(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                          "attachment;filename=test.pdf");

        // 读取文件并写入响应
        String filePath = getServletContext().getRealPath("/files/test.pdf");
        try (FileInputStream fis = new FileInputStream(filePath);
             ServletOutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }

    // JSON响应示例
    protected void jsonResponse(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.print("{\"code\": 200, \"message\": \"success\", \"data\": {\"name\": \"张三\"}}");
    }
}
```

### 6.3 请求转发与重定向对比

```
┌──────────────────────────────────────────────────────────────────┐
│                        请求转发 (Forward)                         │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   浏览器          Servlet A              Servlet B               │
│     │                │                      │                    │
│     │   请求         │                      │                    │
│     │───────────────▶│                      │                    │
│     │                │   forward            │                    │
│     │                │─────────────────────▶│                    │
│     │                │                      │  处理              │
│     │                │                      │                    │
│     │◀──────────────────────────────────────│  响应              │
│     │                                                            │
│   特点：                                                          │
│   - 地址栏URL不变                                                 │
│   - 服务器内部跳转                                                 │
│   - 一次请求                                                      │
│   - 可以共享request域数据                                         │
│   - 只能跳转到本应用内部资源                                       │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                        重定向 (Redirect)                          │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   浏览器          Servlet A              Servlet B               │
│     │                │                      │                    │
│     │   请求1        │                      │                    │
│     │───────────────▶│                      │                    │
│     │                │                      │                    │
│     │◀───────────────│  302 + Location      │                    │
│     │                                       │                    │
│     │   请求2                               │                    │
│     │──────────────────────────────────────▶│                    │
│     │                                       │  处理              │
│     │◀──────────────────────────────────────│  响应              │
│     │                                                            │
│   特点：                                                          │
│   - 地址栏URL改变                                                 │
│   - 客户端重新发起请求                                             │
│   - 两次请求                                                      │
│   - 不能共享request域数据                                         │
│   - 可以跳转到任意URL（包括外部网站）                               │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

**代码实现：**

```java
public class ForwardRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("forward".equals(action)) {
            // 请求转发
            request.setAttribute("message", "这是转发传递的数据");
            request.getRequestDispatcher("/target")
                   .forward(request, response);

        } else if ("redirect".equals(action)) {
            // 重定向
            response.sendRedirect("/app/target");

            // 重定向传递数据需要使用URL参数或Session
            // response.sendRedirect("/app/target?message=hello");
        }
    }
}
```

---

## 7. Servlet 配置方式

### 7.1 web.xml 配置（传统方式）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- ============ Servlet定义 ============ -->
    <servlet>
        <servlet-name>userServlet</servlet-name>
        <servlet-class>com.example.servlet.UserServlet</servlet-class>

        <!-- 初始化参数 -->
        <init-param>
            <param-name>pageSize</param-name>
            <param-value>10</param-value>
        </init-param>

        <!-- 启动时加载 -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- ============ Servlet映射 ============ -->
    <servlet-mapping>
        <servlet-name>userServlet</servlet-name>
        <url-pattern>/user/*</url-pattern>
    </servlet-mapping>

    <!-- 支持多个URL映射到同一个Servlet -->
    <servlet-mapping>
        <servlet-name>userServlet</servlet-name>
        <url-pattern>/api/user</url-pattern>
    </servlet-mapping>

    <!-- ============ 全局参数 ============ -->
    <context-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </context-param>

    <!-- ============ 欢迎页面 ============ -->
    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

    <!-- ============ 错误页面 ============ -->
    <error-page>
        <error-code>404</error-code>
        <location>/error/404.html</location>
    </error-page>

    <error-page>
        <error-code>500</error-code>
        <location>/error/500.html</location>
    </error-page>

    <error-page>
        <exception-type>java.lang.Exception</exception-type>
        <location>/error/error.html</location>
    </error-page>

</web-app>
```

### 7.2 注解配置（Servlet 3.0+）

```java
import javax.servlet.annotation.*;

/**
 * 使用@WebServlet注解配置Servlet
 */
@WebServlet(
    name = "userServlet",                           // Servlet名称
    urlPatterns = {"/user", "/user/*"},             // URL映射
    loadOnStartup = 1,                              // 启动加载
    initParams = {                                  // 初始化参数
        @WebInitParam(name = "pageSize", value = "10"),
        @WebInitParam(name = "maxAge", value = "100")
    },
    asyncSupported = true                           // 支持异步
)
public class UserServlet extends HttpServlet {

    private int pageSize;

    @Override
    public void init() throws ServletException {
        // 获取初始化参数
        pageSize = Integer.parseInt(getInitParameter("pageSize"));
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().write("Page Size: " + pageSize);
    }
}
```

### 7.3 URL 映射规则

```
┌─────────────────────────────────────────────────────────────────────┐
│                        URL-Pattern 匹配规则                          │
├─────────────────┬───────────────────────────────────────────────────┤
│     模式类型     │                    示例                           │
├─────────────────┼───────────────────────────────────────────────────┤
│  精确匹配        │  /user/login     只匹配 /user/login              │
├─────────────────┼───────────────────────────────────────────────────┤
│  路径匹配        │  /user/*         匹配 /user/任意路径              │
│                 │                   /user, /user/1, /user/info      │
├─────────────────┼───────────────────────────────────────────────────┤
│  扩展名匹配      │  *.do            匹配所有.do结尾的请求             │
│                 │  *.action        匹配所有.action结尾的请求         │
├─────────────────┼───────────────────────────────────────────────────┤
│  默认匹配        │  /               默认Servlet，匹配所有未匹配的请求  │
└─────────────────┴───────────────────────────────────────────────────┘

匹配优先级：精确匹配 > 路径匹配（最长优先）> 扩展名匹配 > 默认匹配
```

**示例：**

```java
// 精确匹配
@WebServlet("/user/login")

// 路径匹配
@WebServlet("/api/*")

// 扩展名匹配
@WebServlet("*.json")

// 多个URL映射
@WebServlet(urlPatterns = {"/user", "/user/*", "/api/user"})

// 默认Servlet
@WebServlet("/")
```

---

## 8. 会话管理

### 8.1 Cookie

```java
public class CookieServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("set".equals(action)) {
            // ============ 创建Cookie ============
            Cookie cookie = new Cookie("username", "zhangsan");

            // 设置有效期（秒）
            // 正数：持久化到硬盘，浏览器关闭后仍有效
            // 负数或不设置：会话Cookie，浏览器关闭后失效
            // 0：立即删除Cookie
            cookie.setMaxAge(60 * 60 * 24 * 7);  // 7天

            // 设置作用路径
            cookie.setPath("/");  // 整个应用有效

            // 设置作用域名
            cookie.setDomain("example.com");  // 包括子域名

            // 设置HttpOnly（防止JS访问，增加安全性）
            cookie.setHttpOnly(true);

            // 设置Secure（仅HTTPS传输）
            cookie.setSecure(true);

            // 添加Cookie到响应
            response.addCookie(cookie);

            // 可以添加多个Cookie
            Cookie rememberMe = new Cookie("rememberMe", "true");
            rememberMe.setMaxAge(60 * 60 * 24 * 30);  // 30天
            response.addCookie(rememberMe);

            response.getWriter().write("Cookie已设置");

        } else if ("get".equals(action)) {
            // ============ 读取Cookie ============
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    System.out.println(name + " = " + value);

                    if ("username".equals(name)) {
                        response.getWriter().write("Welcome, " + value);
                    }
                }
            } else {
                response.getWriter().write("No cookies found");
            }

        } else if ("delete".equals(action)) {
            // ============ 删除Cookie ============
            Cookie cookie = new Cookie("username", "");
            cookie.setMaxAge(0);  // 设置为0立即删除
            cookie.setPath("/");  // 路径要和设置时一致
            response.addCookie(cookie);

            response.getWriter().write("Cookie已删除");
        }
    }
}
```

### 8.2 Session

```
┌──────────────────────────────────────────────────────────────────┐
│                        Session工作原理                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   浏览器                              服务器                       │
│     │                                   │                        │
│     │   首次请求（无Session）            │                        │
│     │─────────────────────────────────▶│                        │
│     │                                   │  创建Session           │
│     │                                   │  SessionID = ABC123    │
│     │   响应 + Set-Cookie: JSESSIONID=ABC123                     │
│     │◀─────────────────────────────────│                        │
│     │                                   │                        │
│     │   后续请求 + Cookie: JSESSIONID=ABC123                     │
│     │─────────────────────────────────▶│                        │
│     │                                   │  根据ID找到Session     │
│     │◀─────────────────────────────────│  返回响应               │
│     │                                                            │
└──────────────────────────────────────────────────────────────────┘
```

```java
public class SessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // ============ 获取Session ============
        // getSession() 或 getSession(true)：没有则创建
        // getSession(false)：没有则返回null
        HttpSession session = request.getSession();

        // ============ Session基本信息 ============
        String sessionId = session.getId();
        long creationTime = session.getCreationTime();
        long lastAccessedTime = session.getLastAccessedTime();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        boolean isNew = session.isNew();

        out.println("<h2>Session信息</h2>");
        out.println("<p>Session ID: " + sessionId + "</p>");
        out.println("<p>创建时间: " + new Date(creationTime) + "</p>");
        out.println("<p>最后访问时间: " + new Date(lastAccessedTime) + "</p>");
        out.println("<p>超时时间: " + maxInactiveInterval + "秒</p>");
        out.println("<p>是否新Session: " + isNew + "</p>");

        // ============ Session属性操作 ============
        // 存储数据
        session.setAttribute("user", new User("张三", 25));
        session.setAttribute("loginTime", new Date());

        // 获取数据
        User user = (User) session.getAttribute("user");

        // 删除数据
        session.removeAttribute("loginTime");

        // 获取所有属性名
        Enumeration<String> attributeNames = session.getAttributeNames();

        // ============ Session配置 ============
        // 设置超时时间（秒）
        session.setMaxInactiveInterval(30 * 60);  // 30分钟

        // ============ 销毁Session ============
        // session.invalidate();  // 使Session立即失效
    }

    // 登录示例
    protected void login(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 验证用户（实际应查询数据库）
        if ("admin".equals(username) && "123456".equals(password)) {
            // 登录成功，将用户信息存入Session
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", username);
            session.setAttribute("isLogin", true);

            response.sendRedirect("/home");
        } else {
            response.sendRedirect("/login?error=1");
        }
    }

    // 登出示例
    protected void logout(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 销毁Session
        }
        response.sendRedirect("/login");
    }
}
```

### 8.3 Session 配置（web.xml）

```xml
<web-app>
    <!-- Session配置 -->
    <session-config>
        <!-- 超时时间（分钟） -->
        <session-timeout>30</session-timeout>

        <!-- Cookie配置 -->
        <cookie-config>
            <name>MYSESSIONID</name>
            <path>/</path>
            <http-only>true</http-only>
            <secure>false</secure>
            <max-age>-1</max-age>
        </cookie-config>

        <!-- 跟踪模式 -->
        <tracking-mode>COOKIE</tracking-mode>
    </session-config>
</web-app>
```

---

## 9. 过滤器 Filter

### 9.1 Filter 工作原理

```
┌──────────────────────────────────────────────────────────────────┐
│                        Filter过滤链                               │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   请求 ──▶ Filter1 ──▶ Filter2 ──▶ Filter3 ──▶ Servlet          │
│                                                                  │
│   响应 ◀── Filter1 ◀── Filter2 ◀── Filter3 ◀── Servlet          │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

详细流程：
┌────────────────────────────────────────────────────────────────────┐
│  客户端请求                                                         │
│      │                                                             │
│      ▼                                                             │
│  ┌──────────┐                                                      │
│  │ Filter1  │  doFilter() {                                        │
│  │          │      // 1. 前置处理                                   │
│  │          │      chain.doFilter(req, res);  // 放行               │
│  │          │      // 4. 后置处理                                   │
│  │          │  }                                                   │
│  └────┬─────┘                                                      │
│       │                                                            │
│       ▼                                                            │
│  ┌──────────┐                                                      │
│  │ Filter2  │  doFilter() {                                        │
│  │          │      // 2. 前置处理                                   │
│  │          │      chain.doFilter(req, res);  // 放行               │
│  │          │      // 3. 后置处理                                   │
│  │          │  }                                                   │
│  └────┬─────┘                                                      │
│       │                                                            │
│       ▼                                                            │
│  ┌──────────┐                                                      │
│  │ Servlet  │  service()                                           │
│  └──────────┘                                                      │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### 9.2 Filter 实现

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 字符编码过滤器
 */
@WebFilter(
    filterName = "encodingFilter",
    urlPatterns = "/*",
    initParams = {
        @WebInitParam(name = "encoding", value = "UTF-8")
    }
)
public class EncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 获取初始化参数
        encoding = filterConfig.getInitParameter("encoding");
        if (encoding == null) {
            encoding = "UTF-8";
        }
        System.out.println("EncodingFilter 初始化，编码: " + encoding);
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        // 设置请求编码
        request.setCharacterEncoding(encoding);

        // 设置响应编码
        response.setCharacterEncoding(encoding);
        response.setContentType("text/html;charset=" + encoding);

        // 放行，继续执行后续的Filter或Servlet
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("EncodingFilter 销毁");
    }
}
```

### 9.3 登录验证过滤器

```java
@WebFilter(urlPatterns = {"/admin/*", "/user/*"})
public class LoginFilter implements Filter {

    // 不需要登录的路径
    private static final Set<String> EXCLUDE_PATHS = new HashSet<>(
        Arrays.asList("/login", "/register", "/css/", "/js/", "/images/")
    );

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        // 检查是否是排除路径
        boolean isExcluded = EXCLUDE_PATHS.stream()
                .anyMatch(path::startsWith);

        if (isExcluded) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否登录
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null &&
                             session.getAttribute("loginUser") != null;

        if (isLoggedIn) {
            // 已登录，放行
            chain.doFilter(request, response);
        } else {
            // 未登录，重定向到登录页
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
```

### 9.4 日志记录过滤器

```java
@WebFilter("/*")
public class LoggingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIP = request.getRemoteAddr();

        logger.info(String.format("请求开始 - %s %s%s from %s",
            method, uri,
            queryString != null ? "?" + queryString : "",
            clientIP));

        try {
            // 执行请求
            chain.doFilter(req, res);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            HttpServletResponse response = (HttpServletResponse) res;
            logger.info(String.format("请求结束 - %s %s 状态码:%d 耗时:%dms",
                method, uri, response.getStatus(), duration));
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
```

### 9.5 web.xml 中配置 Filter

```xml
<web-app>
    <!-- Filter定义 -->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>com.example.filter.EncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>

    <filter>
        <filter-name>loginFilter</filter-name>
        <filter-class>com.example.filter.LoginFilter</filter-class>
    </filter>

    <!-- Filter映射（顺序决定执行顺序） -->
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <filter-mapping>
        <filter-name>loginFilter</filter-name>
        <url-pattern>/admin/*</url-pattern>
        <url-pattern>/user/*</url-pattern>
    </filter-mapping>
</web-app>
```

---

## 10. 监听器 Listener

### 10.1 监听器分类

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Servlet监听器分类                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    ServletContext监听器                       │   │
│  │  ServletContextListener        - 应用启动/关闭               │   │
│  │  ServletContextAttributeListener - Context属性变化           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    HttpSession监听器                          │   │
│  │  HttpSessionListener           - Session创建/销毁            │   │
│  │  HttpSessionAttributeListener  - Session属性变化             │   │
│  │  HttpSessionBindingListener    - 对象绑定/解绑Session         │   │
│  │  HttpSessionActivationListener - Session钝化/活化            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    ServletRequest监听器                       │   │
│  │  ServletRequestListener        - 请求创建/销毁               │   │
│  │  ServletRequestAttributeListener - Request属性变化           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 10.2 ServletContextListener（最常用）

```java
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 应用程序启动/关闭监听器
 * 常用于：初始化全局资源、加载配置、创建线程池等
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    /**
     * 应用启动时调用
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== 应用程序启动 ===");

        ServletContext context = sce.getServletContext();

        // 1. 加载配置文件
        String configPath = context.getRealPath("/WEB-INF/config.properties");
        Properties props = loadConfig(configPath);
        context.setAttribute("config", props);

        // 2. 初始化数据库连接池
        initDataSource(props);

        // 3. 初始化缓存
        initCache();

        // 4. 启动定时任务
        scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("定时任务执行: " + new Date());
        }, 0, 1, TimeUnit.HOURS);

        System.out.println("=== 应用程序初始化完成 ===");
    }

    /**
     * 应用关闭时调用
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== 应用程序关闭 ===");

        // 1. 关闭定时任务
        if (scheduler != null) {
            scheduler.shutdown();
        }

        // 2. 关闭数据库连接池
        closeDataSource();

        // 3. 清理缓存
        clearCache();

        System.out.println("=== 资源清理完成 ===");
    }

    private Properties loadConfig(String path) {
        // 加载配置文件实现
        return new Properties();
    }

    private void initDataSource(Properties props) {
        // 初始化数据源
    }

    private void closeDataSource() {
        // 关闭数据源
    }

    private void initCache() {
        // 初始化缓存
    }

    private void clearCache() {
        // 清理缓存
    }
}
```

### 10.3 HttpSessionListener

```java
@WebListener
public class SessionListener implements HttpSessionListener {

    // 在线用户计数（线程安全）
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = onlineCount.incrementAndGet();
        System.out.println("Session创建: " + se.getSession().getId());
        System.out.println("当前在线人数: " + count);

        // 将在线人数存入ServletContext
        se.getSession().getServletContext()
          .setAttribute("onlineCount", count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = onlineCount.decrementAndGet();
        System.out.println("Session销毁: " + se.getSession().getId());
        System.out.println("当前在线人数: " + count);

        se.getSession().getServletContext()
          .setAttribute("onlineCount", count);
    }

    public static int getOnlineCount() {
        return onlineCount.get();
    }
}
```

### 10.4 ServletRequestListener

```java
@WebListener
public class RequestListener implements ServletRequestListener {

    private static final AtomicLong requestCount = new AtomicLong(0);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        long count = requestCount.incrementAndGet();

        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();

        // 记录请求开始时间
        request.setAttribute("startTime", System.currentTimeMillis());

        System.out.println(String.format("请求 #%d 开始: %s %s",
            count, request.getMethod(), request.getRequestURI()));
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();

        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        System.out.println(String.format("请求结束: %s %s 耗时: %dms",
            request.getMethod(), request.getRequestURI(), duration));
    }
}
```

---

## 11. Servlet 3.0+新特性

### 11.1 注解配置

```java
// Servlet注解
@WebServlet(
    name = "myServlet",
    urlPatterns = "/my",
    loadOnStartup = 1,
    initParams = @WebInitParam(name = "key", value = "value"),
    asyncSupported = true
)

// Filter注解
@WebFilter(
    filterName = "myFilter",
    urlPatterns = "/*",
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
)

// Listener注解
@WebListener
```

### 11.2 异步处理

```java
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // 开启异步模式
        AsyncContext asyncContext = request.startAsync();

        // 设置超时时间
        asyncContext.setTimeout(30000);

        // 添加监听器
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                System.out.println("异步处理完成");
            }

            @Override
            public void onTimeout(AsyncEvent event) {
                System.out.println("异步处理超时");
            }

            @Override
            public void onError(AsyncEvent event) {
                System.out.println("异步处理错误");
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
                System.out.println("异步处理开始");
            }
        });

        // 提交异步任务
        executor.submit(() -> {
            try {
                // 模拟耗时操作
                Thread.sleep(3000);

                // 获取响应
                HttpServletResponse asyncResponse =
                    (HttpServletResponse) asyncContext.getResponse();
                asyncResponse.getWriter().write("异步处理完成！");

                // 完成异步处理
                asyncContext.complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 主线程立即返回，不等待异步任务完成
        System.out.println("主线程继续执行...");
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
}
```

### 11.3 文件上传

```java
@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB，超过则写入磁盘
    maxFileSize = 1024 * 1024 * 10,       // 单个文件最大10MB
    maxRequestSize = 1024 * 1024 * 50,    // 请求最大50MB
    location = "/tmp"                      // 临时文件存储位置
)
public class FileUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取上传目录的真实路径
        String uploadPath = getServletContext().getRealPath("") +
                           File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        // 获取所有Part（包括文件和普通表单字段）
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            String fileName = getFileName(part);

            if (fileName != null && !fileName.isEmpty()) {
                // 这是文件字段
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                String filePath = uploadPath + File.separator + uniqueFileName;

                // 保存文件
                part.write(filePath);

                out.println("<p>文件上传成功: " + fileName + "</p>");
                out.println("<p>文件大小: " + part.getSize() + " bytes</p>");
                out.println("<p>Content-Type: " + part.getContentType() + "</p>");
            } else {
                // 这是普通表单字段
                String fieldName = part.getName();
                String fieldValue = request.getParameter(fieldName);
                out.println("<p>" + fieldName + ": " + fieldValue + "</p>");
            }
        }

        out.println("</body></html>");
    }

    /**
     * 从Part中获取文件名
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");

        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1)
                             .trim()
                             .replace("\"", "");
            }
        }
        return null;
    }
}
```

**上传表单 HTML：**

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>文件上传</title>
  </head>
  <body>
    <h2>文件上传示例</h2>
    <form action="upload" method="post" enctype="multipart/form-data">
      <p>
        <label>描述：</label>
        <input type="text" name="description" />
      </p>
      <p>
        <label>文件1：</label>
        <input type="file" name="file1" />
      </p>
      <p>
        <label>文件2：</label>
        <input type="file" name="file2" />
      </p>
      <p>
        <input type="submit" value="上传" />
      </p>
    </form>
  </body>
</html>
```

---

## 12. 完整项目示例

### 12.1 项目结构

```
user-management/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           ├── servlet/
        │           │   ├── UserServlet.java
        │           │   └── LoginServlet.java
        │           ├── filter/
        │           │   ├── EncodingFilter.java
        │           │   └── AuthFilter.java
        │           ├── listener/
        │           │   └── AppListener.java
        │           ├── model/
        │           │   └── User.java
        │           ├── dao/
        │           │   └── UserDao.java
        │           └── service/
        │               └── UserService.java
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml
            ├── css/
            │   └── style.css
            ├── js/
            │   └── app.js
            ├── login.html
            └── index.html
```

### 12.2 Model 层

```java
// User.java
package com.example.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private Date createTime;

    // 构造方法
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createTime = new Date();
    }

    // Getter和Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
```

### 12.3 DAO 层

```java
// UserDao.java
package com.example.dao;

import com.example.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserDao {

    // 模拟数据库（实际项目应使用真实数据库）
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    // 初始化测试数据
    static {
        User admin = new User("admin", "admin123", "admin@example.com");
        admin.setId(idGenerator.getAndIncrement());
        users.put(admin.getId(), admin);
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public User findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    public boolean delete(Long id) {
        return users.remove(id) != null;
    }

    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
}
```

### 12.4 Service 层

```java
// UserService.java
package com.example.service;

import com.example.dao.UserDao;
import com.example.model.User;
import java.util.List;

public class UserService {

    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User register(String username, String password, String email) {
        if (userDao.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User(username, password, email);
        return userDao.save(user);
    }

    public User getUser(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(User user) {
        return userDao.save(user);
    }

    public boolean deleteUser(Long id) {
        return userDao.delete(id);
    }
}
```

### 12.5 Servlet 层

```java
// LoginServlet.java
package com.example.servlet;

import com.example.model.User;
import com.example.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // 显示登录页面
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userService.login(username, password);

            if (user != null) {
                // 登录成功，创建Session
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", user);
                session.setMaxInactiveInterval(30 * 60); // 30分钟

                out.print("{\"code\":200,\"message\":\"登录成功\",\"data\":{\"username\":\""
                         + user.getUsername() + "\"}}");
            } else {
                out.print("{\"code\":401,\"message\":\"用户名或密码错误\"}");
            }
        } catch (Exception e) {
            out.print("{\"code\":500,\"message\":\"服务器错误: " + e.getMessage() + "\"}");
        }
    }
}

// LogoutServlet.java
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
```

```java
// UserServlet.java
package com.example.servlet;

import com.example.model.User;
import com.example.service.UserService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                // GET /api/users - 获取所有用户
                List<User> users = userService.getAllUsers();
                out.print(gson.toJson(new Result(200, "success", users)));
            } else {
                // GET /api/users/{id} - 获取单个用户
                Long id = Long.parseLong(pathInfo.substring(1));
                User user = userService.getUser(id);
                if (user != null) {
                    out.print(gson.toJson(new Result(200, "success", user)));
                } else {
                    response.setStatus(404);
                    out.print(gson.toJson(new Result(404, "用户不存在", null)));
                }
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print(gson.toJson(new Result(500, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 读取请求体
            BufferedReader reader = request.getReader();
            User user = gson.fromJson(reader, User.class);

            // 注册用户
            User savedUser = userService.register(
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
            );

            response.setStatus(201);
            out.print(gson.toJson(new Result(201, "注册成功", savedUser)));

        } catch (Exception e) {
            response.setStatus(400);
            out.print(gson.toJson(new Result(400, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            User user = gson.fromJson(reader, User.class);

            User updatedUser = userService.updateUser(user);
            out.print(gson.toJson(new Result(200, "更新成功", updatedUser)));

        } catch (Exception e) {
            response.setStatus(500);
            out.print(gson.toJson(new Result(500, e.getMessage(), null)));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                Long id = Long.parseLong(pathInfo.substring(1));
                boolean deleted = userService.deleteUser(id);

                if (deleted) {
                    out.print(gson.toJson(new Result(200, "删除成功", null)));
                } else {
                    response.setStatus(404);
                    out.print(gson.toJson(new Result(404, "用户不存在", null)));
                }
            } else {
                response.setStatus(400);
                out.print(gson.toJson(new Result(400, "缺少用户ID", null)));
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print(gson.toJson(new Result(500, e.getMessage(), null)));
        }
    }

    // 统一响应结果类
    static class Result {
        int code;
        String message;
        Object data;

        Result(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}
```

### 12.6 Filter

```java
// EncodingFilter.java
package com.example.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}

// AuthFilter.java
package com.example.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter(urlPatterns = "/api/*")
public class AuthFilter implements Filter {

    private static final Set<String> EXCLUDE_PATHS = new HashSet<>(
        Arrays.asList("/api/users")  // POST请求用于注册，不需要登录
    );

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI()
                            .substring(request.getContextPath().length());
        String method = request.getMethod();

        // POST /api/users 是注册接口，不需要登录
        if ("POST".equals(method) && EXCLUDE_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否登录
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"code\":401,\"message\":\"请先登录\"}");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
```

### 12.7 Listener

```java
// AppListener.java
package com.example.listener;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;

@WebListener
public class AppListener implements ServletContextListener,
                                   HttpSessionListener {

    private static int onlineCount = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================================");
        System.out.println("  用户管理系统启动");
        System.out.println("  启动时间: " + new java.util.Date());
        System.out.println("========================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========================================");
        System.out.println("  用户管理系统关闭");
        System.out.println("  关闭时间: " + new java.util.Date());
        System.out.println("========================================");
    }

    @Override
    public synchronized void sessionCreated(HttpSessionEvent se) {
        onlineCount++;
        se.getSession().getServletContext()
          .setAttribute("onlineCount", onlineCount);
        System.out.println("新会话创建，当前在线: " + onlineCount);
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent se) {
        if (onlineCount > 0) {
            onlineCount--;
        }
        se.getSession().getServletContext()
          .setAttribute("onlineCount", onlineCount);
        System.out.println("会话销毁，当前在线: " + onlineCount);
    }
}
```

### 12.8 login.html

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>用户登录</title>
    <style>
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }
      body {
        font-family: Arial, sans-serif;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      .login-container {
        background: white;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
        width: 350px;
      }
      h2 {
        text-align: center;
        margin-bottom: 30px;
        color: #333;
      }
      .form-group {
        margin-bottom: 20px;
      }
      label {
        display: block;
        margin-bottom: 5px;
        color: #666;
      }
      input[type="text"],
      input[type="password"] {
        width: 100%;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 14px;
      }
      input:focus {
        border-color: #667eea;
        outline: none;
      }
      button {
        width: 100%;
        padding: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
      }
      button:hover {
        opacity: 0.9;
      }
      .message {
        text-align: center;
        margin-top: 15px;
        color: red;
      }
      .success {
        color: green;
      }
    </style>
  </head>
  <body>
    <div class="login-container">
      <h2>用户登录</h2>
      <form id="loginForm">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" name="username" required />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" name="password" required />
        </div>
        <button type="submit">登录</button>
      </form>
      <div id="message" class="message"></div>
    </div>

    <script>
      document
        .getElementById("loginForm")
        .addEventListener("submit", function (e) {
          e.preventDefault();

          const username = document.getElementById("username").value;
          const password = document.getElementById("password").value;

          fetch("login", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `username=${encodeURIComponent(
              username
            )}&password=${encodeURIComponent(password)}`,
          })
            .then((response) => response.json())
            .then((data) => {
              const messageDiv = document.getElementById("message");
              if (data.code === 200) {
                messageDiv.textContent = "登录成功！正在跳转...";
                messageDiv.className = "message success";
                setTimeout(() => {
                  window.location.href = "index.html";
                }, 1000);
              } else {
                messageDiv.textContent = data.message;
                messageDiv.className = "message";
              }
            })
            .catch((error) => {
              document.getElementById("message").textContent =
                "请求失败：" + error;
            });
        });
    </script>
  </body>
</html>
```

---

## 13. 常见面试题

### 13.1 基础概念

**Q1: Servlet 是什么？它的作用是什么？**

```
A: Servlet是运行在Web服务器上的Java程序，主要用于：
   1. 接收和解析客户端的HTTP请求
   2. 处理业务逻辑
   3. 生成动态Web内容并返回给客户端
```

**Q2: Servlet 的生命周期是什么？**

```
A: Servlet的生命周期包含以下阶段：
   1. 加载和实例化：容器加载Servlet类并创建实例（只执行一次）
   2. 初始化：调用init()方法（只执行一次）
   3. 服务：每次请求调用service()方法（执行多次）
   4. 销毁：容器关闭时调用destroy()方法（只执行一次）
```

**Q3: Servlet 是单例的吗？线程安全吗？**

```
A:
   - Servlet默认是单例的，整个应用只有一个实例
   - Servlet不是线程安全的，多个请求会使用多个线程同时访问同一个Servlet实例
   - 解决方案：
     1. 避免使用实例变量
     2. 使用synchronized同步
     3. 使用ThreadLocal
     4. 将Servlet配置为SingleThreadModel（已废弃）
```

### 13.2 请求和响应

**Q4: 请求转发和重定向的区别？**

```
┌────────────────┬─────────────────────┬─────────────────────┐
│     特点        │    请求转发(forward) │   重定向(redirect)   │
├────────────────┼─────────────────────┼─────────────────────┤
│ 浏览器地址栏    │ 不变                 │ 变化                 │
│ 请求次数        │ 1次                  │ 2次                  │
│ 数据共享        │ 可以共享request数据   │ 不能共享             │
│ 跳转范围        │ 只能本应用内         │ 可以跨应用           │
│ 执行位置        │ 服务器端             │ 客户端               │
│ WEB-INF访问    │ 可以访问             │ 不能访问             │
└────────────────┴─────────────────────┴─────────────────────┘
```

**Q5: GET 和 POST 请求的区别？**

```
┌────────────────┬─────────────────────┬─────────────────────┐
│     特点        │       GET请求        │      POST请求        │
├────────────────┼─────────────────────┼─────────────────────┤
│ 参数位置        │ URL中                │ 请求体中             │
│ 参数长度        │ 有限制(约2KB)        │ 无限制               │
│ 安全性          │ 低(参数可见)         │ 较高                 │
│ 幂等性          │ 幂等                 │ 非幂等               │
│ 缓存            │ 可缓存               │ 不缓存               │
│ 书签            │ 可收藏               │ 不可收藏             │
└────────────────┴─────────────────────┴─────────────────────┘
```

### 13.3 会话管理

**Q6: Session 和 Cookie 的区别？**

```
┌────────────────┬─────────────────────┬─────────────────────┐
│     特点        │      Cookie         │      Session        │
├────────────────┼─────────────────────┼─────────────────────┤
│ 存储位置        │ 客户端(浏览器)       │ 服务器端             │
│ 安全性          │ 较低                 │ 较高                 │
│ 存储大小        │ 4KB左右              │ 无限制               │
│ 存储类型        │ 字符串               │ 任意对象             │
│ 生命周期        │ 可设置过期时间        │ 默认30分钟           │
│ 服务器压力      │ 无                   │ 有                   │
└────────────────┴─────────────────────┴─────────────────────┘
```

**Q7: 如何实现 Session 共享？**

```
A: 分布式环境下Session共享的方案：
   1. Session粘滞：将同一用户的请求始终路由到同一服务器
   2. Session复制：在集群节点间同步Session数据
   3. Session集中存储：使用Redis、Memcached等存储Session
   4. Token方案：使用JWT等无状态token替代Session
```

### 13.4 Filter 和 Listener

**Q8: Filter 的作用是什么？常见应用场景？**

```
A: Filter用于在请求到达Servlet之前或响应返回客户端之前进行预处理/后处理。

常见应用场景：
   1. 字符编码设置
   2. 登录验证/权限控制
   3. 日志记录
   4. 敏感词过滤
   5. 数据压缩
   6. CORS跨域处理
```

**Q9: Listener 有哪些类型？分别有什么作用？**

```
A: Servlet中有8种监听器：

1. ServletContext监听器
   - ServletContextListener：监听应用启动/关闭
   - ServletContextAttributeListener：监听Context属性变化

2. HttpSession监听器
   - HttpSessionListener：监听Session创建/销毁
   - HttpSessionAttributeListener：监听Session属性变化
   - HttpSessionBindingListener：监听对象绑定/解绑Session
   - HttpSessionActivationListener：监听Session钝化/活化

3. ServletRequest监听器
   - ServletRequestListener：监听请求创建/销毁
   - ServletRequestAttributeListener：监听Request属性变化
```

---

## 总结

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Servlet知识体系总结                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  核心组件                                                            │
│  ├── Servlet：处理请求，生成响应                                     │
│  ├── Filter：请求/响应的预处理和后处理                                │
│  └── Listener：监听生命周期事件                                      │
│                                                                     │
│  核心对象                                                            │
│  ├── ServletConfig：Servlet配置信息                                  │
│  ├── ServletContext：应用上下文（全局共享）                           │
│  ├── HttpServletRequest：HTTP请求对象                                │
│  ├── HttpServletResponse：HTTP响应对象                               │
│  └── HttpSession：会话对象                                           │
│                                                                     │
│  配置方式                                                            │
│  ├── web.xml（传统方式）                                             │
│  └── 注解（Servlet 3.0+）                                            │
│                                                                     │
│  关键知识点                                                          │
│  ├── 生命周期：init → service → destroy                             │
│  ├── 线程模型：单例多线程                                            │
│  ├── 请求转发 vs 重定向                                              │
│  ├── Cookie vs Session                                              │
│  └── Filter链的执行顺序                                              │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

> 📌 **提示**：Servlet 是 Java Web 开发的基础，虽然现代框架（如 Spring MVC）封装了大部分 Servlet 操作，但理解 Servlet 原理对于深入理解 Web 开发非常重要。
