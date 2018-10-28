package org.eclipse.smarthome.io.rest.sse.sitemap.rendering;

import java.util.UUID;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Used for fingerprinting clients.
 *
 * TODO Preliminary code, it may need to be moved elsewhere as mentioned on {@link SubscriptionResource}
 *
 * @author Flavio Costa
 *
 */
public class ClientIdentity {

    public static final String UNKNOWN_USER_AGENT = "";

    private final UUID uuid;

    private final String userAgent;

    public ClientIdentity(@NonNull UUID uuid, String userAgent) {
        this.uuid = uuid;
        this.userAgent = userAgent != null ? userAgent : UNKNOWN_USER_AGENT;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String toString() {
        return userAgent + '@' + uuid;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClientIdentity) {
            ClientIdentity other = (ClientIdentity) obj;
            return this.uuid.equals(other.uuid) && this.userAgent.equals(other.userAgent);
        }
        return false;
    }

}
