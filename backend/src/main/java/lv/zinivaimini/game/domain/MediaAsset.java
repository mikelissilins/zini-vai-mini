package lv.zinivaimini.game.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_assets")
public class MediaAsset {
    @Id
    private UUID id;
    private String fileName;
    private String contentType;
    private long byteSize;
    private byte[] data;
    private Instant createdAt;

    protected MediaAsset() {
    }

    public MediaAsset(String fileName, String contentType, byte[] data) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.contentType = contentType;
        this.byteSize = data.length;
        this.data = data;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public long getByteSize() { return byteSize; }
    public byte[] getData() { return data; }
}
