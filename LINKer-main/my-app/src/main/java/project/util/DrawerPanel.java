package project.util;

import javax.swing.*;
import javax.swing.border.Border;

import project.util.button_in_option.MapChoiceDialog;
import project.util.button_in_option.ScheduleListDialog;

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

    // 옵션 내부 버튼들 일정, 지도
    private void initializeComponents() {

        JButton planButton = createStyledButton("일정");
        planButton.setBounds(20, 50, 160, 30);
        add(planButton);
        // 일정 버튼 클릭 시 일정창 등장 메서드
        planButton.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(planButton);
            ScheduleListDialog dialog = new ScheduleListDialog((JFrame) parentWindow);
            dialog.setVisible(true);
        });

        JButton mapButton = createStyledButton("지도");
        mapButton.setBounds(20, 100, 160, 30);
        add(mapButton);
        // 지도 버튼 클릭 시 지도 프로그램 선택 창 등장 메서드
        mapButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(mapButton);

            MapChoiceDialog dialog = new MapChoiceDialog(parentFrame, this::createStyledButton);
            dialog.setVisible(true);
        });
        // 버튼 리스트
        List<JComponent> buttons = new ArrayList<>();
        buttons.add(planButton);
        buttons.add(mapButton);
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

    // 버튼 스타일
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 배경색 채우기 (라운드)
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 라운드 테두리 색
                int arc = 12; // 버튼 곡률
                g2.setColor(new Color(60, 60, 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2.dispose();
            }
        };
        button.setFont(new Font("SansSerif", Font.PLAIN, 15));
        button.setForeground(new Color(230, 230, 230));
        button.setBackground(new Color(40, 42, 48));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 호버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 174, 255));
                button.setFont(new Font("SansSerif", Font.BOLD, 15));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 42, 48));
                button.setFont(new Font("SansSerif", Font.PLAIN, 15));
            }
        });

        return button;
    }

    // 둥근 버튼
    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(60, 60, 60));
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}