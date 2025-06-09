package project.login;

import project.core.AppContext;
import project.panel.ChatPanel;
import project.panel.FriendPanel;
import project.panel.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import project.Network.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class FriendList extends JFrame {

    private Point initialClick;
    private JPanel centerPanel;
    private String userEmail;
    private List<String> friends = new ArrayList<>();
    private String CUser = AppContext.getCurrentUserID();
    public FriendList(String userEmail) {
        this.userEmail = userEmail;

        setTitle("LINKer - 친구 목록");
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setBounds(100, 100, 765, 471);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (!GraphicsEnvironment.isHeadless()) {
            setShape(new RoundRectangle2D.Double(0, 0, 765, 471, 30, 30));
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.setBackground(new Color(30, 30, 32));
        setContentPane(contentPane);

        // 상단 바
        JPanel topBar = new JPanel(null);
        topBar.setPreferredSize(new Dimension(765, 30));
        topBar.setBackground(new Color(24, 24, 24));

        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        topBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(getX() + xMoved, getY() + yMoved);
            }
        });

        JButton closeBtn = new JButton("✕");
        closeBtn.setBounds(707, 0, 58, 30);
        closeBtn.setForeground(new Color(180, 180, 180));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);

        contentPane.add(topBar, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0, 0, 0));
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // 하단 네비게이션 바
        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(24, 24, 24));
        navBar.setPreferredSize(new Dimension(0, 60));
        navBar.setLayout(new GridLayout(1, 4));

        navBar.add(createNavButton("친구목록"));
        navBar.add(createNavButton("채팅목록"));
        navBar.add(createNavButton("친구추가"));
        navBar.add(createNavButton("설정"));

        contentPane.add(navBar, BorderLayout.SOUTH);

        showPanel("친구목록");

        // 기본 glassPane 세팅
        JPanel glassPane = (JPanel) getGlassPane();
        glassPane.setLayout(null);
        glassPane.setVisible(false);
        glassPane.setOpaque(false);

        setVisible(true);
    }

    private JButton createNavButton(String name) {
        JButton button = new JButton(name);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(24, 24, 24));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showPanel(name));

        // 마우스 올렸을 때 배경색 변경, 나갔을 때 원래 배경색으로 복구
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 174, 255));  // 파란색
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(24, 24, 24));   // 원래 색
            }
        });

        return button;
    }

    private void showPanel(String name) {
        if (name.equals("친구추가")) {
            String friendId = JOptionPane.showInputDialog(this, "친구추가할 LINKer 아이디를 입력하세요:");
            if (friendId != null && !friendId.trim().isEmpty()) {
                /*friends.add(friendId.trim());
                JOptionPane.showMessageDialog(this, friendId + "님이 친구 목록에 추가되었습니다.");*/
                AppContext.getServerConn().addFriend(friendId.trim());
            }
            showPanel("친구목록"); // 친구 추가 후 친구목록 다시 보여주기
            return;
        }


        centerPanel.removeAll();
        centerPanel.setLayout(null);

        switch (name) {
            case "친구목록":
                System.err.println("친구목록 선택");
                FriendPanel friendPanel = new FriendPanel(friends);
                friendPanel.setBounds(0, 0, 765, 400);
                centerPanel.add(friendPanel);
                AppContext.getServerConn().requestFriendList();

                break;

            case "채팅목록":
                System.err.println("채팅목록 선택");
                JLabel chatLabel = new JLabel("여기에 최근 채팅 내역이 표시됩니다.");
                chatLabel.setBounds(30, 30, 500, 20);
                chatLabel.setForeground(Color.LIGHT_GRAY);
                chatLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                centerPanel.add(chatLabel);
                AppContext.getServerConn().sendChathistory(CUser);
                break;

            case "설정":
                System.err.println("설정 선택.");
                SettingsPanel settingsPanel = new SettingsPanel(userEmail, null);
                settingsPanel.setBounds(0, 0, 765, 400);
                centerPanel.add(settingsPanel);
                break;
        }

        centerPanel.revalidate();
        centerPanel.repaint();


        AppContext.getServerConn().setFriendListener(new FriendListener() {
            @Override //친구추가 성공여부 리스너, 서버에서 결과값 받아서 전송
            public void onFriendAddResult(boolean success, String message) {
                SwingUtilities.invokeLater(() -> {
                    String title = success ? "✅ 친구 추가 성공" : "❌ 친구 추가 실패";
                    JOptionPane.showMessageDialog(FriendList.this, message, title, JOptionPane.INFORMATION_MESSAGE);
                });
            }

            @Override // 친구리스트 리스너, 서버에서 친구 리스트 받아서 전송.
            public void onFriendListReceived(String[] friends) {
                System.err.println("친구목록 디버거: " + Arrays.toString(friends) );
                SwingUtilities.invokeLater(() -> {
                    centerPanel.removeAll();
                    FriendPanel friendPanel = new FriendPanel(List.of(friends));
                    friendPanel.setBounds(0, 0, 765, 400);
                    centerPanel.add(friendPanel);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                });
            }
            // 다른 메서드는 비워두거나 구현
            @Override public void onFriendRequest(String fromUserId) {}
        });

        AppContext.getServerConn().setChatListener(new ChatListener() {
            @Override
            public void onChatListReceived(String[] chats) {
                SwingUtilities.invokeLater(() -> {

                System.err.println("채팅목록 디버거: " + Arrays.toString(chats) );
                centerPanel.removeAll();
                ChatPanel chatPanel = new ChatPanel(List.of(chats));
                chatPanel.setBounds(0, 0, 765, 400);
                centerPanel.add(chatPanel);
                centerPanel.revalidate();
                centerPanel.repaint();
                });
            }
        });


    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FriendList("test@example.com"));
    }
}
