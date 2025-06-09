package project.Network;

// MessageListener.java
public interface MessageListener {
    void onMessageReceived(String from, String content);
    void onMessageSendError(String reason);
    void onImageReceived(String from, byte[] image);
    void onImageSendError(String reason);
    void onEmojiReceived(String from, int emojiId);

    void onRECOVERMessageS_Received(String content,String time);
    void onRECOVERMessageR_Received(String content,String time);

    void onRECOVERImgS_Received(byte[] image,String time);
    void onRECOVERImgR_Received( byte[] image,String time);

    void onRECOVEREmojiS_Received(int emojiId,String time);
    void onRECOVEREmojiR_Received(int emojiId,String time);



}
