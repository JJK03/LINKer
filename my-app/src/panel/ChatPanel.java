package panel;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    public ChatPanel() {
        setLayout(null);
        setBackground(new Color(0, 0, 0));

        JLabel label = new JLabel("채팅 목록");
        label.setBounds(30, 30, 500, 20);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(label);
    }
}
