import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.annotations.Page;
import ru.redguy.jrweb.annotations.Param;
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

    @Page(value = "/call3")
    public void call3(@NotNull Context context, @Param(value = "param1", required = false) Structure2 param1) {
        context.response.send("Hello from RouterClass call3 with param1: " + param1.param1);
    }

    @Page(value = "/call4", method = "POST")
    public void call4(@NotNull Context context, @Param(value = "param1", required = false) Structure1 param1) {
        context.response.send("Hello from RouterClass call4 with param1: " + param1.param1);
    }

    public static class Structure1 {
        public String param1;
        public Structure1(String param1) {
            this.param1 = param1;
        }
    }

    public static class Structure2 {
        public String param1;
        public static Structure2 fromString(String str) {
            Structure2 structure2 = new Structure2();
            structure2.param1 = str;
            return structure2;
        }
    }

}
