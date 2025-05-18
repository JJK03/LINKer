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
        
        // GUI 크기
        int width = 800;
        int height = 600;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(148, 180, 193));
        contentPane.setBorder(new LineBorder(Color.GRAY, 2, true));
        setContentPane(contentPane);
        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/img/Back.png"));
        Image img = rawIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon closeIcon = new ImageIcon(img);

        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(10, 10, 30, 30);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);
        inputField = new JTextField();
        inputField.setBounds(30, 520, 640, 40);
        contentPane.add(inputField);

        JButton sendButton = new JButton("전송");
        sendButton.setBounds(690, 520, 80, 40);
        contentPane.add(sendButton);

        // 제목
        JLabel lblNewLabel = new JLabel("장진규", SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18)); // 글씨 크기
        lblNewLabel.setBounds(0, 8, width, 24);
        contentPane.add(lblNewLabel);

        messagePanel = new JPanel();
        messagePanel.setLayout(null);
        messagePanel.setBackground(new Color(148, 180, 193));

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(30, 60, 740, 440);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        contentPane.add(scrollPane);

        // 외부 클래스로 분리된 스크롤바 UI 적용
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
        
     // 창이 뜬 후 입력창에 자동으로 포커스
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());

    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
        	// 말풍선 색상
            SpeechBubble bubble = new SpeechBubble(message, true, Color.decode("#DCF8C6"));
            messagePanel.add(bubble);

            // 메시지 패널 크기와 말풍선 위치
            int y = 0;
            int width = scrollPane.getWidth();
            for (Component comp : messagePanel.getComponents()) {
                Dimension pref = comp.getPreferredSize();
                int x = width - pref.width - 25;
                comp.setBounds(x, y, pref.width, pref.height);
                y += pref.height + 10;
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
