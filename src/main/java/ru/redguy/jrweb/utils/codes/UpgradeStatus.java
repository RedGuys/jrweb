package ru.redguy.jrweb.utils.codes;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.StatusCode;

/**
 * Status codes with upgrade header. 101, 426.
 * @author RedGuy
 */
public class UpgradeStatus extends StatusCode {
    public UpgradeStatus(int status, String message, String upgradeTo, String connection) {
        super(status, message);
        getHeaders().add(Headers.Common.CONNECTION.instance(connection));
        getHeaders().add(Headers.Common.UPGRADE.instance(upgradeTo));
    }
}
