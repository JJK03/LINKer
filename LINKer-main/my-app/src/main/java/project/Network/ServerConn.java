package project.Network;


import project.core.AppContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;

public class ServerConn {
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private final String charset = "UTF-8";

    // 리스너들
    private LoginListener loginListener;
    private MessageListener messageListener;
    private FriendListener friendListener;
    private ChatListener chatListener;


    public ServerConn(String host, int port, String token, String version) throws IOException {
        this.socket = new Socket(host, port);
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();


        System.out.println("Connected to " + host + ":" + port);
        System.out.println("Version: " + version);
        System.out.println("Token: " + token);

        send(token);     // 인증 토큰 전송
        send(version);   // 버전 전송

        Thread receiver = new Thread(this::receiveLoop);
        receiver.setDaemon(true);
        receiver.start();
    }

    // 수신 루프
    private void receiveLoop() {
        try {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead, charset).trim();
                handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("📴 서버 연결 끊김: " + e.getMessage());
        }
    }


    // 메시지 분기 처리 == 메세지를 각 리스너로 전송후 리스너는 GUI로 전송전달.
    private void handleMessage(String line) throws IOException {
        line = line.trim();
        System.err.println(line);

        if (line.startsWith("/#/info ")) {
            String info = line.substring(8);
            if (loginListener != null) loginListener.onLoginSuccess(info);
        }
        else if (line.equals("-1")) {
            if(loginListener != null){
                loginListener.onLoginFailure("로그인 실패");
            }
        }

        else if (line.startsWith("/#/signed_up ")) {
            String userId = line.substring(14);
            if (loginListener != null) loginListener.onSignUpSuccess(userId);
        }

        else if (line.startsWith("/#/error ")) {
            String error = line.substring(9);
            if (loginListener != null) {
                if (error.contains("로그인")) loginListener.onLoginFailure(error);
                else if (error.contains("이미 존재하는 ")) loginListener.onSignUpFailure(error);
            }
            if (messageListener != null && error.contains("메시지")) {
                messageListener.onMessageSendError(error);
            }
        }
        else if (line.startsWith("/#/fl ")) {
            if (friendListener != null) {
                String[] friends = line.substring(6).split(" ");
                friendListener.onFriendListReceived(friends);
            }
        }
        else if (line.startsWith("/#/af ")) {
            if (friendListener != null) {
                String result = line.substring(6);
                boolean success = !result.startsWith("/#/error ");
                friendListener.onFriendAddResult(success, result);
            }
        }

        else if (line.startsWith("/#/recvmsg ")) {
            String[] from = line.substring(11).split(" ",2);
            if (from.length == 2 && messageListener != null) {
                String fromUser = from[0];
                String message = from[1];
                messageListener.onMessageReceived(fromUser, message);
            }
        }

        else if (line.startsWith("/#/recvimgmsg ")) {
            StringBuilder imgBuilder = new StringBuilder();

            String[] from = line.substring(15).split(" ", 2);
            if (from.length == 2) {
                String fromUser = from[0];
                String part = from[1];

                imgBuilder.append(part);

                // 만약 이 라인에 /img_EOF 가 이미 포함돼 있으면 끝
                while (!imgBuilder.toString().endsWith("/img_EOF")) {
                    byte[] buffer = new byte[8192];
                    int bytesRead = input.read(buffer);
                    if (bytesRead == -1) break;

                    String nextChunk = new String(buffer, 0, bytesRead, charset).trim();
                    imgBuilder.append(nextChunk);
                }

                String encoded = imgBuilder.toString();
                if (encoded.endsWith("/img_EOF")) {
                    encoded = encoded.substring(0, encoded.length() - "/img_EOF".length()).trim();
                }

                try {
                    byte[] image = Base64.getDecoder().decode(encoded);
                    if (messageListener != null) {
                        messageListener.onImageReceived(fromUser, image);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ 이미지 디코딩 실패: " + e.getMessage());
                }
            }
        }
        else if(line.startsWith("/#/chathistory ")) {
            if(chatListener != null){
                String[] parts = line.substring(15).split(" ");
                chatListener.onChatListReceived(parts);
            }
        }
        else if(line.startsWith("/#/recvemoji ")) {
            String[] parts = line.substring(13).split(" ",2);
            if(parts.length == 2 && messageListener != null){
                String fromUser = parts[0];
                int emojiId = Integer.parseInt(parts[1]);
                messageListener.onEmojiReceived(fromUser,emojiId);
            }
        }
        else if(line.startsWith("/#/sendMsgRECOVER||")) {
            String[] parts = line.split("\\|\\|");
            if(parts.length == 3 && messageListener != null){
                String contents = parts[1];
                String time = parts[2];
                messageListener.onRECOVERMessageS_Received(contents, time);
            }
        }
        else if(line.startsWith("/#/receiveMsgRECOVER||")) {
            String[] parts = line.split("\\|\\|");
            if(parts.length == 3 && messageListener != null){
                String contents = parts[1];
                String time = parts[2];
                System.err.println("내용 디버거 :" + contents);
                messageListener.onRECOVERMessageR_Received(contents, time);
            }

        }
        else if(line.startsWith("/#/sendImgRECOVER||")) {
            StringBuilder imgBuilder = new StringBuilder();

            String[] from = line.split("\\|\\|");
            if (from.length == 3) {
                String time = from[2];
                String part = from[1];

                imgBuilder.append(part);

                // 만약 이 라인에 /img_EOF 가 이미 포함돼 있으면 끝
                while (!imgBuilder.toString().endsWith("/img_EOF")) {
                    byte[] buffer = new byte[8192];
                    int bytesRead = input.read(buffer);
                    if (bytesRead == -1) break;

                    String nextChunk = new String(buffer, 0, bytesRead, charset).trim();
                    imgBuilder.append(nextChunk);
                }

                String encoded = imgBuilder.toString();
                if (encoded.endsWith("/img_EOF")) {
                    encoded = encoded.substring(0, encoded.length() - "/img_EOF".length()).trim();
                }

                try {
                    byte[] image = Base64.getDecoder().decode(encoded);
                    if (messageListener != null) {
                        messageListener.onRECOVERImgS_Received(image, time);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ 이미지 디코딩 실패: " + e.getMessage());
                }

            }

        }
        else if (line.startsWith("/#/receiveimgRECOVER||")) {
            StringBuilder imgBuilder = new StringBuilder();
            System.err.println("/#/receiveimgRECOVER|| 로 시작 통과");
            String[] from = line.split("\\|\\|",3);
            if (from.length == 3) {
                String time = from[2];
                String part = from[1];

                imgBuilder.append(part);

                // 만약 이 라인에 /img_EOF 가 이미 포함돼 있으면 끝
                while (!imgBuilder.toString().endsWith("/img_EOF")) {
                    byte[] buffer = new byte[8192];
                    int bytesRead = input.read(buffer);
                    if (bytesRead == -1) break;

                    String nextChunk = new String(buffer, 0, bytesRead, charset).trim();
                    imgBuilder.append(nextChunk);
                }

                String encoded = imgBuilder.toString();
                if (encoded.endsWith("/img_EOF")) {
                    encoded = encoded.substring(0, encoded.length() - "/img_EOF".length()).trim();
                    System.err.println("인코딩 완료");
                }

                try {
                    byte[] image = Base64.getDecoder().decode(encoded);
                    if (messageListener != null) {
                        messageListener.onRECOVERImgR_Received(image, time);

                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ 이미지 디코딩 실패: " + e.getMessage());
                }

            }

        }
        else if(line.startsWith("/#/sendEmojiRECOVER||")) {
            String[] parts = line.split("\\|\\|");

            if(parts.length == 3 && messageListener != null){
                int emojiId = Integer.parseInt(parts[1]);
                String time = parts[2];
                messageListener.onRECOVEREmojiS_Received(emojiId, time);
            }
        }
        else if(line.startsWith("/#/receiveEmojiRECOVER||")) {
            String[] parts = line.split("\\|\\|");
            if(parts.length == 3 && messageListener != null){
                int emojiId = Integer.parseInt(parts[1]);
                String time = parts[2];
                messageListener.onRECOVEREmojiR_Received(emojiId, time);
            }
        }

        // 필요한 기능 계속 이곳에서 확장 및 수정
    }


    // 메세지 전송 (줄바꿈 자동 처리)
    public void send(String msg) {
        try {
            output.write((msg + "\n").getBytes(charset));
            output.flush();
        } catch (IOException e) {
            System.err.println("❌ 전송 실패: " + e.getMessage());
        }
    }



    // 인증 관련
    public void login(String id, String pw) {
        System.err.println("서버로 로그인 요청 전송");
        send(":c:login");
        send(id);
        send(pw);
    }

    public void signUp(String id, String pw, String nick) {
        send(":c:sign_up");
        send(id);
        send(pw);
        send(nick);
    }

    public void sendImage(String targetId, byte[] ImageBytes) {
        String base64 = Base64.getEncoder().encodeToString(ImageBytes);

        send("/img " + targetId +" "+ base64+"/img_EOF");

//        System.err.println(base64);

    }


    public void addFriend(String targetId) {
        send("/af " + targetId);
    }
    // 기타 기능

    public void requestFriendList() {
        send("/fl");
    }

    public void sendMessage(String to, String msg) {
        String message = "/msg " + to + " " + msg;
        send(message);
        System.err.println(message); //디버깅코드
    }


    public void sendemoji(String to,String  emojiId) {
        send("/emoji " + to +" "+ emojiId);
    }
    public void sendChathistory(String Userid) {
        send("/chathis " + Userid);
    }
    public void CreateChat(String user1, String user2) {
        send("/CChat " + user1 + " " + user2);
    }
    public void sendChatRecords(String user1, String user2) {
        send("/ChatRecords " + user1 + " " + user2);
    }

    // 리스너 설정자
    public void setLoginListener(LoginListener l) { this.loginListener = l; }
    public void setMessageListener(MessageListener l) { this.messageListener = l; }
    public void setFriendListener(FriendListener l) { this.friendListener = l; }
    public void setChatListener(ChatListener l) { this.chatListener = l; }
}
