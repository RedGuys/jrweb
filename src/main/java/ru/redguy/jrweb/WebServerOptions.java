package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Brotli;
import ru.redguy.jrweb.utils.Compressor;
import ru.redguy.jrweb.utils.Deflate;
import ru.redguy.jrweb.utils.Gzip;

/**
 * Web server options.
 *
 * @author RedGuy
 */
public class WebServerOptions {
    private int socketBacklog = 50;
    private boolean enableChunkedTransfer = false;
    private Compressor compressor = null;
    private boolean enableSessionStorage = false;
    private long sessionTTL = -1;
    private long sessionCheckInterval = 60;
    private boolean removeExpiredSessionsOnAccess = false;
    private boolean showExceptions = false;

    /**
     * @return socket backlog.
     */
    public int getSocketBacklog() {
        return socketBacklog;
    }

    /**
     * @return true if chunked transfer is enabled.
     */
    public boolean isEnableChunkedTransfer() {
        return enableChunkedTransfer;
    }

    /**
     * @return returns current compressor or null if compression is disabled.
     */
    public Compressor getCompressor() {
        return compressor;
    }

    /**
     * @return true if session storage is enabled.
     */
    public boolean isEnableSessionStorage() {
        return enableSessionStorage;
    }

    /**
     * @return session time to live in seconds.
     */
    public long getSessionTTL() {
        return sessionTTL;
    }

    /**
     * @return session check interval in seconds.
     */
    public long getSessionCheckInterval() {
        return sessionCheckInterval;
    }

    /**
     * @return true if remove expired sessions on access is enabled.
     */
    public boolean isRemoveExpiredSessionsOnAccess() {
        return removeExpiredSessionsOnAccess;
    }

    /**
     * @return true if show exceptions on web page is enabled.
     */
    public boolean isShowExceptions() {
        return showExceptions;
    }

    /**
     * Sets socket backlog.
     *
     * @param socketBacklog requested maximum length of the queue of incoming connections.
     * @return self.
     */
    public WebServerOptions socketBacklog(int socketBacklog) {
        this.socketBacklog = socketBacklog;
        return this;
    }

    /**
     * Enables chunked transfer.
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Transfer-Encoding#chunked_encoding">Chunked transfer encoding</a>
     * @return self.
     */
    public WebServerOptions enableChunkedTransfer() {
        this.enableChunkedTransfer = true;
        return this;
    }

    /**
     * Enables gzip compression.
     *
     * @return self.
     */
    public WebServerOptions enableGzipCompression() {
        this.compressor = new Gzip();
        return this;
    }

    /**
     * Enables deflate compression.
     *
     * @return self.
     */
    public WebServerOptions enableDeflateCompression() {
        this.compressor = new Deflate();
        return this;
    }

    /**
     * Enables brotli compression.
     *
     * @return self.
     *
     * @implNote Works only if brotliSupport feature is enabled.
     */
    public WebServerOptions enableBrotliCompression() {
        this.compressor = new Brotli();
        return this;
    }

    /**
     * Enables session storage.
     *
     * @return self.
     *
     * @implNote Automatically creates jrsession cookie.
     */
    public WebServerOptions enableSessionStorage() {
        this.enableSessionStorage = true;
        return this;
    }

    /**
     * Enables session storage.
     *
     * @param sessionTTL session time to live in seconds.
     * @return self.
     *
     * @implNote Automatically creates jrsession cookie.
     */
    public WebServerOptions enableSessionStorage(long sessionTTL) {
        this.enableSessionStorage = true;
        this.sessionTTL = sessionTTL;
        return this;
    }

    /**
     * Enables session storage.
     *
     * @param sessionTTL           session time to live in seconds.
     * @param sessionCheckInterval session check interval in seconds.
     * @return self.
     *
     * @implNote Works only if brotliSupport feature is enabled.
     */
    public WebServerOptions enableSessionStorage(long sessionTTL, long sessionCheckInterval) {
        this.enableSessionStorage = true;
        this.sessionTTL = sessionTTL;
        this.sessionCheckInterval = sessionCheckInterval;
        return this;
    }

    /**
     * Toggles check for expired sessions on access.
     *
     * @return self.
     */
    public WebServerOptions removeExpiredSessionsOnAccess() {
        this.removeExpiredSessionsOnAccess = !this.removeExpiredSessionsOnAccess;
        return this;
    }

    /**
     * Toggles showing exceptions.
     *
     * @return self.
     */
    public WebServerOptions showExceptions() {
        this.showExceptions = !this.showExceptions;
        return this;
    }
}
