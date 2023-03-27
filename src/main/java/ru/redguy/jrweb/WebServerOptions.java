package ru.redguy.jrweb;

/**
 * Web server options.
 * @author RedGuy
 */
public class WebServerOptions {
    private int socketBacklog = 50;
    private boolean enableChunkedTransfer = false;

    public int getSocketBacklog() {
        return socketBacklog;
    }

    public boolean isEnableChunkedTransfer() {
        return enableChunkedTransfer;
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

    /**
     * Enables chunked transfer.
     * @return self.
     */
    public WebServerOptions enableChunkedTransfer() {
        this.enableChunkedTransfer = true;
        return this;
    }
}
