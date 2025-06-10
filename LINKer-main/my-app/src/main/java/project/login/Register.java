package project.login;

import project.Network.LoginListener;
import project.Network.ServerConn;
import project.core.AppContext;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class Register extends JFrame {

    private int mouseX, mouseY;

    public Register() {
        setTitle("회원가입");
        setSize(786, 456);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        setBackground(new Color(0, 0, 0));

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2d.dispose();
            }
        };

        panel.setLayout(null);
        panel.setOpaque(false);
        setContentPane(panel);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(469, 280, 249, 176);
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\준하\\Desktop\\tttt.png"));
        panel.add(lblNewLabel);

        // 창 드래그 가능
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
            }
        });

        JLabel title = new JLabel("계정 만들기");
        title.setBounds(30, 27, 300, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        panel.add(title);

        JLabel nameLabel = new JLabel("사용자 이름");
        nameLabel.setBounds(30, 69, 300, 20);
        nameLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(30, 88, 289, 40);
        nameField.setBackground(new Color(45, 45, 50));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(new RoundedBorder(20));
        panel.add(nameField);

        JLabel emailLabel = new JLabel("이메일 주소");
        emailLabel.setBounds(30, 138, 300, 20);
        emailLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(30, 157, 289, 40);
        emailField.setBackground(new Color(45, 45, 50));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(new RoundedBorder(20));
        panel.add(emailField);

        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setBounds(30, 207, 300, 20);
        pwLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(pwLabel);

        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(30, 226, 289, 40);
        pwField.setBackground(new Color(45, 45, 50));
        pwField.setForeground(Color.WHITE);
        pwField.setCaretColor(Color.WHITE);
        pwField.setBorder(new RoundedBorder(20));
        panel.add(pwField);

        JLabel confirmPwLabel = new JLabel("비밀번호 확인");
        confirmPwLabel.setBounds(30, 276, 300, 20);
        confirmPwLabel.setForeground(Color.LIGHT_GRAY);
        panel.add(confirmPwLabel);

        JPasswordField confirmPwField = new JPasswordField();
        confirmPwField.setBounds(30, 298, 289, 40);
        confirmPwField.setBackground(new Color(45, 45, 50));
        confirmPwField.setForeground(Color.WHITE);
        confirmPwField.setCaretColor(Color.WHITE);
        confirmPwField.setBorder(new RoundedBorder(20));
        panel.add(confirmPwField);

        RoundedButton registerBtn = new RoundedButton("회원가입", 30);
        registerBtn.setBounds(30, 362, 316, 45);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());
        panel.add(registerBtn);

        // 뒤로가기 버튼 추가
        RoundedButton backBtn = new RoundedButton("뒤로가기", 30);
        backBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        backBtn.setBackground(new Color(0, 0, 0));
        backBtn.setBounds(-14, 407, 132, 30);
        backBtn.setBorder(BorderFactory.createEmptyBorder());
        panel.add(backBtn);

        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(740, 10, 30, 30);
        closeBtn.setBackground(new Color(55, 60, 68));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        panel.add(closeBtn);
        
                // 이미지 라벨 먼저 추가 (뒤로가기 버튼을 가리지 않게 하기 위해)
                JLabel lblNewLabel_1 = new JLabel("");
                lblNewLabel_1.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/qqqq.png"))));

                lblNewLabel_1.setBounds(226, 29, 585, 368);
                panel.add(lblNewLabel_1);

        // 회원가입 버튼 동작
        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String pw = new String(pwField.getPassword());
            String pwConfirm = new String(confirmPwField.getPassword());

            if (name.isEmpty() || email.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty()) {
                showCustomDialog("모든 필드를 입력하세요.");
            } else if (!pw.equals(pwConfirm)) {
                showCustomDialog("비밀번호가 일치하지 않습니다.");
            } else {
                ServerConn conn = AppContext.getServerConn();
                conn.signUp(email, pw, name);
            }
        });

        AppContext.getServerConn().setLoginListener(new LoginListener() {

            @Override
            public void onSignUpSuccess(String userId) {
                SwingUtilities.invokeLater(() -> {
                    showCustomDialog("회원가입 성공. 로그인 해주세요.");
                    dispose();
                    new Login();
                });
            }

            @Override
            public void onSignUpFailure(String reason) {
                SwingUtilities.invokeLater(() -> {
                    showCustomDialog(reason);
                });
            }


            // 필요 없는 로그인 콜백.
            @Override
            public void onLoginSuccess(String userId) {}
            @Override
            public void onLoginFailure(String reason) {}



        });

        // 뒤로가기 버튼 동작
        backBtn.addActionListener(e -> {
            dispose();
            new Login(); // 로그인 창 호출
        });

        setVisible(true);
    }
    private void showCustomDialog(String message) {
        showCustomDialog(message, null);
    }

    // 커스텀 다이얼로그: 확인 후 동작도 설정 가능
    private void showCustomDialog(String message, Runnable onClose) {
        JDialog dialog = new JDialog(this, "알림", true);
        dialog.setUndecorated(true);//기본 타이틀 위에 그거 제거한거
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);// 프로그램 중앙 위치함
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 300, 150, 30, 30)); // 둥근 모서리 설정

        // 메시지 라벨
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;'>" + message.replaceAll("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        msgLabel.setBounds(20, 20, 260, 50);
        dialog.add(msgLabel);

        // 확인 버튼
        JButton okButton = new JButton("확인");
        okButton.setBounds(100, 90, 100, 35);
        okButton.setFocusPainted(false);
        okButton.setBackground(new Color(0, 174, 255));
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder());
        okButton.setFont(new Font("Dialog", Font.BOLD, 14));
        okButton.addActionListener(e -> {
            dialog.dispose(); // 다이얼로그 닫기
            if (onClose != null) onClose.run(); // 닫기 후 실행할 작업
        });
        dialog.add(okButton);

        dialog.setVisible(true); // 다이얼로그 표시
    }

    // 커스텀 둥근 테두리 클래스
    class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(80, 80, 85));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 14, 10, 14);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(10, 14, 10, 14);
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Register::new);
    }
}
