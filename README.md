# JRWeb

Java webserver implementation without any external dependencies (only jetbrains annotations).

## Features

- HTTP/2
- GET, POST, HEAD, PUT, DELETE, OPTIONS, TRACE, CONNECT
- Middlewares
- Routes
- Static files serving
- Reflection routers parsing (define page with using method annotation)
- Cookies

## Planned features

- Websockets
- Servlets
- JSP
- JSTL
- Chunked transfer encoding
- Keep-alive
- GZIP compression
- Basic authentication
- SSL/TLS
- Cache control
- Cache
- Sessions
- Logging
- Error pages
- Custom error pages
- Custom error codes
- Custom error messages
- Custom error handlers

## Usage

### Init server

```java
class Main {
    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start(80);

        server.addPage(new Page("/", (ctx) -> {
            ctx.response.setStatusCode(StatusCodes.OK);
            ctx.response.send("<html><body>Hello World!</body></html>");
        }));
    }
}
```

### Redirect

```java
server.addPage(new Page("/redirect",(ctx) -> {
    ctx.response.setStatusCode(StatusCodes.MOVED_PERMANENTLY("https://google.com"));
}));
```

### Post request

```java
server.addPage(new Page(Methods.POST,"/post-only",(ctx) -> {
    ctx.response.setStatusCode(StatusCodes.OK);
    ctx.response.send("<html><body>POST ONLY</body></html>");
}));
```

### Router with page

```java
Router router = server.addRouter(new Router("/route"));
router.add(new Page("/test",(ctx) -> {
    ctx.response.setStatusCode(StatusCodes.OK);
    ctx.response.send("<html><body>Route test</body></html>");
}));
```

### File sending

```java
server.addPage(new Page(Methods.GET, "/file", (ctx) -> {
    ctx.response.setStatusCode(StatusCodes.OK);
    InputStream is = SimpleWebServer.class.getResourceAsStream("rick.webp");
    byte[] bytes = readAllBytes(is);
    ctx.response.getHeaders().add(Headers.Response.CONTENT_LENGTH, String.valueOf(bytes.length));
    ctx.response.send(bytes);
}));
```

### Static files serving

```java
server.addRouter(new FileRouter("/src", Paths.get("src")));
```

### Reflection router

```java
server.addRouter(new RouterClass());
```

```java
@Router("/class")
public class RouterClass {

    @Page("/call")
    public void call(@NotNull Context context) {
        context.response.send("Hello from RouterClass");
    }
}
```

### Resources serving

```java
server.addRouter(new ResourcesRouter("/resources", "/"));
```

### Reading request headers
    
```java
server.addPage(new Page("/headers",(ctx) -> {
    ctx.response.send("<html><body>Headers:<br>");
    ctx.request.headers.forEach((e) -> {
        ctx.response.send(e.generate() + ": " + e.getValue() + "<br>");
    });
    ctx.response.send("</body></html>");
}));
```

## License
MIT

## Contributors
RedGuys