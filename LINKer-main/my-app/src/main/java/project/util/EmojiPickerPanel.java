package project.util;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class EmojiPickerPanel extends JPanel {

    public EmojiPickerPanel(List<Integer> emojiIds, Consumer<Integer> onEmojiSelected) {
        setLayout(new GridLayout(4, 5, 3, 3)); // 4행 5열, 간격 3px
        setBackground(new Color(24, 26, 30));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        for (int emojiId : emojiIds) {
            String path = "/img/" + emojiId + ".svg";
            ImageIcon emojiIcon = SvgUtils.resizeSvgIcon(path, 22, 22); // 작은 사이즈로 로딩

            JButton btn = new JButton(emojiIcon);
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> onEmojiSelected.accept(emojiId));

            add(btn);
        }
    }
}