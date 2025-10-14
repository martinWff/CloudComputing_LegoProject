package cc.srv;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;

public class Comment {
    private String author;

    private String content;

    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss a")
    private Timestamp timestamp;

    public Comment() {

    }
    public Comment(String author,String content,Timestamp ts) {
        this.author = author;
        this.content = content;
        this.timestamp = ts;
    }

    public Comment(String author,String content) {
        this.author = author;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String a) {
        this.author = a;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String c) {
        this.content = c;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp t) {
        this.timestamp = t;
    }
}
