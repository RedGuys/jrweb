package ru.redguy.jrweb;

/**
 * Web server options.
 * @author RedGuy
 */
public class WebServerOptions {
    private int socketBacklog = 50;

    public int getSocketBacklog() {
        return socketBacklog;
    }

    /**
     * Sets socket backlog.
     * @param socketBacklog requested maximum length of the queue of incoming connections.
     * @return self.
     */
    public WebServerOptions socketBacklog(int socketBacklog) {
        this.socketBacklog = socketBacklog;
        return this;
    }
}
