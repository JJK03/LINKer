package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpeechBubble extends JPanel {
    private final boolean isRight;
    private static final int MAX_WIDTH = 200;
    private Color bubbleColor;

    private JTextArea textArea;
    private JLabel imageLabel;
    private JLabel timeLabel; // 타임스탬프용 라벨
    // private Runnable onImageClick;

    // 타임스탬프 저장용 필드
    private String timestamp;

    // 텍스트용 생성자
    public SpeechBubble(String text, boolean isRight, Color bubbleColor) {
        this.isRight = isRight;
        this.bubbleColor = bubbleColor;
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
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 13));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(textArea, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    // 이미지용 생성자
    public SpeechBubble(ImageIcon icon, boolean isRight, Color bubbleColor, Runnable onImageClick) {
        this.isRight = isRight;
        this.bubbleColor = bubbleColor;
        // this.onImageClick = onImageClick;
        this.timestamp = getCurrentTime();

        setOpaque(false);
        setLayout(new BorderLayout());

        imageLabel = new JLabel(icon);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
        timeLabel = new JLabel(timestamp);
        timeLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 10));
        timeLabel.setForeground(Color.DARK_GRAY);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    @Override
    public Dimension getPreferredSize() {
        if (textArea != null) {
            textArea.setSize(new Dimension(MAX_WIDTH, Short.MAX_VALUE));
            // Dimension d = textArea.getPreferredSize();

            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
            int maxLineWidth = 0;
            for (String line : textArea.getText().split("\n")) {
                int lineWidth = fm.stringWidth(line);
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
            }

            int paddingHorizontal = 24;
            int extraMargin = 8;
            int width = Math.min(Math.max(maxLineWidth + paddingHorizontal + extraMargin, 40), MAX_WIDTH + paddingHorizontal);
            textArea.setSize(width, Short.MAX_VALUE);
            int height = textArea.getPreferredSize().height;

            if (timeLabel != null) {
                height += timeLabel.getPreferredSize().height;
            }

            return new Dimension(width, height);
        } else if (imageLabel != null) {
            Dimension size = imageLabel.getPreferredSize();
            int height = size.height + 20;
            int width = size.width + 24;

            if (timeLabel != null) {
                height += timeLabel.getPreferredSize().height;
            }

            return new Dimension(width, height);
        }
        return super.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
            g2.fillOval(baseX - tailW / 2, baseY + tailH / 2 - tailW / 2, tailW, tailW);
        } else {
            int baseX = bubbleX + arc / 2;
            int baseY = bubbleY + bubbleH - tailH;

            Polygon tail = new Polygon();
            tail.addPoint(baseX, baseY);
            tail.addPoint(baseX - tailW, baseY + tailH / 2);
            tail.addPoint(baseX, baseY + tailH);
            g2.fillPolygon(tail);
            g2.fillOval(baseX - tailW / 2 - tailW / 2, baseY + tailH / 2 - tailW / 2, tailW, tailW);
        }

        g2.dispose();
    }
}
