package project;

import java.awt.*;
import javax.swing.*;

public class RoundedTextField extends JTextField {

    private String placeholder;

    public RoundedTextField(int size) {
        super(size);
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 0));  // 완전 투명 배경
        setForeground(Color.BLACK);  // 글씨 색깔 진하게
        setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 배경 그리기 (반투명 흰색)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        g2.dispose();

        super.paintComponent(g);

        // 텍스트 없고 포커스도 없을 때 placeholder 표시
        if (placeholder != null && getText().isEmpty()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setColor(new Color(150, 150, 150));  // 연한 회색
            Insets insets = getInsets();
            FontMetrics fm = g3.getFontMetrics();
            int x = insets.left;
            int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
            g3.drawString(placeholder, insets.left + 5, getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 2);
            g3.dispose();
        }
    }
}
