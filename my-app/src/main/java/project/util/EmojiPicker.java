package project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EmojiPicker extends JPopupMenu {
    public EmojiPicker(ActionListener onEmojiSelected) {
        String[] emojis = {"😊", "😂", "❤️", "😍", "🤣", "😭", "🙏", "🤔", "😎", "😡"};

        JPanel panel = new JPanel(new GridLayout(2, 5, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(60, 63, 65));

        for (String emoji : emojis) {
            JButton btn = new JButton(emoji);
            btn.setFont(new Font("Noto Sans CJK KR", Font.PLAIN, 24)); // 폰트 설정
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setForeground(Color.WHITE);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setActionCommand(emoji);
            btn.addActionListener(onEmojiSelected);
            panel.add(btn);
        }

        add(panel);
    }
}