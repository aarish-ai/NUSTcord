package com.nustcord.model;

public class Role {
    private int id;
    private int serverId;
    private String name;
    private String permissions; // comma-separated list

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServerId() { return serverId; }
    public void setServerId(int serverId) { this.serverId = serverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
}
