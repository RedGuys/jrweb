import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.annotations.Page;
import ru.redguy.jrweb.annotations.Router;
import ru.redguy.jrweb.Context;

@Router("/class")
public class RouterClass {

    @Page("/call")
    public void call(@NotNull Context context) {
        context.response.send("Hello from RouterClass");
    }

    @Page(value = "/call2", method = "POST")
    public void call2(@NotNull Context context) {
        context.response.send(context.request.params.toString());
    }
}
