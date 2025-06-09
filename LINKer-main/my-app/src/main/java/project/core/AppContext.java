package project.core;


import project.Network.ServerConn;

public class AppContext {
    private static ServerConn serverConn;
    private static String currentUserId;

    public static void setServerConn(ServerConn conn) {
        serverConn = conn;
    }

    public static ServerConn getServerConn() {
        return serverConn;
    }

    public static String getCurrentUserID() {
        return currentUserId;
    }
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }
}
