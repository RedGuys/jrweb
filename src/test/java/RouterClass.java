import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.annotations.Page;
import ru.redguy.jrweb.annotations.Router;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.bodyparsers.BytesBody;
import ru.redguy.jrweb.utils.bodyparsers.URLEncodedBody;

@Router("/class")
public class RouterClass {

    @Page("/call")
    public void call(@NotNull Context context) {
        context.response.send("Hello from RouterClass");
    }

    @Page(value = "/call2", method = "POST")
    public void call2(@NotNull Context context) {
        if(context.request.body instanceof BytesBody) {
            BytesBody body = (BytesBody) context.request.body;
            context.response.send(body.bytes);
        } else if(context.request.body instanceof URLEncodedBody) {
            URLEncodedBody body = (URLEncodedBody) context.request.body;
            context.response.send(body.parameters.keySet().toString());
        }
    }
}
