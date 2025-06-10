package project.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.security.*;
import java.sql.Timestamp;
import java.util.*;

class msgth extends Thread {
    private final String token = "ajk123#%k2!lsd!234!%^^f17!@#sdfs!@$3$*s1s56!@#";
    static Hashtable<String, Socket> usrNick = new Hashtable<>();
    public String myName;
    public static final Map<String, PrintWriter> clientWriters = new HashMap<>();
    private List<String> friend = new ArrayList<>();
    private PrintWriter pwriter;
    Socket socket;
    BufferedReader reader;
    OutputStream output;
    Connection conn = null;

    public msgth(Socket socket) {
        this.socket = socket;
        try {
            output = socket.getOutputStream();
            conn = DBManager.getConnection();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (Exception e) {
            System.out.println("쓰레드 생성 에러");
        }
    }

    private void send(String msg) throws Exception {
        output.write((msg).getBytes("UTF-8"));
        output.flush();
        Thread.sleep(60);
    }

    public void run() {
        try {
            String msg;
            pwriter = new PrintWriter(socket.getOutputStream(), true);
            if (!token.equals(reader.readLine())) {
                System.out.println("접근 권한 없음");
                socket.close();
                return;
            } else if (!Server.serverVersion.equals(reader.readLine())) {
                send("업데이트 필요! 버전: " + Server.serverVersion);
                socket.close();
                return;
            }

            send("버전: " + Server.serverVersion);
            firstAccess();
            clientWriters.put(myName, pwriter);

            while ((msg = reader.readLine()) != null) {
                System.err.println(msg);
                if (msg.startsWith("/msg")) {
                    sendPrivateMessage(msg);
                } else if (msg.equals("/exit")) {
                    clientWriters.remove(myName);
                    usrNick.remove(myName);
                    socket.close();
                    return;
                } else if (msg.startsWith("/fl")) {
                    try {
                        PreparedStatement stmt = conn.prepareStatement(
                                "SELECT u.username, u.nickname " +
                                        "FROM friends f " +
                                        "JOIN users u ON f.friend = u.username " +
                                        "WHERE f.owner = ?");
                        stmt.setString(1, myName);
                        ResultSet rs = stmt.executeQuery();

                        StringBuilder fl = new StringBuilder("/#/fl");
                        while (rs.next()) {
                            String username = rs.getString("username");
                            String nickname = rs.getString("nickname");
                            fl.append(" ").append(nickname).append("||").append(username);
                        }

                        rs.close();
                        stmt.close();

                        send(fl.toString());

                    } catch (SQLException e) {
                        e.printStackTrace();
                        try {
                            send("/#/error 친구 목록 조회 중 오류 발생");
                        } catch (Exception ignored) {
                        }
                    }

                } else if (msg.startsWith("/af")) {
                    String[] parts = msg.split(" ");
                    if (parts.length >= 2) {
                        String target = parts[1];

                        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
                        PreparedStatement stmt = conn.prepareStatement(checkSql);
                        stmt.setString(1, target);
                        ResultSet rs = stmt.executeQuery();
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            if (!friend.contains(target)) {
                                String insertSql = "INSERT INTO friends (owner, friend) VALUES (?, ?)";
                                PreparedStatement ins = conn.prepareStatement(insertSql);
                                ins.setString(1, myName);
                                ins.setString(2, target);
                                ins.executeUpdate();
                                friend.add(target);
                                send("/#/af " + target + "을 친구로 추가하였습니다.");
                            } else {
                                send("/#/error 이미 친구입니다.");
                            }
                        } else {
                            send("/#/error 사용자 존재하지 않음.");
                        }

                    }
                } else if (msg.startsWith("/img ") && msg.endsWith("/img_EOF")) {
                    sendPrivateImgMsg(msg);

                } else if (msg.startsWith("/chathis")) {
                    String username = msg.substring(9).trim();

                    try {
                        PreparedStatement stmt = conn.prepareStatement(
                                "SELECT user1, user2 FROM chats WHERE user1 = ? OR user2 = ?");
                        stmt.setString(1, username);
                        stmt.setString(2, username);

                        ResultSet rs = stmt.executeQuery();
                        Set<String> partners = new LinkedHashSet<>(); // 중복 제거 + 순서 유지

                        while (rs.next()) {
                            String user1 = rs.getString("user1");
                            String user2 = rs.getString("user2");

                            if (username.equals(user1)) {
                                partners.add(user2);
                            } else if (username.equals(user2)) {
                                partners.add(user1);
                            }
                        }

                        if (partners.isEmpty()) {
                            send("/#/chathistory"); // 아무 채팅 상대 없음
                        } else {
                            StringBuilder sb = new StringBuilder("/#/chathistory");
                            for (String partner : partners) {
                                sb.append(" ").append(partner);
                            }
                            send(sb.toString());
                        }

                        rs.close();
                        stmt.close();
                    } catch (SQLException e) {
                        send("/error 채팅 기록을 가져오는 중 오류가 발생했습니다.");
                        e.printStackTrace();
                    }
                } else if (msg.startsWith("/CChat")) {
                    String[] parts = msg.split(" ");
                    if (parts.length != 3) {
                        send("/error 잘못된 채팅 생성 명령어 형식입니다.");
                        continue;
                    }

                    String user1 = parts[1];
                    String user2 = parts[2];

                    try {
                        PreparedStatement checkStmt = conn.prepareStatement(
                                "SELECT COUNT(*) FROM chats WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)");
                        checkStmt.setString(1, user1);
                        checkStmt.setString(2, user2);
                        checkStmt.setString(3, user2);
                        checkStmt.setString(4, user1);

                        ResultSet rs = checkStmt.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        rs.close();
                        checkStmt.close();

                        if (count > 0) {
                            send("이미 존재하는 채팅방입니다.");
                            continue;
                        }

                        PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO chats (user1, user2) VALUES (?, ?)");
                        insertStmt.setString(1, user1);
                        insertStmt.setString(2, user2);
                        insertStmt.executeUpdate();
                        insertStmt.close();

                        send("채팅방 생성 완료");

                    } catch (SQLException e) {
                        send("채팅방 생성 중 오류가 발생했습니다.");
                        e.printStackTrace();
                    }

                } else if (msg.startsWith("/emoji")) {
                    sendPrivateEmoji(msg);

                }

                else if (msg.startsWith("/ChatRecords")) {
                    String[] parts = msg.split(" ", 3);
                    if (parts.length >= 3) {
                        String Me = parts[1]; // aaaa
                        String target = parts[2]; // tttt
                        int type;
                        String message;
                        String time;
                        String nametest;
                        try {
                            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM messages " +
                                    "WHERE (se_username = ? AND re_username = ?) " +
                                    " OR (se_username = ? AND re_username = ?) " +
                                    "ORDER BY timestamp ASC");
                            preparedStatement.setString(1, Me);
                            preparedStatement.setString(2, target);
                            preparedStatement.setString(3, target);
                            preparedStatement.setString(4, Me);

                            ResultSet resultSet = preparedStatement.executeQuery();

                            while (resultSet.next()) {
                                if ((type = resultSet.getInt("message_type")) == 0) {
                                    message = resultSet.getString("contents");
                                    time = resultSet.getString("timestamp");
                                    if ((Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/sendMsgRECOVER||" + message + "||" + time.toString() + "\n");
                                        continue;
                                    } else if ((!Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/receiveMsgRECOVER||" + message + "||" + time.toString() + "\n");
                                        continue;
                                    }

                                } else if ((type = resultSet.getInt("message_type")) == 1) {
                                    message = resultSet.getString("imagedata");
                                    time = resultSet.getString("timestamp");
                                    if ((Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/sendImgRECOVER||" + message + "||" + time.toString() + "\n");
                                        Thread.sleep(3000);
                                        continue;
                                    } else if ((!Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/receiveimgRECOVER||" + message + "||" + time.toString() + "\n");
                                        Thread.sleep(3000);
                                        continue;
                                    }

                                } else if ((type = resultSet.getInt("message_type")) == 2) {
                                    message = resultSet.getString("contents");
                                    time = resultSet.getString("timestamp");
                                    if ((Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/sendEmojiRECOVER||" + message + "||" + time.toString());
                                        continue;
                                    } else if ((!Me.equals((nametest = resultSet.getString("se_username"))))) {
                                        send("/#/receiveEmojiRECOVER||" + message + "||" + time.toString());
                                        continue;
                                    }
                                }

                            }
                            resultSet.close();
                            preparedStatement.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            clientWriters.remove(myName);
            usrNick.remove(myName);
        }
    }

    private void sendPrivateMessage(String message) {
        String[] parts = message.split(" ", 3);
        if (parts.length >= 3) {
            String target = parts[1];
            String content = parts[2];
            PrintWriter writer = clientWriters.get(target);
            System.err.println("Db 연결상태 " + (conn != null));
            if (writer != null && friend.contains(target)) {
                writer.println("/#/recvmsg " + myName + " " + content);
                try {
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "INSERT INTO messages (se_username, re_username, contents, message_type) VALUES (?, ?, ?, ?)");
                    preparedStatement.setString(1, myName);
                    preparedStatement.setString(2, target);
                    preparedStatement.setString(3, content);
                    preparedStatement.setInt(4, 0);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.err.println("DB 저장 실패.");
                    e.printStackTrace();

                }
            } else {
                try {
                    send("/#/error 친구가 오프라인입니다.");
                } catch (Exception e) {
                    System.out.println("쪽지 전달 실패");
                }
            }

        }
    }

    private void sendPrivateImgMsg(String imgMsg) {
        String[] parts = imgMsg.split(" ", 3);
        if (parts.length >= 3) {
            String target = parts[1];
            String ImgContent = parts[2];
            String contents = "ImgMsg";
            PrintWriter writer = clientWriters.get(target);
            if (writer != null && friend.contains(target) && ImgContent.endsWith("/img_EOF")) {

                writer.println("/#/recvimgmsg " + myName + " " + ImgContent);
                // try {
                //     PreparedStatement preparedStatement = conn.prepareStatement(
                //             "INSERT INTO messages (se_username, re_username, contents, imagedata, message_type) VALUES (?, ?, ?, ?, ?)");
                //     preparedStatement.setString(1, myName);
                //     preparedStatement.setString(2, target);
                //     preparedStatement.setString(3, contents);
                //     preparedStatement.setString(4, ImgContent);
                //     preparedStatement.setInt(5, 3);
                //     preparedStatement.executeUpdate();
                //     preparedStatement.close();

                // } catch (SQLException e) {
                //     System.err.println("DB 저장 실패패");
                //     e.printStackTrace();
                // }
            } else {
                try {
                    send("/#/error 친구가 오프라인 입니다.");
                } catch (Exception e) {
                    System.out.println("이미지 쪽지 전달 실패");
                }
            }

        }
    }

    private void sendPrivateEmoji(String emojiId) {
        String[] parts = emojiId.split(" ", 3);
        if (parts.length >= 3) {
            String target = parts[1];
            String emojiContents = parts[2];
            PrintWriter writer = clientWriters.get(target);
            System.err.println("서버 이모지 디버거 target: " + target);
            System.err.println("서버 이모지 디버거 emojiContents :" + emojiContents);
            if (writer != null && friend.contains(target)) {
                writer.println("/#/recvemoji " + myName + " " + emojiContents);
            } else {
                try {
                    send("/#/error 친구가 오프라인입니다.");
                } catch (Exception e) {
                    System.out.println("쪽지 전달 실패");
                }
            }
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "INSERT INTO messages (se_username, re_username, contents, message_type) VALUES (?, ?, ?, ?)");
                preparedStatement.setString(1, myName);
                preparedStatement.setString(2, target);
                preparedStatement.setString(3, emojiContents);
                preparedStatement.setInt(4, 2);
                preparedStatement.executeUpdate();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void firstAccess() {
        String msg;
        int loginAccess = -1;
        try {
            while (loginAccess == -1 && (msg = reader.readLine()) != null) {
                if (msg.startsWith(":c:login")) {
                    String id = reader.readLine();
                    String pwd = encrypt(reader.readLine());
                    String sql = "SELECT password FROM users WHERE username = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getString("password").equals(pwd)) {
                        myName = id;
                        send("/#/info " + myName);
                        loginAccess = 1;

                        PreparedStatement flstmt = conn.prepareStatement("SELECT friend FROM friends WHERE owner = ?");

                        flstmt.setString(1, myName);

                        ResultSet frs = flstmt.executeQuery();

                        while (frs.next()) {
                            friend.add(frs.getString("friend"));
                        }
                    } else {
                        send("-1");
                    }
                } else if (msg.startsWith(":c:sign_up")) {
                    String id = reader.readLine();
                    String pwd = encrypt(reader.readLine());
                    String nick = reader.readLine();

                    System.out.println("ID: " + id);
                    System.out.println("PWD: " + pwd);
                    System.out.println("NICK: " + nick);

                    PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
                    check.setString(1, id);
                    ResultSet rs = check.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        PreparedStatement insert = conn
                                .prepareStatement("INSERT INTO users (username, password ,nickname) VALUES (?, ?, ?)");
                        insert.setString(1, id);
                        insert.setString(2, pwd);
                        insert.setString(3, nick);
                        insert.executeUpdate();
                        myName = id;
                        send("/#/signed_up " + myName);
                        loginAccess = 1;
                    } else {
                        send("/#/error 이미 존재하는 ID입니다.");
                    }
                }
            }
            usrNick.put(myName, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}

public class Server {
    static String serverVersion = "v0.0.1b";

    public static void main(String[] args) {
        int port = 4885;
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("서버열림 " + serverVersion);
            while (true) {
                Socket user = ss.accept();
                System.out.println("클라이언트 입장: " + user.getInetAddress() + ":" + user.getPort());
                new msgth(user).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
