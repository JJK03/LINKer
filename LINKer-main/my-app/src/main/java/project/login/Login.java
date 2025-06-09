package project.login;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import java.util.Stack;

import project.Network.*;
import project.core.AppContext;

class RoundedBorder extends AbstractBorder {
    private final int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }
}

class RoundedButton extends JButton {
    private final int radius;


    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Dialog", Font.BOLD, 16));
        setBackground(new Color(0, 174, 255));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getBackground().darker());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    }
}

public class Login extends JFrame {

    private int mouseX, mouseY;
    private JTextField emailField;
    private JPasswordField passwordField;


    public Login() {
        setTitle("로그인");
        setSize(786, 456);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        // 둥근 창
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        setBackground(new Color(30, 30, 32));

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
        panel.setOpaque(false);
        setContentPane(panel);

        // 창 이동
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });
        panel.setLayout(null);

        JLabel title = new JLabel("LINKer 로그인");
        title.setBounds(30, 40, 400, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        panel.add(title);

        JLabel subtitle = new JLabel("만나서 반가워요!");
        subtitle.setBounds(30, 70, 300, 20);
        subtitle.setForeground(new Color(150, 150, 150));
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 13));
        panel.add(subtitle);

        // 이메일
        JLabel emailLabel = new JLabel("이메일 또는 전화번호");
        emailLabel.setBounds(30, 120, 300, 20);
        emailLabel.setForeground(new Color(200, 200, 200));
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(30, 145, 287, 40);
        emailField.setBackground(new Color(45, 45, 50));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setOpaque(true);
        emailField.setBorder(new RoundedBorder(15));
        panel.add(emailField);

        // 비밀번호
        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBounds(36, 200, 300, 20);
        passwordLabel.setForeground(new Color(200, 200, 200));
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(30, 225, 287, 40);
        passwordField.setBackground(new Color(45, 45, 50));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setOpaque(true);
        passwordField.setBorder(new RoundedBorder(15));
        panel.add(passwordField);

        // 비밀번호 잊음
        JButton forgotBtn = new JButton("비밀번호를 잊으셨나요?");
        forgotBtn.setBounds(30, 275, 200, 20);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setForeground(new Color(150, 150, 150));
        forgotBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        panel.add(forgotBtn);

        // 로그인 버튼 (둥근 버튼)
        RoundedButton loginBtn = new RoundedButton("로그인", 30);
        loginBtn.setBounds(30, 310, 323, 45);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        panel.add(loginBtn);

        // 회원가입 버튼 (둥근 버튼, 회색 배경)
        RoundedButton signupBtn = new RoundedButton("회원가입", 30);
        signupBtn.setBounds(30, 370, 323, 40);
        signupBtn.setBackground(new Color(55, 60, 68));
        signupBtn.setFont(new Font("Dialog", Font.PLAIN, 14));
        panel.add(signupBtn);

        // 닫기 버튼
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(740, 10, 30, 30);
        closeBtn.setBackground(new Color(55, 60, 68));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        panel.add(closeBtn);

        // 이미지 라벨
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(469, 280, 249, 176);
        lblNewLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/tttt.png"))));
        panel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/qqqq.png"))));
        lblNewLabel_1.setBounds(226, 29, 585, 368);
        panel.add(lblNewLabel_1);

        signupBtn.addActionListener(e -> {
            dispose();
            new Register();
        });

        forgotBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "비밀번호 찾기 기능은 준비 중입니다."));

        setVisible(true);
        initServerConn();

        loginBtn.addActionListener(e -> tryLogin());

    }// 로그인 클래스 종료시점
    //네트워크 연결 시스템 시작지점
    private void initServerConn(){
        try {
            ServerConn conn = new ServerConn(
                    "sjc07250.iptime.org", 4885,
                    "ajk123#%k2!lsd!234!%^^f17!@#sdfs!@$3$*s1s56!@#", // 토큰
                    "v0.0.1b" // 버전
            );

            AppContext.setServerConn(conn);

            conn.setLoginListener(new LoginListener() {
                @Override
                public void onLoginSuccess(String userId) {
                    SwingUtilities.invokeLater(() -> {

                        new FriendList(userId).setVisible(true);
                        dispose();
                    });
                }

                @Override
                public void onLoginFailure(String reason) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(Login.this, "❌ 로그인 실패: " + reason);
                    });
                }

                @Override
                public void onSignUpSuccess(String userId) {
                    // 회원가입 성공 시 로그인 창에선 사용하지 않음
                }

                @Override
                public void onSignUpFailure(String reason) {
                    // 회원가입 실패 시 로그인 창에선 사용하지 않음
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "🚫 서버 연결 실패: " + e.getMessage());
            System.exit(1);
        }
    }

    private void tryLogin() {
        String id = emailField.getText().trim();
        String pw = new String(passwordField.getPassword()).trim();
        AppContext.setCurrentUserId(id);

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.");
            return;
        }

        ServerConn conn = AppContext.getServerConn();
        conn.login(id, pw);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
