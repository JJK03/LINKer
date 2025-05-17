package project;

import javax.swing.*;
import java.awt.*;

public class SpeechBubble extends JPanel {
    private final JTextArea textArea;
    private final boolean isRight;
    private static final int MAX_WIDTH = 200;

    public SpeechBubble(String text, boolean isRight) {
        this.isRight = isRight;
        setOpaque(false);
        setLayout(new BorderLayout());

        textArea = new JTextArea(text);
        textArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        textArea.setForeground(Color.BLACK);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 12)); // top, left, bottom, right

        add(textArea, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        textArea.setSize(new Dimension(MAX_WIDTH, Short.MAX_VALUE));
        Dimension d = textArea.getPreferredSize();

        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int maxLineWidth = 0;
        for (String line : textArea.getText().split("\n")) {
            int lineWidth = fm.stringWidth(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        int paddingHorizontal = 24; // 12 + 12 padding
        int extraMargin = 8;        // 말풍선 끝 글자 여백

        int width = Math.min(Math.max(maxLineWidth + paddingHorizontal + extraMargin, 40), MAX_WIDTH + paddingHorizontal);

        textArea.setSize(width, Short.MAX_VALUE);

        int height = textArea.getPreferredSize().height;

        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 16;
        int tailW = 13;  // 꼬리 너비 좀 키움
        int tailH = 13;  // 꼬리 높이

        int bubbleX = isRight ? 0 : tailW;
        int bubbleY = 0;
        int bubbleW = getWidth() - tailW;
        int bubbleH = getHeight() - 4;

        // 말풍선 본체
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, arc, arc);

        // 꼬리 그리기 - 오른쪽 아래 모서리 부근
        if (isRight) {
            // 꼬리를 둥근 삼각형으로 오른쪽 아래 코너에 붙이기
            int baseX = bubbleX + bubbleW - arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX + tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);

            g2.fillPolygon(tail);

            // 둥근 곡선으로 꼬리 연결 (optional)
            g2.setColor(Color.WHITE);
            g2.fillOval(baseX - tailW / 2, baseY + tailH / 2 - tailW / 2, tailW, tailW);

        } else {
            // 왼쪽 아래 모서리 꼬리도 원한다면 여기에 비슷하게 구현 가능
            int baseX = bubbleX + arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX - tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);

            g2.fillPolygon(tail);

            g2.setColor(Color.WHITE);
            g2.fillOval(baseX - tailW / 2 - tailW / 2, baseY + tailH / 2 - tailW / 2, tailW, tailW);
        }

        g2.dispose();
    }
}
