package project;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GigaChat extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Point initialClick;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private JPanel messagePanel;
    private RoundedTextField roundedInputField;

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

        int width = 800;
        int height = 600;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel(null);
        contentPane.setBackground(new Color(148, 180, 193));
        contentPane.setBorder(new LineBorder(Color.GRAY, 2, true));
        setContentPane(contentPane);

        // 상단 닫기 버튼
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

        // 메시지 영역
        messagePanel = new JPanel(null);
        messagePanel.setBackground(new Color(148, 180, 193));

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(30, 60, 740, 440);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        contentPane.add(scrollPane);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, Integer.MAX_VALUE));
        verticalBar.setUI(new ThinScrollBarUI());

        // 입력창 하단 패널
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBounds(30, 520, 740, 40);
        inputPanel.setOpaque(false);
        contentPane.add(inputPanel);

        ImageIcon plusRawIcon = new ImageIcon(getClass().getResource("/img/Plus.png")); // +이미지 경로
        Image plusImg = plusRawIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon plusIcon = new ImageIcon(plusImg);

        JButton imageButton = new JButton(plusIcon);
        imageButton.setPreferredSize(new Dimension(40, 40));
        imageButton.setFocusable(false);
        imageButton.setContentAreaFilled(false); // 배경 없애기
        imageButton.setBorderPainted(false);     // 테두리 없애기
        imageButton.setOpaque(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(imageButton, BorderLayout.WEST);

        inputField = new RoundedTextField(20);
        inputPanel.add(inputField, BorderLayout.CENTER);

        ImageIcon sendRawIcon = new ImageIcon(getClass().getResource("/img/Send.png")); // 내가 준비한 전송 이미지 경로
        Image sendImg = sendRawIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // 버튼 크기에 맞게 조정
        ImageIcon sendIcon = new ImageIcon(sendImg);

        JButton sendButton = new JButton(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setFocusable(false);
        sendButton.setContentAreaFilled(false); // 배경 없애기
        sendButton.setBorderPainted(false);     // 테두리 없애기
        sendButton.setOpaque(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        roundedInputField = new RoundedTextField(30);
        roundedInputField.setPlaceholder("메시지를 입력하세요...");
        inputPanel.add(roundedInputField, BorderLayout.CENTER);

        roundedInputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                roundedInputField.repaint();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                roundedInputField.repaint();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                roundedInputField.repaint();
            }
        });

        JLabel lblTitle = new JLabel("장진규", SwingConstants.CENTER);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblTitle.setBounds(0, 8, width, 24);
        contentPane.add(lblTitle);

        // 이벤트 처리
        sendButton.addActionListener(e -> sendMessage());
        roundedInputField.addActionListener(e -> sendMessage());

        imageButton.addActionListener(e -> uploadImage());

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

        SwingUtilities.invokeLater(() -> roundedInputField.requestFocusInWindow());
    }

    private void sendMessage() {
        String message = roundedInputField.getText().trim();
        if (!message.isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, true, Color.decode("#DCF8C6"));
            messagePanel.add(bubble);
            layoutMessages();
            roundedInputField.setText("");
            
            roundedInputField.requestFocusInWindow();
        }
    }

    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("이미지 업로드");
        chooser.setFileFilter(new FileNameExtensionFilter("이미지 파일", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());

            // 크기 조정
            Image scaled = icon.getImage().getScaledInstance(150, -1, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);

            SpeechBubble imageBubble = new SpeechBubble(scaledIcon, true, Color.decode("#DCF8C6"));
            messagePanel.add(imageBubble);
            layoutMessages();
        }
    }

    private void layoutMessages() {
        SwingUtilities.invokeLater(() -> {
            int panelWidth = messagePanel.getWidth();
            if (panelWidth == 0) panelWidth = scrollPane.getViewport().getWidth();

            int y = 0;
            for (Component comp : messagePanel.getComponents()) {
                Dimension pref = comp.getPreferredSize();
                int x = panelWidth - pref.width - 25;
                comp.setBounds(x, y, pref.width, pref.height);
                y += pref.height + 10;
            }

            messagePanel.setPreferredSize(new Dimension(panelWidth, y + 10));
            messagePanel.revalidate();
            messagePanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
    }
}
