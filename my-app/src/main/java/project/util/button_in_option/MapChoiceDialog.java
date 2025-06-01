package project.util.button_in_option;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class MapChoiceDialog extends JDialog {

    public MapChoiceDialog(JFrame parent, java.util.function.Function<String, JButton> buttonFactory) {
        super(parent, "지도 선택", true);
        setLayout(new GridBagLayout());
        setSize(320, 160);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 버튼 생성
        JButton naverButton = buttonFactory.apply("네이버 지도");
        naverButton.addActionListener((ActionEvent e) -> {
            openWebpage("https://map.naver.com");
            dispose();
        });

        JButton googleButton = buttonFactory.apply("  구글 지도  ");
        googleButton.addActionListener((ActionEvent e) -> {
            openWebpage("https://maps.google.com");
            dispose();
        });

        add(naverButton, gbc);
        gbc.gridy++;
        add(googleButton, gbc);
    }

    private void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "웹 브라우저를 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
