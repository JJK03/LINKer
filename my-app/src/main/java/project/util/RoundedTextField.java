package project.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class RoundedTextField extends JTextPane {
    private String placeholder;

    public RoundedTextField(int columns) {
        super();
        setOpaque(false);
        setFont(new Font("Noto Sans CJK KR", Font.PLAIN, 14));
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // 텍스트 변경 시 리페인트해서 플레이스홀더 상태 갱신
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                repaint();
            }
        });

        // 포커스 변화시 플레이스홀더 표시 갱신
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(180, 180, 180));
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = insets.left + 2;
            int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}
