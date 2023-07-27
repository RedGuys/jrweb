package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.StatusCodes;

public class ErrorHandlers {
    /**
     * Called when 404 error. Default: send "Not found" message.
     * @param context Context of request.
     */
    public void on404(@NotNull Context context) {
        context.response.setStatusCode(StatusCodes.NOT_FOUND);
        context.response.send("Not found");
    }

    /**
     * Called when 500 error. Default: send "Internal Server Error" message. If showExceptions is true, print stack trace.
     * @param context Context of request.
     * @param e Exception.
     */
    public void on500(@NotNull Context context, Exception e) {
        if (!context.response.isHeadersSent()) {
            context.response.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            context.response.send("Internal Server Error");
            if(context.server.getOptions().isShowExceptions()) {
                //print like printStackTrace
                context.response.send("\r\n");
                context.response.send(e.toString());
                StackTraceElement[] trace = e.getStackTrace();
                for (StackTraceElement traceElement : trace)
                    context.response.send("\n\tat " + traceElement+"\r\r");
            }
        }
        e.printStackTrace();
        context.processed = true;
    }
}
