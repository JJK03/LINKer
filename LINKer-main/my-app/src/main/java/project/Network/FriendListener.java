package project.Network;
// FriendListener.java

public interface FriendListener {
    void onFriendListReceived(String[] friends);
    void onFriendRequest(String fromUserId);
    void onFriendAddResult(boolean success, String message);
}
