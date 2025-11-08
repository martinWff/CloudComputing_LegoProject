package com.example.legoproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class CommentData {
    private String id;

    private String productId;

    private String author;

    private String content;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp;

    public CommentData() {

    }

    public CommentData(String id,String productId,String author,String content) {
        this.id = id;
        this.productId = productId;
        this.author = author;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public CommentData(String id,String productId,String author,String content,Instant timestamp) {
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
