package project;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class GigaChat extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Point initialClick;
    private JTextField inputField;
    private int messageCount = 0;

    private JScrollPane scrollPane;
    private JPanel messagePanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GigaChat frame = new GigaChat();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public GigaChat() {
        setTitle("기가챗");
        setUndecorated(true);

        int width = 344;
        int height = 514;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, width, height);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(134, 163, 199));
        contentPane.setBorder(new LineBorder(Color.GRAY, 2, true));
        setContentPane(contentPane);

        JButton closeButton = new JButton("<");
        closeButton.setFont(new Font("Yu Gothic Medium", Font.BOLD | Font.ITALIC, 13));
        closeButton.setBounds(-13, 5, 63, 30);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setOpaque(false);
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);

        inputField = new JTextField();
        inputField.setBounds(22, 455, 241, 30);
        contentPane.add(inputField);

        JButton sendButton = new JButton("전송");
        sendButton.setBounds(275, 454, 57, 30);
        contentPane.add(sendButton);

        JLabel lblNewLabel = new JLabel("대화상대");
        lblNewLabel.setBounds(148, 10, 57, 15);
        contentPane.add(lblNewLabel);

        messagePanel = new JPanel();
        messagePanel.setLayout(null);
        messagePanel.setBackground(new Color(134, 163, 199));

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(22, 60, 309, 365);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        contentPane.add(scrollPane);

        // ✅ 외부 클래스로 분리된 스크롤바 UI 적용
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, Integer.MAX_VALUE));
        verticalBar.setUI(new ThinScrollBarUI());

        // 액션 리스너
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // 창 드래그
        contentPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        contentPane.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(getX() + xMoved, getY() + yMoved);
            }
        });
        
     // 창이 뜬 후 입력창에 자동으로 포커스 맞추기
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());

    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, true);
            messagePanel.add(bubble);

            // 메시지 패널 크기와 말풍선 위치 다시 계산
            int y = 0;
            int width = scrollPane.getWidth();
            for (Component comp : messagePanel.getComponents()) {
                Dimension pref = comp.getPreferredSize();
                int x = width - pref.width - 25;  // 오른쪽 정렬
                comp.setBounds(x, y, pref.width, pref.height);
                y += pref.height + 10;  // 다음 말풍선 y 위치 (간격 10)
            }

            messagePanel.setPreferredSize(new Dimension(scrollPane.getWidth(), y + 10));
            messagePanel.revalidate();
            messagePanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });

            inputField.setText("");
        }
    }
}