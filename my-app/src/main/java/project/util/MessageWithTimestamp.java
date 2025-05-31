package project.util;

import javax.swing.*;
import java.awt.*;

public class MessageWithTimestamp extends JPanel {
    private final boolean isRight;  // 메시지가 오른쪽(내 메시지)인지 저장

    public MessageWithTimestamp(Component bubble, String time, boolean isRight) {
        this.isRight = isRight;
        setLayout(new GridBagLayout());
        setOpaque(false); // 투명

        // 시간 표시 라벨
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setForeground(Color.GRAY);

        GridBagConstraints gbc = new GridBagConstraints();

        if (isRight) {
            // 오른쪽 말풍선일 때
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(bubble, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(-15, 0, 0, 5);
            add(timeLabel, gbc);

        } else {
            // 왼쪽 말풍선일 때
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(0, 0, 0, 0);
            add(bubble, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.insets = new Insets(-15, 5, 0, 0);
            add(timeLabel, gbc);
        }
    }

    // isRight 값을 가져오는 getter
    public boolean isMine() {
        return isRight;
    }
}