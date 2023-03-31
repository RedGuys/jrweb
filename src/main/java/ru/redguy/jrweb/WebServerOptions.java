package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Compressor;
import ru.redguy.jrweb.utils.Gzip;

/**
 * Web server options.
 * @author RedGuy
 */
public class WebServerOptions {
    private int socketBacklog = 50;
    private boolean enableChunkedTransfer = false;
    private Compressor compressor = null;

    public int getSocketBacklog() {
        return socketBacklog;
    }

    public boolean isEnableChunkedTransfer() {
        return enableChunkedTransfer;
    }

    public Compressor getCompressor() {
        return compressor;
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

    /**
     * Enables gzip compression.
     * @return self.
     */
    public WebServerOptions enableGzipCompression() {
        this.compressor = new Gzip();
        return this;
    }
}
