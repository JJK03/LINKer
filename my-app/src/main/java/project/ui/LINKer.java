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

import project.util.MessageWithTimestamp;
import project.util.RoundedTextField;
import project.util.SpeechBubble;
import project.util.SvgUtils;
import project.util.ScrollBar;

// TODO 이모티콘 기능 구현하기 .svg

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
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        // 메시지 패널
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
        verticalBar.setUnitIncrement(20);

        // 입력창 하단 패널
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBounds(30, 520, 740, 40);
        inputPanel.setOpaque(false);
        contentPane.add(inputPanel);

        // 왼쪽 이미지 업로드 버튼
        ImageIcon plusIcon = SvgUtils.loadSvgIcon("/img/Plus.svg", 35, 35);
        JButton imageButton = new JButton(plusIcon);
        imageButton.setPreferredSize(new Dimension(40, 40));
        imageButton.setContentAreaFilled(false);
        imageButton.setBorderPainted(false);
        imageButton.setFocusPainted(false);
        imageButton.setOpaque(false);
        imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inputPanel.add(imageButton, BorderLayout.WEST);

        // 가운데 입력 필드
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

        // 오른쪽 전송 버튼만 있는 패널 (이모지 버튼 제거됨)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        ImageIcon sendIcon = SvgUtils.loadSvgIcon("/img/Send.svg", 35, 35);
        JButton sendButton = new JButton(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(sendButton);

        inputPanel.add(rightPanel, BorderLayout.EAST);

        // 상단 제목 라벨
        JLabel lblTitle = new JLabel("상대방_이름", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("Noto Sans CJK KR", Font.BOLD, 20));
        lblTitle.setBounds(0, 16, width, 24);
        contentPane.add(lblTitle);

        // 이벤트 등록
        sendButton.addActionListener(e -> sendMessage());
        // Enter로 메시지 전송
        roundedInputField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        roundedInputField.getActionMap().put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        imageButton.addActionListener(e -> uploadImage());

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
    }

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
                        x = panelWidth - width - 10;
                    } else {
                        x = 10;
                    }

                    msg.setBounds(x, y, width, height);
                    y += height + 10;
                }
            }

            messagePanel.setPreferredSize(new Dimension(panelWidth, y));
            messagePanel.revalidate();
            messagePanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
    }

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
                        dragStart = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    public void mouseReleased(MouseEvent e) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                });

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

                addMouseWheelListener(e -> {
                    double delta = 0.05f * e.getPreciseWheelRotation();
                    scale -= delta;
                    scale = Math.max(0.1, Math.min(scale, 5));
                    repaint();
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
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