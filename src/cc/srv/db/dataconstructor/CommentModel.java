package cc.srv.db.dataconstructor;

import cc.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class CommentModel {

    private String id;

    private String productId;

    private String author;

    private String content;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp;

    public CommentModel() {

    }

    public CommentModel(String id,String productId,String author,String content) {
        this.id = id;
        this.productId = productId;
        this.author = author;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public CommentModel(String id,String productId,String author,String content,Instant timestamp) {
        this.id = id;
        this.productId = productId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
