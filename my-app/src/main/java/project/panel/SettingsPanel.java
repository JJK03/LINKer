package project.panel;

import project.login.Login;
import project.login.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SettingsPanel extends JPanel {
    public SettingsPanel(String userEmail, Consumer<String> showTextOverlay) {
        setLayout(null);
        setBackground(new Color(0, 0, 0));

        JLabel titleLabel = new JLabel("내 프로필");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(12, 10, 200, 30);
        add(titleLabel);

        JLabel profilePicLabel = new JLabel(new ImageIcon("C:\\Users\\준하\\Desktop\\profile.png"));
        profilePicLabel.setBounds(12, 50, 80, 80);
        add(profilePicLabel);

        JLabel myAccountLabel = new JLabel("이메일: " + userEmail);
        myAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        myAccountLabel.setForeground(Color.LIGHT_GRAY);
        myAccountLabel.setBounds(115, 61, 300, 20);
        add(myAccountLabel);

        JLabel linkerIdLabel = new JLabel("LINKer ID: linker_" + userEmail.hashCode());
        linkerIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        linkerIdLabel.setForeground(Color.LIGHT_GRAY);
        linkerIdLabel.setBounds(115, 94, 300, 20);
        add(linkerIdLabel);

        String[] items = {"내 계정", "보안", "배경화면", "로그아웃"};
        int y = 150;

        for (String item : items) {
            RoundedPanel panel = new RoundedPanel(15);
            panel.setLayout(new BorderLayout());
            panel.setBounds(20, y, 700, 45);
            panel.setBackground(new Color(45, 45, 47));

            JLabel label = new JLabel(item);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.PLAIN, 15));
            label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            JLabel arrow = new JLabel(">");
            arrow.setForeground(Color.GRAY);
            arrow.setFont(new Font("SansSerif", Font.BOLD, 15));
            arrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            arrow.setHorizontalAlignment(SwingConstants.RIGHT);

            panel.add(label, BorderLayout.WEST);
            panel.add(arrow, BorderLayout.EAST);

            panel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (item.equals("로그아웃")) {
                        SwingUtilities.getWindowAncestor(SettingsPanel.this).dispose();
                        new Login();
                    } else {
                        if (showTextOverlay != null) {
                            showTextOverlay.accept(item + " 구현중");
                        }
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                public void mouseExited(MouseEvent e) {
                    panel.setBackground(new Color(45, 45, 47));
                }
            });

            add(panel);
            y += 50;
        }
    }
}
