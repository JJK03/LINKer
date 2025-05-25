package project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EmojiPicker extends JPopupMenu {

    public EmojiPicker(ActionListener emojiSelectListener) {
        String[] emojis = {
            "😀", "😂", "😍", "😎", "😢", "😡", "👍", "🙏", "🎉", "❤️", "🔥", "🤔", "💡", "💯", "🥹"
        };

        for (String emoji : emojis) {
            JMenuItem item = new JMenuItem(emoji);
            item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            item.addActionListener(emojiSelectListener); // 선택 이벤트 위임
            add(item);
        }
    }

    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
    }
}