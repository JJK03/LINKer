package project.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.security.*;
import java.util.*;

import project.db.DBManager;

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

    public msgth(Socket socket) {
        this.socket = socket;
        try {
            output = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (Exception e) {
            System.out.println("쓰레드 생성 에러");
        }
    }

    private void send(String msg) throws Exception {
        output.write((msg).getBytes("UTF-8"));
        output.flush();
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
                if (msg.startsWith("/msg")) {
                    sendPrivateMessage(msg);
                } else if (msg.equals("/exit")) {
                    clientWriters.remove(myName);
                    usrNick.remove(myName);
                    socket.close();
                    return;
                } else if (msg.startsWith("/fl")) {
                    StringBuilder fl = new StringBuilder();
                    for (String data : friend) fl.append(" ").append(data);
                    send("/#/fl" + fl);
                } else if (msg.startsWith("/af")) {
                    String[] parts = msg.split(" ");
                    if (parts.length >= 2) {
                        String target = parts[1];
                        try (Connection conn = DBManager.getConnection()) {
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
                                    send("/#/info " + target + "을 친구로 추가하였습니다.");
                                } else {
                                    send("/#/error 이미 친구입니다.");
                                }
                            } else {
                                send("/#/error 사용자 존재하지 않음.");
                            }
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
            if (writer != null && friend.contains(target)) {
                writer.println("/#/recvmsg " + myName + " " + content);
            } else {
                try {
                    send("/#/error 친구가 오프라인입니다.");
                } catch (Exception e) {
                    System.out.println("쪽지 전달 실패");
                }
            }
        }
    }

    private void firstAccess() {
        String msg;
        int loginAccess = -1;
        try (Connection conn = DBManager.getConnection()) {
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
                        send(myName);
                        loginAccess = 1;
                        PreparedStatement flstmt = conn.prepareStatement("SELECT friend FROM friends WHERE owner = ?");
                        flstmt.setString(1, myName);
                        ResultSet frs = flstmt.executeQuery();
                        while (frs.next()) friend.add(frs.getString("friend"));
                    } else {
                        send("-1");
                    }
                } else if (msg.startsWith(":c:sign_up")) {
                    String id = reader.readLine();
                    String pwd = encrypt(reader.readLine());
                    PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
                    check.setString(1, id);
                    ResultSet rs = check.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                        insert.setString(1, id);
                        insert.setString(2, pwd);
                        insert.executeUpdate();
                        myName = id;
                        send(myName);
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
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}

public class Server {
    static String serverVersion = "v0.0.1b";

    public static void main(String[] args) {
        int port = 80;
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