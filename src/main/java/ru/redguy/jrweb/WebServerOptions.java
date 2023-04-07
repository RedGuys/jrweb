package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Brotli;
import ru.redguy.jrweb.utils.Compressor;
import ru.redguy.jrweb.utils.Deflate;
import ru.redguy.jrweb.utils.Gzip;

/**
 * Web server options.
 * @author RedGuy
 */
public class WebServerOptions {
    private int socketBacklog = 50;
    private boolean enableChunkedTransfer = false;
    private Compressor compressor = null;
    private boolean enableSessionStorage = false;
    private long sessionTTL = -1;

    public int getSocketBacklog() {
        return socketBacklog;
    }

    public boolean isEnableChunkedTransfer() {
        return enableChunkedTransfer;
    }

    public Compressor getCompressor() {
        return compressor;
    }

    public boolean isEnableSessionStorage() {
        return enableSessionStorage;
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

    /**
     * Enables deflate compression.
     * @return self.
     */
    public WebServerOptions enableDeflateCompression() {
        this.compressor = new Deflate();
        return this;
    }

    /**
     * Enables brotli compression.
     * @return self.
     */
    public WebServerOptions enableBrotliCompression() {
        this.compressor = new Brotli();
        return this;
    }

    /**
     * Enables session storage.
     * @return self.
     */
    public WebServerOptions enableSessionStorage() {
        this.enableSessionStorage = true;
        return this;
    }

    /**
     * Enables session storage.
     * @param sessionTTL session time to live in seconds.
     * @return self.
     */
    public WebServerOptions enableSessionStorage(long sessionTTL) {
        this.enableSessionStorage = true;
        this.sessionTTL = sessionTTL;
        return this;
    }
}
