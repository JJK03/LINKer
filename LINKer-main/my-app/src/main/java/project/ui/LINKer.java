package project.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import project.Network.MessageListener;
import project.core.AppContext;
import project.util.DrawerPanel;
import project.util.EmojiPickerPanel;
import project.util.ImageConverterUtils;
import project.util.ImagePopupViewer;
import project.util.MessageWithTimestamp;
import project.util.RoundedTextField;
import project.util.SpeechBubble;
import project.util.SvgUtils;
import project.util.ScrollBar;

// TODO: 일정 내용 적는 스크롤 바 고치기 
// TODO: ImageConverterUtils.java, ImageMessageHandler.java랑 214줄 참고
public class LINKer extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private Point initialClick;

    private JScrollPane scrollPane;
    private JPanel messagePanel;

    private RoundedTextField roundedInputField;

    private JDialog emojiDialog;
    private EmojiPickerPanel emojiPickerPanel;

    private boolean isDrawerOpen = false;

    private String fname = "";
    private String userId,usernickname;



    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LINKer frame = new LINKer("상대방_이름");
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LINKer(String fname) {
        try {
            // OS 기본 테마 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(fname);

        this.fname = fname;// 상대방 이름 //장진규||aaaa

        System.out.println(fname);

        String[] parts = fname.split("\\|\\|");

        usernickname = parts[0];
        userId = parts[1];

        System.out.println(parts[0]);
        System.out.println(parts[1]);

        // 기본 프레임 설정
        setTitle("LINKer");
        setUndecorated(true);
        int width = 800;
        int height = 600;
        int arc = 40;
        setShape(new RoundRectangle2D.Double(0, 0, width, height, arc, arc));
        setBounds(100, 100, width, height);
        setLocationRelativeTo(null);
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

        // 상단 우측 옵션 버튼
        ImageIcon optionIcon = SvgUtils.resizeSvgIcon("/img/Option.svg", 35, 35);
        JButton optionButton = new JButton(optionIcon);
        optionButton.setBounds(747, 14, 30, 30);
        optionButton.setContentAreaFilled(false);
        optionButton.setBorderPainted(false);
        optionButton.setFocusPainted(false);
        optionButton.setOpaque(false);
        optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(optionButton);

        // 서랍 애니메이션
        DrawerPanel drawerPanel = new DrawerPanel(300, height);
        contentPane.add(drawerPanel);

        // 오버레이 패널
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // 부드러운 어두운 배경 (알파 값 조절 가능)
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        overlayPanel.setOpaque(false); // 반드시 false
        overlayPanel.setBounds(0, 0, width, height);
        overlayPanel.setLayout(null); // 자식 컴포넌트 올릴 경우
        overlayPanel.setVisible(false);
        contentPane.add(overlayPanel, Integer.valueOf(100)); // 위에 오도록 충분히 큰 z-index

        optionButton.addActionListener(e -> {
            toggleDrawer(drawerPanel, overlayPanel);
        });
        // 옵션 창 열린 상태에서 다른 곳에 포커스 시에 옵션 창 닫힘
        overlayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleDrawer(drawerPanel, overlayPanel);
            }
        });

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
        // 문구 삽입, 삭제, 변화를 감지하여 플레이스홀더 생성
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

        // 이미지 바이트로 변환 -> DB 전송
        imageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String imagePath = selectedFile.getAbsolutePath(); // 경로를 String으로 얻음
                System.out.println("사용자가 선택한 이미지 경로: " + imagePath);


                // 필요한 경우 ImageIcon 으로 변환
                ImageIcon icon = new ImageIcon(imagePath);

                // 필요 시 byte[]로 변환
                byte[] imageBytes = ImageConverterUtils.imageToBytes(icon, "png");

                AppContext.getServerConn().sendImage(userId, imageBytes);


                // 이미지 업로드 또는 DB 전송 등 원하는 로직 수행
                uploadImage(icon); // 예시: 업로드 메서드 호출
            }
        });


        // 상단 상대방 이름 (채팅창 제목)
        JLabel lblTitle = new JLabel(usernickname, SwingConstants.CENTER);
        lblTitle.setForeground(new Color(200, 200, 200));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setBounds(0, 16, width, 24);
        contentPane.add(lblTitle);


        // 이모지 아이콘 목록 1.svg ~ 20.svg
        List<Integer> emojiIds = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            emojiIds.add(i);
        }



        // 이모지 패널과 다이얼로그 생성
        emojiPickerPanel = new EmojiPickerPanel(emojiIds, this::sendEmojiMessageById);
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
        //서버 수신부
        AppContext.getServerConn().setMessageListener(new MessageListener() {
            @Override
            public void onMessageReceived(String fromUser, String message) {
                SwingUtilities.invokeLater(() -> {
                    receiveMessage(message);
                });
            }
            @Override
            public void onMessageSendError(String reason) {
                JOptionPane.showMessageDialog(null, reason);
            }
            @Override //이미지 출력부
            public void onImageReceived(String from, byte[] image) {
                SwingUtilities.invokeLater(() -> {
                    receiveImage(image);
                });
            }
            @Override
            public void onImageSendError(String reason) {}
            @Override
            public void onEmojiReceived(String fromUser, int emojiId) {
                SwingUtilities.invokeLater(() -> {
                   receiveEmojiMessageById(emojiId);
                });
            }
            @Override
            public void onRECOVERMessageS_Received(String message, String time) {
                SwingUtilities.invokeLater(() -> {
                   sendMessageRECOVER(message, time);
                });
            }
            @Override
            public void onRECOVERMessageR_Received(String message, String time){
                SwingUtilities.invokeLater(() -> {
                   receiveMessageRECOVER(message, time);
                });
            }


            @Override
            public void onRECOVERImgS_Received(byte[] image,String time){
                SwingUtilities.invokeLater(() -> {
                   sendImageRECOVER(image,time);
                });
            }
            @Override
            public void onRECOVERImgR_Received(byte[] image,String time){
                SwingUtilities.invokeLater(() -> {
                    receiveImageRECOVER(image,time);
                });
            }


            @Override
            public void onRECOVEREmojiS_Received(int emojiId,String time){
                SwingUtilities.invokeLater(() -> {
                   sendEmojiMessageByIdRECOVER(emojiId,time);
                });
            }
            @Override
            public void onRECOVEREmojiR_Received(int emojiId,String time){
                SwingUtilities.invokeLater(() -> {
                    receiveEmojiMessageByIdRECOVER(emojiId,time);
                });
            }

        });
    } // LINKer 생성자 끝

    // 옵션 창 토글
    private void toggleDrawer(JPanel drawerPanel, JPanel overlayPanel) {
        boolean willOpen = !isDrawerOpen; // 현재 상태에 따라 열릴 건지 판단

        int startX = willOpen ? 800 : 600;
        int endX = willOpen ? 600 : 800;

        if (willOpen) {
            overlayPanel.setVisible(true); // 열릴 경우 바로 보여줌
        }

        Animator animator = PropertySetter.createAnimator(
                300,
                drawerPanel,
                "bounds",
                new Rectangle(startX, 0, drawerPanel.getWidth(), drawerPanel.getHeight()),
                new Rectangle(endX, 0, drawerPanel.getWidth(), drawerPanel.getHeight()));

        animator.setAcceleration(0.2f);
        animator.setDeceleration(0.3f);

        animator.addTarget(new TimingTargetAdapter() {
            @Override
            public void end() {
                isDrawerOpen = willOpen; // 애니메이션이 끝난 후에 상태 업데이트

                if (!willOpen) {
                    overlayPanel.setVisible(false); // 닫히는 경우만 끔
                }
            }
        });

        animator.start();
    }

    // 텍스트 메시지 전송 처리

    private void sendMessage() {
        String message = roundedInputField.getText().trim();
        if (!message.isEmpty()) {
            AppContext.getServerConn().sendMessage(userId, message); // 서버 연결코드
            SpeechBubble bubble = new SpeechBubble(message, true, Color.decode("#B3E5FC"));
            String time = new SimpleDateFormat("HH:mm").format(new Date());
            MessageWithTimestamp wrapped = new MessageWithTimestamp(bubble, time, true);
            messagePanel.add(wrapped);
            layoutMessages();
            roundedInputField.setText("");
            roundedInputField.requestFocusInWindow();
        }
    }

    // 텍스트 메세지 수신 처리
    public void receiveMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, false,Color.decode("#EEEEEE"));
            addMessageWithTimestamp(bubble, false, null);
        }
    }

    //텍스트 메세지 상대방 복구용
    public void receiveMessageRECOVER(String message,String Time) {
        if (message != null && !message.trim().isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, false,Color.decode("#EEEEEE"));
            addMessageWithTimestamp(bubble, false, Time);
        }
    }
    //텍스트 메세지 send 복구용
    public void sendMessageRECOVER(String message, String Time) {
        if (message != null && !message.trim().isEmpty()) {
            SpeechBubble bubble = new SpeechBubble(message, true,Color.decode("#B3E5FC"));
            addMessageWithTimestamp(bubble, true, Time);
        }
    }


    // 이미지 전송자편
    private void uploadImage(ImageIcon icon) {
        int maxSize = 400;
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        float scale = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
        scale = Math.min(scale, 1.0f); // 확대 방지

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        SpeechBubble imageBubble = new SpeechBubble(
                scaledIcon,
                true,
                Color.decode("#B3E5FC"),
                () -> ImagePopupViewer.showImagePopup(this, icon));

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        MessageWithTimestamp wrapped = new MessageWithTimestamp(imageBubble, time, true);
        messagePanel.add(wrapped);
        layoutMessages();
    }
    // 이미지 메소드 수신 처리
    public void receiveImage(byte[] imageData) {
        if (imageData == null || imageData.length == 0)
            return;
        try {
            // byte[] -> BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                // 크기 조절
                int maxSize = 300;
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                float scale = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
                scale = Math.min(scale, 1.0f);
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);
                Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                // 말풍선 생성
                SpeechBubble imageBubble = new SpeechBubble(
                        scaledIcon,
                        false, // 왼쪽 정렬 (상대방 메시지)
                        Color.decode("#EEEEEE"),
                        () -> ImagePopupViewer.showImagePopup(this, icon));
                String time = new SimpleDateFormat("HH:mm").format(new Date());
                MessageWithTimestamp wrapped = new MessageWithTimestamp(imageBubble, time, false);
                messagePanel.add(wrapped);
                layoutMessages();
            } else {
                System.err.println("이미지를 불러올 수 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //수신용 이미지 복구용
    public void receiveImageRECOVER(byte[] imageData,String Time) {
        if (imageData == null || imageData.length == 0)
            return;
        try {
            // byte[] -> BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                // 크기 조절
                int maxSize = 300;
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                float scale = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
                scale = Math.min(scale, 1.0f);
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);
                Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                // 말풍선 생성
                SpeechBubble imageBubble = new SpeechBubble(
                        scaledIcon,
                        false, // 왼쪽 정렬 (상대방 메시지)
                        Color.decode("#EEEEEE"),
                        () -> ImagePopupViewer.showImagePopup(this, icon));
                addMessageWithTimestamp(imageBubble, false, Time);
            } else {
                System.err.println("이미지를 불러올 수 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //전송 이미지 복구용
    public void sendImageRECOVER(byte[] imageData, String Time) {
        if (imageData == null || imageData.length == 0)
            return;
        try {
            // byte[] -> BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                // 크기 조절
                int maxSize = 300;
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                float scale = Math.min((float) maxSize / originalWidth, (float) maxSize / originalHeight);
                scale = Math.min(scale, 1.0f);
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);
                Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                // 말풍선 생성
                SpeechBubble imageBubble = new SpeechBubble(
                        scaledIcon,
                        true, // 왼쪽 정렬 (상대방 메시지)
                        Color.decode("#B3E5FC"),
                        () -> ImagePopupViewer.showImagePopup(this, icon));
                addMessageWithTimestamp(imageBubble, true, Time);
            } else {
                System.err.println("이미지를 불러올 수 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 이모지 메시지 전송
    public void sendEmojiMessageById(int emojiId) {
        String emojiIdString = String.valueOf(emojiId);
        AppContext.getServerConn().sendemoji(userId, emojiIdString);
        String path = "/img/" + emojiId + ".svg";
        ImageIcon icon = SvgUtils.resizeSvgIcon(path, 64, 64); // 말풍선용
        ImageIcon popupIcon = SvgUtils.resizeSvgIcon(path, 128, 128); // 팝업용

        SpeechBubble emojiBubble = new SpeechBubble(
                icon,
                true,
                Color.decode("#B3E5FC"),
                () -> ImagePopupViewer.showImagePopup(this, popupIcon));

        addMessageWithTimestamp(emojiBubble, true, null);
    }
    // 이모지 메세지 수신
    public void receiveEmojiMessageById(int emojiId) {
        // 경로 예: /img/1.svg
        String path = "/img/" + emojiId + ".svg";
        // SVG 아이콘 로드 및 리사이즈
        ImageIcon icon = SvgUtils.resizeSvgIcon(path, 64, 64); // 큰 말풍선용
        ImageIcon popupIcon = SvgUtils.resizeSvgIcon(path, 128, 128); // 확대 팝업용
        // 말풍선 생성 (false = 받은 메시지)
        SpeechBubble emojiBubble = new SpeechBubble(
                icon,
                false,
                Color.decode("#EEEEEE"),
                () -> ImagePopupViewer.showImagePopup(this, popupIcon));
        // 시간 표시
        addMessageWithTimestamp(emojiBubble, false, null);
    }
    // 이모지 메세지 전송 복구용
    public void sendEmojiMessageByIdRECOVER(int emojiId,String time) {
        String path = "/img/" + emojiId + ".svg";
        ImageIcon icon = SvgUtils.resizeSvgIcon(path, 64, 64); // 말풍선용
        ImageIcon popupIcon = SvgUtils.resizeSvgIcon(path, 128, 128); // 팝업용

        SpeechBubble emojiBubble = new SpeechBubble(
                icon,
                true,
                Color.decode("#B3E5FC"),
                () -> ImagePopupViewer.showImagePopup(this, popupIcon));

        addMessageWithTimestamp(emojiBubble, true, time);
    }
    // 이모지 메세지 수신 복구용
    public void receiveEmojiMessageByIdRECOVER(int emojiId,String time) {
        String path = "/img/" + emojiId + ".svg";
        ImageIcon icon = SvgUtils.resizeSvgIcon(path, 64, 64); // 말풍선용
        ImageIcon popupIcon = SvgUtils.resizeSvgIcon(path, 128, 128); // 팝업용

        SpeechBubble emojiBubble = new SpeechBubble(
                icon,
                false,
                Color.decode("#EEEEEE"),
                () -> ImagePopupViewer.showImagePopup(this, popupIcon));

        addMessageWithTimestamp(emojiBubble, false, time);
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

            // 스크롤 바 맨 아래로 갱신
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
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
//     상대방 텍스트 메시지 수신 (임시)


    public void addMessageWithTimestamp(JComponent bubble, boolean isSenderMe, String msgTimeStamp) {
        long timestamp;
        if (msgTimeStamp == null) {
            timestamp = System.currentTimeMillis();
        } else {
            try {
                // 서버에서 받은 문자열 포맷에 맞게 SimpleDateFormat 객체 생성
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date parsedDate = sdf.parse(msgTimeStamp); // 문자열 → Date
                timestamp = parsedDate.getTime(); // Date → long (밀리초)
            } catch (Exception e) {
                e.printStackTrace();
                timestamp = System.currentTimeMillis(); // 파싱 실패 시 현재 시간 사용
            }
        }

        String time = new SimpleDateFormat("HH:mm").format(new Date(timestamp));
        MessageWithTimestamp wrapped = new MessageWithTimestamp(bubble, time, isSenderMe);
        messagePanel.add(wrapped);
        layoutMessages();
    }
}