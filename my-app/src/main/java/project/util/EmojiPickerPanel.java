package project.util;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmojiPickerPanel extends JPanel {
    public interface EmojiClickListener {
        void onEmojiClicked(ImageIcon emoji);
    }

    private EmojiClickListener listener;

    public EmojiPickerPanel(List<ImageIcon> emojis, EmojiClickListener listener) {
        this.listener = listener;
        setLayout(new GridLayout(4, 5, 3, 3)); // 4행 5열, 간격 5px

        setBackground(new Color(24, 26, 30));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        for (ImageIcon emojiIcon : emojis) {
            JButton btn = new JButton(emojiIcon);
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                if (this.listener != null) {
                    this.listener.onEmojiClicked(emojiIcon);
                }
            });

            add(btn);
        }
    }
}
