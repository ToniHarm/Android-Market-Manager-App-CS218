package com.example.cs218marketmanager.data.model;

public class Notification {
    private long id;
    private String message;
    private String timestamp;
    private boolean isRead;

    public Notification () {}
    // Constructor
    public Notification(long id, String message, String timestamp, boolean isRead) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getter and Setter methods
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}

