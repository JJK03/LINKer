package project.util;

import javax.swing.*;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class DrawerPanel extends RoundedPanel {

    public DrawerPanel(int width, int height) {
        super(30); // 둥근 정도
        setLayout(null);
        setBackground(new Color(24, 26, 30)); // 옵션 창 색깔
        setBounds(800, 0, width, height); // 초기엔 오른쪽 화면 밖
        setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2, true));
        initializeComponents();
    }

    // 옵션 내부 버튼들 보낸 사진, 파일들, 채팅방 나가기, 신고하기
    private void initializeComponents() {
        // 1. 버튼 생성
        JButton sharedButton = new JButton("사진/동영상");
        sharedButton.setBounds(20, 50, 160, 30);
        sharedButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(sharedButton);

        JButton fileButton = new JButton("파일");
        fileButton.setBounds(20, 100, 160, 30);
        fileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(fileButton);

        JButton themelogoutButton = new JButton("테마 변경");
        themelogoutButton.setBounds(20, 150, 160, 30);
        themelogoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(themelogoutButton);

        JButton exitButton = new JButton("채팅방 나가기");
        exitButton.setBounds(20, 480, 160, 30);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(exitButton);

        JButton reportButton = new JButton("신고하기");
        reportButton.setForeground(Color.RED);
        reportButton.setBounds(20, 530, 160, 30);
        reportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(reportButton);

        // 2. 리스트에 버튼들 추가
        List<JComponent> buttons = new ArrayList<>();
        buttons.add(sharedButton);
        buttons.add(fileButton);
        buttons.add(themelogoutButton);
        buttons.add(exitButton);
        buttons.add(reportButton);
    }

    // 옵션 창
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 30; // 둥근 정도
        int width = getWidth();
        int height = getHeight();

        // 왼쪽 위, 왼쪽 아래만 둥근 직사각형 모양 만들기
        Shape shape = createLeftRoundedRect(0, 0, width, height, arc);

        g2.setClip(shape);
        g2.setColor(getBackground());
        g2.fill(shape);

        g2.dispose();
    }

    // 옵션 창 테두리
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 30;
        int width = getWidth();
        int height = getHeight();

        Shape shape = createLeftRoundedRect(0, 0, width - 1, height - 1, arc);

        g2.setColor(new Color(70, 70, 70)); // 테두리 색상
        g2.setStroke(new BasicStroke(2f));
        g2.draw(shape);

        g2.dispose();
    }

    // 왼쪽 위, 왼쪽 아래만 둥근 직사각형을 만드는 메서드
    private Shape createLeftRoundedRect(int x, int y, int w, int h, int arc) {
        int r = arc;

        Path2D path = new Path2D.Double();
        path.moveTo(x + r, y); // 왼쪽 위 곡률 시작점
        path.quadTo(x, y, x, y + r); // 왼쪽 위 곡률
        path.lineTo(x, y + h - r); // 왼쪽 아래 곡률 시작점
        path.quadTo(x, y + h, x + r, y + h); // 왼쪽 아래 곡률
        path.lineTo(x + w, y + h); // 아래쪽 직선
        path.lineTo(x + w, y); // 위쪽 직선
        path.closePath();
        return path;
    }
}