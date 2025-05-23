package project;

import javax.swing.*;
import java.awt.*;

public class MessageWithTimestamp extends JPanel {
    public MessageWithTimestamp(Component bubble, String time, boolean isRight) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
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
            gbc.insets = new Insets(-15, 0, 0, 5);  // 여기서 top 마진을 2로 줄임 (살짝 위로)
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
            gbc.insets = new Insets(2, 5, 0, 0);  // 마찬가지로 top 마진 조정
            add(timeLabel, gbc);
        }
    }
}
