package project.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.util.EmojiPicker;
import project.util.MessageWithTimestamp;
import project.util.RoundedTextField;
import project.util.SpeechBubble;
import project.util.SvgUtils;
import project.util.ScrollBar;

public class LINKer extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private Point initialClick;

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
            // OS 기본 테마 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("LINKer");
        setUndecorated(true);

        int width = 800;
        int height = 600;
        int arc = 40;
        // 둥근 창 모양
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 메인 contentPane - null 레이아웃
        contentPane = new JPanel(null);
        contentPane.setBackground(new Color(24, 26, 30));
        contentPane.setBorder(new LineBorder(new Color(70, 70, 70), 2, true));
        setContentPane(contentPane);

        // 상단 닫기 버튼
        ImageIcon closeIcon = SvgUtils.loadSvgIcon("/img/Back.svg", 35, 35);
        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(10, 14, 30, 30);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        contentPane.add(closeButton);

        // 메시지 패널 (null layout - 수동 배치)
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

        // 커스텀 스크롤바 적용
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, Integer.MAX_VALUE));
        verticalBar.setUI(new ScrollBar());
        verticalBar.setUnitIncrement(20); // 휠 스크롤 속도 증가

        // 입력창 하단 패널 - BorderLayout 사용
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBounds(30, 520, 740, 40);
        inputPanel.setOpaque(false);
        contentPane.add(inputPanel);

        // 1) 왼쪽 이미지 추가 버튼
        ImageIcon plusIcon = SvgUtils.loadSvgIcon("/img/Plus.svg", 35, 35);
        JButton imageButton = new JButton(plusIcon);
        imageButton.setPreferredSize(new Dimension(40, 40));
        imageButton.setContentAreaFilled(false);
        imageButton.setBorderPainted(false);
        imageButton.setFocusPainted(false);
        imageButton.setOpaque(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(imageButton, BorderLayout.WEST);

        // 2) 가운데 입력 필드 (RoundedTextField)
        roundedInputField = new RoundedTextField(30);
        roundedInputField.setPlaceholder("메시지를 입력하세요...");
        inputPanel.add(roundedInputField, BorderLayout.CENTER);

        // 문서 변경시 플레이스홀더 다시 그리기
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

        // 3) 오른쪽에 이모지 버튼과 전송 버튼 묶는 패널 (FlowLayout 오른쪽 정렬)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        // 이모지 버튼
        ImageIcon emojiIcon = SvgUtils.loadSvgIcon("/img/Emoji.svg", 35, 35);
        JButton emojiButton = new JButton(emojiIcon);
        emojiButton.setPreferredSize(new Dimension(40, 40));
        emojiButton.setContentAreaFilled(false);
        emojiButton.setBorderPainted(false);
        emojiButton.setFocusPainted(false);
        emojiButton.setOpaque(false);
        emojiButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(emojiButton);

        // 전송 버튼
        ImageIcon sendIcon = SvgUtils.loadSvgIcon("/img/Send.svg", 35, 35);
        JButton sendButton = new JButton(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(sendButton);

        // 오른쪽 패널을 inputPanel의 EAST에 추가
        inputPanel.add(rightPanel, BorderLayout.EAST);

        // 상단 제목 라벨
        JLabel lblTitle = new JLabel("상대방_이름", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblTitle.setBounds(0, 16, width, 24);
        contentPane.add(lblTitle);

        // 이벤트 등록
        sendButton.addActionListener(e -> sendMessage());
        roundedInputField.addActionListener(e -> sendMessage());
        imageButton.addActionListener(e -> uploadImage());

        emojiButton.addActionListener(e -> {
            EmojiPicker picker = new EmojiPicker(ev -> {
                String emoji = ((JMenuItem) ev.getSource()).getText();
                roundedInputField.setText(roundedInputField.getText() + emoji);
                roundedInputField.requestFocusInWindow();
            });
            picker.show(emojiButton, 0, emojiButton.getHeight());
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

        // 시작시 입력창에 포커스
        SwingUtilities.invokeLater(() -> roundedInputField.requestFocusInWindow());
    }

    // 메시지 전송 처리
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

    // 이미지 업로드 처리
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("이미지 업로드");
        chooser.setFileFilter(new FileNameExtensionFilter("이미지 파일", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());

            Image scaled = icon.getImage().getScaledInstance(150, -1, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);

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

    // 메시지 패널 레이아웃 조정
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
                        x = panelWidth - width - 10; // 오른쪽 정렬
                    } else {
                        x = 10; // 왼쪽 정렬
                    }

                    msg.setBounds(x, y, width, height);
                    y += height + 10;
                }
            }

            messagePanel.setPreferredSize(new Dimension(panelWidth, y));
            messagePanel.revalidate();
            messagePanel.repaint();

            // 레이아웃이 모두 끝난 다음 스크롤을 최하단으로 설정
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
    }

    // 이미지 클릭 팝업
    private void showImagePopup(ImageIcon icon) {
        final JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 220));
        dialog.setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize);
        dialog.setLocationRelativeTo(null);

        int closeBtnSize = 40;
        ImageIcon closeIcon = SvgUtils.loadSvgIcon("/img/Close.svg", closeBtnSize, closeBtnSize);
        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(screenSize.width - closeBtnSize - 20, 20, closeBtnSize, closeBtnSize);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton);

        class ZoomableImagePanel extends JPanel {
            private Image image = icon.getImage();
            private double scale = 1.0;
            private int offsetX, offsetY;
            private Point dragStart;

            public ZoomableImagePanel() {
                setOpaque(false);

                addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        // 닫기 버튼 영역인지 확인
                        if (closeButton.getBounds().contains(e.getPoint())) {
                            dragStart = null;
                            return;
                        }
                        dragStart = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    public void mouseReleased(MouseEvent e) {
                        dragStart = null;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                });

                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        if (dragStart != null) {
                            if (closeButton.getBounds().contains(e.getPoint())) {
                                return;
                            }
                            int dx = e.getX() - dragStart.x;
                            int dy = e.getY() - dragStart.y;
                            offsetX += dx;
                            offsetY += dy;
                            dragStart = e.getPoint();
                            repaint();
                        }
                    }
                });

                addMouseWheelListener(e -> {
                    double delta = 0.1 * e.getPreciseWheelRotation();
                    double oldScale = scale;
                    scale -= delta;
                    scale = Math.max(0.1, Math.min(scale, 5.0)); // 제한

                    Point mouse = e.getPoint();

                    int centerX = getWidth() / 2 + offsetX;
                    int centerY = getHeight() / 2 + offsetY;

                    double imgX = (mouse.x - centerX) / oldScale;
                    double imgY = (mouse.y - centerY) / oldScale;

                    offsetX = (int) (offsetX + (oldScale - scale) * imgX);
                    offsetY = (int) (offsetY + (oldScale - scale) * imgY);

                    repaint();
                });

                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                int imgWidth = (int) (image.getWidth(this) * scale);
                int imgHeight = (int) (image.getHeight(this) * scale);

                int centerX = getWidth() / 2 + offsetX;
                int centerY = getHeight() / 2 + offsetY;

                g2d.drawImage(image, centerX - imgWidth / 2, centerY - imgHeight / 2, imgWidth, imgHeight, this);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(screenSize.width, screenSize.height);
            }
        }

        ZoomableImagePanel zoomPanel = new ZoomableImagePanel();
        zoomPanel.setBounds(0, 0, screenSize.width, screenSize.height);
        dialog.add(zoomPanel);

        dialog.setVisible(true);
    }
}