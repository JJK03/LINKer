package project.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.util.EmojiPickerPanel;
import project.util.MessageWithTimestamp;
import project.util.RoundedTextField;
import project.util.SpeechBubble;
import project.util.SvgUtils;
import project.util.ScrollBar;
// TODO: 이모지 화질개선 안 되나...?
public class LINKer extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private Point initialClick;

    private JScrollPane scrollPane;
    private JPanel messagePanel;

    private RoundedTextField roundedInputField;

    private JDialog emojiDialog;
    private EmojiPickerPanel emojiPickerPanel;

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
            // OS 기본 테마 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 기본 프레임 설정
        setTitle("LINKer");
        setUndecorated(true);
        int width = 800;
        int height = 600;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 메인 패널 설정
        contentPane = new JPanel(null);
        contentPane.setBackground(new Color(24, 26, 30));
        contentPane.setBorder(new LineBorder(new Color(70, 70, 70), 2, true));
        setContentPane(contentPane);

        // 상단 좌측 닫기 버튼
        ImageIcon closeIcon = SvgUtils.resizeSvgIcon("/img/Back.svg", 35, 35);
        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(10, 14, 30, 30);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);

        // 메시지 패널
        messagePanel = new JPanel(null);
        messagePanel.setBackground(new Color(24, 26, 30));

        // 스크롤
        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBounds(30, 60, 740, 440);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        contentPane.add(scrollPane);

        // 스크롤 커스터마이징
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, Integer.MAX_VALUE));
        verticalBar.setUI(new ScrollBar());
        verticalBar.setUnitIncrement(20);

        // 입력창 하단 패널
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBounds(30, 520, 740, 40);
        inputPanel.setOpaque(false);
        contentPane.add(inputPanel);

        // 왼쪽 하단 이미지 업로드 버튼
        ImageIcon plusIcon = SvgUtils.resizeSvgIcon("/img/Plus.svg", 35, 35);
        JButton imageButton = new JButton(plusIcon);
        imageButton.setPreferredSize(new Dimension(40, 40));
        imageButton.setContentAreaFilled(false);
        imageButton.setBorderPainted(false);
        imageButton.setFocusPainted(false);
        imageButton.setOpaque(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(imageButton, BorderLayout.WEST);

        // 메시지 입력 필드
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

        // 오른쪽 이모지/전송 버튼 영역
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        // 이모지 버튼
        ImageIcon emojiIcon = SvgUtils.resizeSvgIcon("/img/Emoji.svg", 35, 35);
        Image emojiImg = emojiIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        ImageIcon scaledEmojiIcon = new ImageIcon(emojiImg);

        JButton emojiButton = new JButton(scaledEmojiIcon);
        emojiButton.setPreferredSize(new Dimension(40, 40));
        emojiButton.setContentAreaFilled(false);
        emojiButton.setBorderPainted(false);
        emojiButton.setFocusPainted(false);
        emojiButton.setOpaque(false);
        emojiButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 전송 버튼
        ImageIcon sendIcon = SvgUtils.resizeSvgIcon("/img/Send.svg", 35, 35);
        JButton sendButton = new JButton(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // (이모지 버튼) (전송 버튼) 순서
        rightPanel.add(emojiButton);
        rightPanel.add(sendButton);

        inputPanel.add(rightPanel, BorderLayout.EAST);

        // 이모지 버튼 클릭 시 팝업 열기/닫기
        emojiButton.addActionListener(e -> {
            if (emojiDialog.isVisible()) {
                emojiDialog.setVisible(false);
            } else {
                // 이모지 버튼의 화면상 위치 가져오기
                Point buttonLocation = emojiButton.getLocationOnScreen();
                int buttonWidth = emojiButton.getWidth();

                // 이모지 다이얼로그의 위치를 버튼의 오른쪽에 맞게 설정
                int dialogX = buttonLocation.x + buttonWidth - 215;
                int dialogY = buttonLocation.y - emojiDialog.getHeight() + emojiButton.getHeight() - 50;

                // 위치 설정 및 다이얼로그 열기
                emojiDialog.setLocation(dialogX, dialogY);
                emojiDialog.setVisible(true);
            }
        });

        // 엔터키로 메시지 전송
        sendButton.addActionListener(e -> sendMessage());
        roundedInputField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        roundedInputField.getActionMap().put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        imageButton.addActionListener(e -> uploadImage());

        // 상단 상대방 이름 (채팅창 제목)
        JLabel lblTitle = new JLabel("상대방_이름", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("Noto Sans CJK KR", Font.BOLD, 20));
        lblTitle.setBounds(0, 16, width, 24);
        contentPane.add(lblTitle);

        // 이모지 아이콘 목록 1.svg ~ 20.svg
        List<ImageIcon> emojiIcons = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ImageIcon icon = SvgUtils.resizeSvgIcon("/img/" + i + ".svg", 22, 22);
            emojiIcons.add(icon);
        }

        // 이모지 패널과 다이얼로그 생성
        emojiPickerPanel = new EmojiPickerPanel(emojiIcons, this::sendEmojiMessage);
        emojiDialog = new JDialog(this, false);
        emojiDialog.setUndecorated(true);
        emojiDialog.getContentPane().add(emojiPickerPanel);
        emojiDialog.pack();
        emojiDialog.setResizable(false);

        // 포커스를 잃으면 (다른 곳 클릭) 이모지 패널이 닫힘
        emojiDialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                emojiDialog.setVisible(false);
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Do nothing
            }
        });

        // 창 드래그 이동
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
    } // LINKer 생성자 끝

    // 텍스트 메시지 전송 처리
    private void sendMessage() {
        String message = roundedInputField.getText().trim();
        if (!message.isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, true, Color.decode("#B3E5FC"));
            String time = new SimpleDateFormat("HH:mm").format(new Date());
            MessageWithTimestamp wrapped = new MessageWithTimestamp(bubble, time, true);
            messagePanel.add(wrapped);
            layoutMessages();
            roundedInputField.setText("");
            roundedInputField.requestFocusInWindow();
        }
    }
    
    // 이미지 업로드
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("이미지 업로드");
        chooser.setFileFilter(new FileNameExtensionFilter("이미지 파일", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());

            ImageIcon scaledIcon = getScaledImageIcon(icon, 60, 60);

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

    // 메시지 레이아웃 정렬
    private void layoutMessages() {
        SwingUtilities.invokeLater(() -> {
            int panelWidth = messagePanel.getWidth();
            if (panelWidth == 0)
                panelWidth = scrollPane.getViewport().getWidth();

            int y = 10;
            for (Component comp : messagePanel.getComponents()) {
                if (comp instanceof MessageWithTimestamp) {
                    MessageWithTimestamp msg = (MessageWithTimestamp) comp;
                    Dimension preferred = msg.getPreferredSize();

                    int width = Math.min(preferred.width, panelWidth - 40);
                    int height = preferred.height;

                    int x;
                    if (msg.isMine()) {
                        x = panelWidth - width - 1;
                    } else {
                        x = 1;
                    }

                    msg.setBounds(x, y, width, height);
                    y += height + 10;
                }
            }

            messagePanel.setPreferredSize(new Dimension(panelWidth, y));
            messagePanel.revalidate();
            messagePanel.repaint();

            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    // 이모지 메시지 전송
    private void sendEmojiMessage(ImageIcon icon) {
        // 크기 조절
        Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        SpeechBubble emojiBubble = new SpeechBubble(
                scaledIcon,
                true,
                Color.decode("#B3E5FC"),
                () -> showImagePopup(icon) // 확대 기능 유지
        );

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        MessageWithTimestamp wrapped = new MessageWithTimestamp(emojiBubble, time, true);
        messagePanel.add(wrapped);
        layoutMessages();
    }

    // 이미지 사이즈 재조절
    public static ImageIcon getScaledImageIcon(ImageIcon srcIcon, int w, int h) {
        Image srcImg = srcIcon.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        // 고품질 스케일링 옵션 설정
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 이미지 크기 조절
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }

    // 이미지 전체 보기 팝업 (확대 및 드래그 기능)
    private void showImagePopup(ImageIcon icon) {
        final JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 220));
        dialog.setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize);
        dialog.setLocationRelativeTo(null);
        // 이미지 확대 시 닫기 버튼
        int closeBtnSize = 40;
        ImageIcon closeIcon = SvgUtils.resizeSvgIcon("/img/Close.svg", closeBtnSize, closeBtnSize);
        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(screenSize.width - closeBtnSize - 20, 20, closeBtnSize, closeBtnSize);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton);

        // 확대 및 이동 가능한 이미지 패널
        class ZoomableImagePanel extends JPanel {
            private Image image = icon.getImage();
            private double scale = 1.0;
            private int offsetX, offsetY;
            private Point dragStart;

            public ZoomableImagePanel() {
                setOpaque(false);
                // 마우스 클릭 시 드래그 시작 위치 저장 및 커서 손 모양
                addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        dragStart = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    public void mouseReleased(MouseEvent e) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                });
                // 마우스 드래그로 이미지 위치 이동
                addMouseMotionListener(new MouseAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;
                        offsetX += dx;
                        offsetY += dy;
                        dragStart = e.getPoint();
                        repaint();
                    }
                });
                // 마우스 휠로 이미지 확대 축소
                addMouseWheelListener(e -> {
                    double delta = 0.05f * e.getPreciseWheelRotation();
                    scale -= delta;
                    scale = Math.max(0.1, Math.min(scale, 5));
                    repaint();
                });
            }
            // 이미지 그리기
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // 이미지 품질 설정 (부드럽게 확대 축소)
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int iw = (int) (image.getWidth(null) * scale);
                int ih = (int) (image.getHeight(null) * scale);

                int x = (getWidth() - iw) / 2 + offsetX;
                int y = (getHeight() - ih) / 2 + offsetY;

                g2.drawImage(image, x, y, iw, ih, null);
            }
        }

        ZoomableImagePanel imagePanel = new ZoomableImagePanel();
        imagePanel.setBounds(0, 0, screenSize.width, screenSize.height);
        dialog.add(imagePanel);

        dialog.setVisible(true);
    }
}