package project.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.util.MessageWithTimestamp;
import project.util.RoundedTextField;
import project.util.SpeechBubble;
import project.util.SvgUtils;
import project.util.ScrollBar;

// TODO: MAVEN 해결, SVG 등록하기, SvgUtils.java 구현 tlqkf

public class LINKer extends JFrame {

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
                LINKer frame = new LINKer();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LINKer() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("LINKer");
        setUndecorated(true);

        int width = 800;
        int height = 600;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel(null);
        contentPane.setBackground(new Color(24, 26, 30));
        contentPane.setBorder(new LineBorder(new Color(70, 70, 70), 2, true));
        setContentPane(contentPane);

        // 상단 닫기 버튼
        // ImageIcon rawIcon = new ImageIcon(getClass().getResource("/img/Back.png"));
        // Image img = rawIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        // ImageIcon closeIcon = new ImageIcon(img);
        ImageIcon closeIcon = SvgUtils.loadSvgIcon("/img/Back.svg", 35, 35);

        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(10, 14, 30, 30);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);

        // 메시지 영역
        messagePanel = new JPanel(null);
        messagePanel.setBackground(new Color(24, 26, 30));

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
        verticalBar.setUI(new ScrollBar());
        verticalBar.setUnitIncrement(20); // 휠 스크롤 속도 증가
        // 입력창 하단 패널
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBounds(30, 520, 740, 40);
        inputPanel.setOpaque(false);
        contentPane.add(inputPanel);

        // ImageIcon plusRawIcon = new ImageIcon(getClass().getResource("/img/Plus.png")); // +이미지 경로
        // Image plusImg = plusRawIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        // ImageIcon plusIcon = new ImageIcon(plusImg);
        ImageIcon plusIcon = SvgUtils.loadSvgIcon("/img/Plus.svg", 35, 35);

        JButton imageButton = new JButton(plusIcon);
        imageButton.setPreferredSize(new Dimension(40, 40));
        imageButton.setFocusable(false);
        imageButton.setContentAreaFilled(false); // 배경 없애기
        imageButton.setBorderPainted(false); // 테두리 없애기
        imageButton.setOpaque(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(imageButton, BorderLayout.WEST);

        inputField = new RoundedTextField(20);
        inputPanel.add(inputField, BorderLayout.CENTER);

        // ImageIcon sendRawIcon = new ImageIcon(getClass().getResource("/img/Send.png")); // 내가 준비한 전송 이미지 경로
        // Image sendImg = sendRawIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH); // 버튼 크기에 맞게 조정
        // ImageIcon sendIcon = new ImageIcon(sendImg);
        ImageIcon sendIcon = SvgUtils.loadSvgIcon("/img/Send.svg", 35, 35);

        JButton sendButton = new JButton(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setFocusable(false);
        sendButton.setContentAreaFilled(false); // 배경 없애기
        sendButton.setBorderPainted(false); // 테두리 없애기
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

        JLabel lblTitle = new JLabel("상대방_이름", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblTitle.setBounds(0, 16, width, 24);
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
            // 말풍선 색깔
            SpeechBubble bubble = new SpeechBubble(message, true, Color.decode("#B3E5FC"));

            // 현재 시간 포맷팅
            String time = new SimpleDateFormat("HH:mm").format(new Date());

            // 타임스탬프와 함께 패널에 추가
            MessageWithTimestamp wrapped = new MessageWithTimestamp(bubble, time, true);
            messagePanel.add(wrapped);

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

            // 말풍선 색깔
            SpeechBubble imageBubble = new SpeechBubble(
                    scaledIcon,
                    true,
                    Color.decode("#B3E5FC"),
                    () -> showImagePopup(icon));

            String time = new SimpleDateFormat("HH:mm").format(new Date());
            MessageWithTimestamp wrapped = new MessageWithTimestamp(imageBubble, time, true);
            messagePanel.add(wrapped);

            layoutMessages();
        }
    }

    private void layoutMessages() {
        SwingUtilities.invokeLater(() -> {
            int panelWidth = messagePanel.getWidth();
            if (panelWidth == 0)
                panelWidth = scrollPane.getViewport().getWidth();

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

    // 사진 확대
    // 1차 팝업 띄우는 함수 (중간 크기)
    private void showImagePopup(ImageIcon icon) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 150));
        dialog.setLayout(new GridBagLayout());

        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(600, -1, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaled));
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        dialog.add(label);

        // 2차 팝업 띄우기 위해 클릭 이벤트 등록
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose(); // 1차 팝업 닫고
                showFullScreenImagePopup(icon); // 2차 팝업 열기
            }
        });

        dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        dialog.setLocationRelativeTo(null);

        dialog.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        });

        dialog.setFocusable(true);
        dialog.setVisible(true);
    }

    // 2차 팝업 - 화면 꽉 채우는 풀스크린 이미지 뷰어
    private void showFullScreenImagePopup(ImageIcon icon) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 220));
        dialog.setLayout(new GridBagLayout());

        Image img = icon.getImage();
        // 화면 크기에 맞춰 최대 크기로 확대 (비율 유지)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = screenSize.width;
        int maxHeight = screenSize.height;

        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();

        double widthRatio = (double) maxWidth / imgWidth;
        double heightRatio = (double) maxHeight / imgHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (imgWidth * ratio);
        int newHeight = (int) (imgHeight * ratio);

        Image scaled = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(scaled));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(label);

        // 클릭 시 팝업 닫기
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        });

        dialog.setSize(screenSize);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}