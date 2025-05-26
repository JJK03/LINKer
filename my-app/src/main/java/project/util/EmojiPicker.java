package project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

public class EmojiPicker extends JPopupMenu {

    public EmojiPicker(Consumer<String> onEmojiSelected) {
        setLayout(new GridLayout(4, 6, 5, 5)); // 4줄 x 5칸, 간격 5픽셀
        setBackground(new Color(40, 40, 40)); // 팝업창 배경 (회색)
        setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 팝업창 외곽선 (회색)

        List<String> emojiPaths = List.of( // 이모지 리스트
            "/img/Smile.svg",
            "/img/Tears_of_Joy.svg",
            "/img/Heart.svg",
            "/img/Victory.svg",
            "/img/Thumbs_Up.svg",
            "/img/Loudly_Crying.svg",
            "/img/Folded_Hands.svg",
            "/img/Throwing_a_Kiss.svg",
            "/img/Smiling_Three_Hearts.svg",
            "/img/Heart_Shaped_Eyes.svg",
            "/img/Party_Popper.svg",
            "/img/Clapping_Hands.svg",
            "/img/Smiling_with_Sunglasses.svg",
            "/img/Pleading_Eyes.svg",
            "/img/Smiling_with_Cold_Sweat.svg",
            "/img/Fire.svg",
            "/img/OK.svg",
            "/img/Biceps.svg",
            "/img/Skull.svg",
            "/img/Check_Mark.svg"
        );

        for (String path : emojiPaths) {
            ImageIcon icon = SvgUtils.loadSvgIcon(path, 30, 30); // 이모지 크기

            // 이모지 버튼
            JButton button = new JButton(icon);
            button.setPreferredSize(new Dimension(30, 30));
            button.setContentAreaFilled(false); // 배경 false
            button.setBorderPainted(false); // 테두리 false
            button.setFocusPainted(false); // 포커스 테두리 false
            button.setOpaque(false); // 투명 처리
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                onEmojiSelected.accept(path);
                setVisible(false); // 선택 후 닫기
            });

            add(button);
        }
    }
}
