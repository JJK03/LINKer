package project.util;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpeechBubble extends JPanel {
    private final boolean isRight;
    private static final int MAX_WIDTH = 200;
    private Color bubbleColor;

    private JTextArea textArea;
    private JLabel imageLabel;
    private JLabel timeLabel; // 타임스탬프용 라벨

    // 텍스트용 말풍선 생성자
    public SpeechBubble(String text, boolean isRight, Color bubbleColor) {
        this.isRight = isRight;
        this.bubbleColor = bubbleColor;
        setOpaque(false);
        setLayout(new BorderLayout());

        textArea = new JTextArea(text);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setForeground(Color.BLACK);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(true);
        textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        // 텍스트 말풍선 내의 캐럿
        textArea.setCaret(new DefaultCaret() {
            @Override
            public void setSelectionVisible(boolean visible) {
                super.setSelectionVisible(visible); // 선택은 보이게 유지
            }

            @Override
            public void setVisible(boolean visible) {
                // 커서 자체는 절대 보이지 않도록 무시
            }
        });

        // 말풍선과 텍스트간 내부 여백
        if (isRight)
            textArea.setBorder(BorderFactory.createEmptyBorder(4, 9, 8, 14)); // 내 말풍선
        else
            textArea.setBorder(BorderFactory.createEmptyBorder(4, 20, 8, 7)); // 상대방 말풍선

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(textArea, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    // 이미지용 말풍선 생성자
    // 이미지용 말풍선 생성자
    public SpeechBubble(ImageIcon icon, boolean isRight, Color bubbleColor, Runnable onImageClick) {
        this.isRight = isRight;
        this.bubbleColor = bubbleColor;

        setOpaque(false);
        setLayout(new BorderLayout());

        imageLabel = new JLabel(icon);
        if(isRight)
            imageLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 14)); // 내 이미지 말풍선
        else
            imageLabel.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 4)); // 상대방 이미지 말풍선

        imageLabel.setOpaque(false);
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onImageClick != null) {
                    onImageClick.run();
                }
            }
        });

        // 타임스탬프 라벨 생성
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(imageLabel, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        if (textArea != null) {
            textArea.setSize(new Dimension(MAX_WIDTH, Short.MAX_VALUE));

            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
            int maxLineWidth = 0;
            for (String line : textArea.getText().split("\n")) {
                int lineWidth = fm.stringWidth(line);
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
            }

            int paddingHorizontal = 24;
            int extraMargin = 8;
            int width = Math.min(Math.max(maxLineWidth + paddingHorizontal + extraMargin, 40),
                    MAX_WIDTH + paddingHorizontal);
            textArea.setSize(width, Short.MAX_VALUE);
            int height = textArea.getPreferredSize().height;

            return new Dimension(width, height);
        } else if (imageLabel != null) {
            Dimension size = imageLabel.getPreferredSize();
            int height = size.height + 20;
            int width = size.width + 20;

            if (timeLabel != null) {
                height += timeLabel.getPreferredSize().height;
            }

            return new Dimension(width, height);
        }
        return super.getPreferredSize();
    }

    // 말풍선 배경 및 꼬리 그리기
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 16;
        int tailW = 13;
        int tailH = 13;

        int bubbleX = isRight ? 0 : tailW;
        int bubbleY = 0;
        int bubbleW = getWidth() - tailW;
        int bubbleH = getHeight() - 4;

        g2.setColor(bubbleColor);
        g2.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, arc, arc);

        if (isRight) {
            int baseX = bubbleX + bubbleW - arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX + tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);
            g2.fillPolygon(tail);
        } else {
            int baseX = bubbleX + arc / 2 - 1; // 약간 오른쪽으로 여유 공간 확보
            int baseY = bubbleY + bubbleH - arc / 2 - 6; // 말풍선 하단

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX - tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);
            g2.fillPolygon(tail);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}