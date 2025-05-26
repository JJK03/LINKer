package project.util;

import javax.swing.*;
import java.awt.*;

public class ImageBubble extends JPanel {
    private final boolean isRight;
    private final Color bubbleColor;
    private final ImageIcon image;

    public ImageBubble(ImageIcon image, boolean isRight, Color bubbleColor) {
        this.isRight = isRight;
        this.bubbleColor = bubbleColor;
        this.image = image;
        setOpaque(false); // 투명 처리
        setLayout(new BorderLayout());

        // 이미지 표시용 라벨
        JLabel imageLabel = new JLabel(image);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); // 여백
        add(imageLabel, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        int width = image.getIconWidth() + 20;
        int height = image.getIconHeight() + 16;
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 부드러운 곡선 만들기
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 말풍선 둥근 모서리와 꼬리 크기 설정, 위치 계산
        int arc = 16;
        int tailW = 13;
        int tailH = 13;

        int bubbleX = isRight ? 0 : tailW;
        int bubbleY = 0;
        int bubbleW = getWidth() - tailW;
        int bubbleH = getHeight() - 4;

        g2.setColor(bubbleColor);
        g2.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, arc, arc);

        if (isRight) { // 오른쪽 메시지 (나)
            int baseX = bubbleX + bubbleW - arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX + tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);
            g2.fillPolygon(tail);
            g2.fillOval(baseX - tailW / 2, baseY + tailH / 2 - tailW / 2, tailW, tailW);

        } else { // 상대방 메시지 (왼쪽 정렬)
            int baseX = bubbleX + arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX - tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);
            g2.fillPolygon(tail);
            g2.fillOval(baseX - tailW, baseY + tailH / 2 - tailW / 2, tailW, tailW);
        }

        g2.dispose();
    }
}