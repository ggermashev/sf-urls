package Models;

import Exceptions.LinkLifetimeExpiredException;
import Exceptions.LinkUsageLimitExceededException;
import utils.FileManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class UrlModel extends Model {
    private UUID userId;
    private URI originalUrl;
    private String shortUrl;
    private Long createdAt;
    private Long lifetime;
    private Integer usages;
    private Integer usagesLimit;

    public UrlModel(UUID userId, String url, long lifetime, int usagesLimit) throws URISyntaxException {
        super();
        title = "Url";

        originalUrl = new URI(url);
        shortUrl = generateShortUrl();
        createdAt = Timestamp.from(Instant.now()).getTime();
        this.lifetime = lifetime;
        this.usages = 0;
        this.usagesLimit = usagesLimit;
        this.userId = userId;
    }

    public UrlModel(UUID userId, String url, long lifetime, int usagesLimit, String shortUrl, long createdAt, int usages) throws URISyntaxException {
        this.originalUrl = new URI(url);
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.lifetime = lifetime;
        this.usages = usages;
        this.usagesLimit = usagesLimit;
        this.userId = userId;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void navigate() throws IOException, LinkLifetimeExpiredException, LinkUsageLimitExceededException {
        long now = Timestamp.from(Instant.now()).getTime();
        if (now - createdAt > lifetime) {
            throw new LinkLifetimeExpiredException();
        }
        if (usages >= usagesLimit) {
            throw new LinkUsageLimitExceededException();
        }

        Desktop.getDesktop().browse(originalUrl);
        usages++;
    }

    @Override
    public String toString() {
        return "<Model/UrlModel>" + "id=" + this.getId() + ";userId=" + this.userId.toString() + ";originalUrl=" + this.originalUrl.toString() + ";shortUrl=" + this.shortUrl + ";createdAt=" + FileManager.recursiveStringify(this.createdAt)
                + ";lifetime=" + FileManager.recursiveStringify(this.lifetime) + ";usages=" + FileManager.recursiveStringify(this.usages) + ";usagesLimit=" + FileManager.recursiveStringify(this.usagesLimit) + "</Model/UrlModel>";
    }

    private String generateShortUrl() {
        String base = "clck.ru/";

        String possibleCharacters = "abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder hash = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int ind = (int) Math.round(Math.random() * (possibleCharacters.length() - 1));
            hash.append(possibleCharacters.charAt(ind));
        }

        return base + hash;
    }
}