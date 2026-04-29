package com.nustcord.model;

import java.sql.Timestamp;

public class Channel {
    private int id;
    private int serverId;
    private String name;
    private ChannelType type;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServerId() { return serverId; }
    public void setServerId(int serverId) { this.serverId = serverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ChannelType getType() { return type; }
    public void setType(ChannelType type) { this.type = type; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
