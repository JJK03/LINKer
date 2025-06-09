package project.util;

import java.awt.*;
import javax.swing.*;

public class RoundedTextField extends JTextPane {
    private String placeholder;

    public RoundedTextField(int i) {
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 0));
        setForeground(Color.BLACK);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    // 텍스트를 입력했다가 취소해도 다시 플레이스 홀더 생성
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그리기
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        g2.dispose();

        super.paintComponent(g);

        // Placeholder 항상 표시 조건
        if (placeholder != null && getText().isEmpty()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setColor(new Color(24, 26, 30)); // 플레이스 홀더 색깔
            Insets insets = getInsets();
            FontMetrics fm = g3.getFontMetrics();
            int x = insets.left;
            int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
            g3.drawString(placeholder, x, y);
            g3.dispose();
        }
    }
}