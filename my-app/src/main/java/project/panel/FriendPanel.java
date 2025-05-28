package project.panel;

import project.login.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FriendPanel extends JPanel {

    public FriendPanel(List<String> friends) {
        setLayout(null);
        setBackground(new Color(0, 0, 0));

        JLabel label = new JLabel("친구 목록");
        label.setBounds(30, 20, 200, 30);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(label);

        int y = 70;
        for (String friend : friends) {
            RoundedPanel panel = new RoundedPanel(15);
            panel.setLayout(new BorderLayout());
            panel.setBounds(30, y, 700, 45);
            panel.setBackground(new Color(45, 45, 47));

            JLabel nameLabel = new JLabel(friend);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            JLabel arrow = new JLabel(">");
            arrow.setForeground(Color.GRAY);
            arrow.setFont(new Font("SansSerif", Font.BOLD, 15));
            arrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            arrow.setHorizontalAlignment(SwingConstants.RIGHT);

            panel.add(nameLabel, BorderLayout.WEST);
            panel.add(arrow, BorderLayout.EAST);

            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    panel.setBackground(new Color(45, 45, 47));
                }

                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    JOptionPane.showMessageDialog(FriendPanel.this, friend + "님과의 채팅창으로 이동합니다");
                }
            });

            add(panel);
            y += 55;
        }
    }
}
