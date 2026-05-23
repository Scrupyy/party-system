package de.scrupy.party.core.config;

public class RedisConfig implements Config {

    public static final String CONFIG_NAME = "redis.json";
    private final String address;
    private final String username;
    private final String password;
    private final int port;

    public RedisConfig() {
        this.address = "127.0.0.1";
        this.username = "username";
        this.password = "password";
        this.port = 6379;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
