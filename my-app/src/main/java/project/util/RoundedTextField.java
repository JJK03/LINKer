package project.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class RoundedTextField extends JTextPane {
    private String placeholder;

    public RoundedTextField(int i) {
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 0));
        setForeground(Color.BLACK);
        setFont(new Font("Noto Sans CJK KR", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    // 텍스트를 입력했다가 취소해도 다시 플레이스 홀더 생성성
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 배경 그리기
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        g2.dispose();

        super.paintComponent(g);

        // Placeholder
        if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setColor(new Color(200, 200, 200));
            Insets insets = getInsets();
            g3.drawString(placeholder, insets.left + 5, getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 4);
            g3.dispose();
        }
    }

    // 이모지 공사중...
    public void insertEmojiIcon(ImageIcon emojiIcon) {
        StyledDocument doc = getStyledDocument();
        Style style = doc.addStyle("emoji", null);
        StyleConstants.setIcon(style, emojiIcon);

        try {
            doc.insertString(getCaretPosition(), " ", null); // 공백 먼저
            doc.insertString(getCaretPosition(), " ", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}