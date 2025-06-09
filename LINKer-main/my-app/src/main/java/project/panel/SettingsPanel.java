package project.panel;

import project.login.Login;
import project.login.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SettingsPanel extends JPanel implements ThemeManager.ThemeChangeListener {

    // 사용자 성별과 생일 정보를 저장하는 변수 (초기값 설정)
    private String gender = "선택 안함";      // 성별 기본값
    private String birthday = "미입력";       // 생일 기본값

    /**
     * SettingsPanel 생성자
     * @param userEmail 로그인한 사용자의 이메일
     * @param showTextOverlay 텍스트 오버레이를 화면에 띄우는 Consumer 콜백 (기능 미구현 알림용)
     */
    public SettingsPanel(String userEmail, Consumer<String> showTextOverlay) {
        setLayout(null);  // 절대 위치 배치 사용

        // 현재 테마에 맞는 배경색을 ThemeManager에서 가져와 설정
        setBackground(ThemeManager.getBackgroundColor());

        // 테마 변경 이벤트 수신을 위해 리스너 등록
        ThemeManager.addThemeChangeListener(this);

        // "내 프로필" 제목 라벨 생성 및 스타일 설정
        JLabel titleLabel = new JLabel("내 프로필");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20, 10, 200, 30);
        add(titleLabel);

        // 정보 버튼 생성 ("i" 표시)
        JButton infoButton = new JButton("i");
        infoButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoButton.setForeground(Color.WHITE);
        infoButton.setBackground(new Color(45, 45, 47));  // 어두운 배경색
        infoButton.setFocusPainted(false);                // 클릭 시 테두리 비활성화
        infoButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));  // 흰색 테두리
        infoButton.setBounds(110, 15, 20, 20);
        infoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 마우스 커서 손가락으로 변경
        // 클릭 시 제작자 정보 메시지 박스 표시
        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "만든사람\n윤정현\n장진규\n황준하",
                    "제작자 정보",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        add(infoButton);

        // 프로필 사진 라벨, 지정 경로에서 이미지 불러옴 (크기 80x80)
        JLabel profilePicLabel = new JLabel(new ImageIcon("C:\\Users\\준하\\Desktop\\profile.png"));
        profilePicLabel.setBounds(20, 50, 80, 80);
        add(profilePicLabel);

        // 사용자 이메일 표시용 라벨 (아래에 별도 정보도 표시 예정)
        JLabel myAccountLabel = new JLabel();
        myAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        myAccountLabel.setForeground(Color.LIGHT_GRAY);
        myAccountLabel.setBounds(115, 60, 300, 20);
        add(myAccountLabel);

        // LINKER ID 표시용 라벨 (myAccountLabel 아래 위치)
        JLabel linkerIdLabel = new JLabel();
        linkerIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        linkerIdLabel.setForeground(Color.LIGHT_GRAY);
        linkerIdLabel.setBounds(115, 94, 300, 20);
        add(linkerIdLabel);

        // 설정 항목 배열 (내 계정, 내 정보 설정, 테마 변경, 로그아웃)
        String[] items = {"내 계정", "내 정보 설정", "테마 변경", "로그아웃"};
        int y = 150;  // 각 항목의 Y 좌표 시작 위치

        // 각 항목마다 RoundedPanel 생성 및 스타일링, 이벤트 연결
        for (String item : items) {
            RoundedPanel panel = new RoundedPanel(15);  // 둥근 모서리 반경 15
            panel.setLayout(new BorderLayout());
            panel.setBounds(20, y, 700, 45);
            panel.setBackground(new Color(45, 45, 47));  // 어두운 배경색

            // 항목 이름 라벨
            JLabel label = new JLabel(item);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.PLAIN, 15));
            label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // 왼쪽 여백

            // 우측에 화살표 ">" 라벨
            JLabel arrow = new JLabel(">");
            arrow.setForeground(Color.GRAY);
            arrow.setFont(new Font("SansSerif", Font.BOLD, 15));
            arrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // 오른쪽 여백
            arrow.setHorizontalAlignment(SwingConstants.RIGHT);

            panel.add(label, BorderLayout.WEST);  // 왼쪽에 항목명
            panel.add(arrow, BorderLayout.EAST);  // 오른쪽에 화살표

            // 마우스 이벤트 처리기 추가
            panel.addMouseListener(new MouseAdapter() {
                // 클릭 시 동작 정의
                public void mouseClicked(MouseEvent e) {
                    if (item.equals("로그아웃")) {
                        // 현재 창 닫고 로그인 창 다시 열기
                        SwingUtilities.getWindowAncestor(SettingsPanel.this).dispose();
                        new Login();
                    } else if (item.equals("내 계정")) {
                        // 사용자 계정 정보 메시지로 표시
                        String beforeAt = userEmail.contains("@") ? userEmail.substring(0, userEmail.indexOf("@")) : userEmail;
                        JOptionPane.showMessageDialog(SettingsPanel.this,
                                "내 이메일: " + userEmail +
                                        "\n내 LINKER ID: " + beforeAt +
                                        "\n성별: " + gender +
                                        "\n생일: " + birthday,
                                "내 계정 정보",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (item.equals("내 정보 설정")) {
                        // 내 정보 설정 다이얼로그 구성 (성별 선택 콤보박스, 생일 입력 텍스트필드)
                        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
                        infoPanel.setBackground(Color.WHITE);

                        JLabel genderLabel = new JLabel("성별:");
                        String[] genders = {"남성", "여성", "선택 안함"};
                        JComboBox<String> genderBox = new JComboBox<>(genders);
                        genderBox.setSelectedItem(gender);  // 기존 선택값으로 초기화

                        JLabel birthLabel = new JLabel("생일:");
                        JTextField birthField = new JTextField(birthday.equals("미입력") ? "" : birthday);

                        infoPanel.add(genderLabel);
                        infoPanel.add(genderBox);
                        infoPanel.add(birthLabel);
                        infoPanel.add(birthField);

                        // 확인/취소 다이얼로그 띄우기
                        int result = JOptionPane.showConfirmDialog(SettingsPanel.this, infoPanel, "내 정보 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            // 확인 눌렀을 때 입력값 저장 및 기본값 처리
                            gender = (String) genderBox.getSelectedItem();
                            birthday = birthField.getText().trim();
                            if (birthday.isEmpty()) birthday = "미입력";

                            // 입력한 정보 확인용 메시지 표시
                            JOptionPane.showMessageDialog(SettingsPanel.this,
                                    "성별: " + gender + "\n생일: " + birthday,
                                    "입력한 정보",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else if (item.equals("테마 변경")) {
                        // 테마 변경 기능: 색상 선택 다이얼로그 띄우기
                        String[] colorNames = {"기본", "빨강", "주황", "노랑", "초록", "파랑", "남색", "보라"};
                        Color[] colors = {
                                new Color(0, 0, 0),         // 기본 검정
                                new Color(255, 87, 87),     // 빨강
                                new Color(255, 165, 0),     // 주황
                                new Color(255, 255, 0),     // 노랑
                                new Color(0, 255, 0),       // 초록
                                new Color(0, 128, 255),     // 파랑
                                new Color(0, 0, 139),       // 남색
                                new Color(138, 43, 226)     // 보라
                        };

                        // 선택창 띄우고 선택된 테마 색상 적용
                        String selected = (String) JOptionPane.showInputDialog(
                                SettingsPanel.this,
                                "테마 색상을 선택하세요:",
                                "테마 변경",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                colorNames,
                                colorNames[0]
                        );

                        if (selected != null) {
                            for (int i = 0; i < colorNames.length; i++) {
                                if (selected.equals(colorNames[i])) {
                                    // 선택한 색상을 ThemeManager에 저장하고 변경 알림
                                    ThemeManager.setBackgroundColor(colors[i]);
                                    break;
                                }
                            }
                        }
                    } else {
                        // 구현되지 않은 항목 클릭 시, 외부 콜백을 통해 안내 텍스트 표시
                        if (showTextOverlay != null) {
                            showTextOverlay.accept(item + " 구현중");
                        }
                    }
                }

                // 마우스가 항목에 들어올 때 배경색 변경 (하이라이트 효과)
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                // 마우스가 항목에서 나갈 때 배경색 원래대로 복원
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(new Color(45, 45, 47));
                }
            });

            // 패널을 SettingsPanel에 추가
            add(panel);
            y += 50;  // 다음 항목 Y 좌표 간격
        }
    }

    /**
     * 테마 변경 시 호출되는 콜백 메서드
     * @param newColor 변경된 배경색
     */
    @Override
    public void onThemeChanged(Color newColor) {
        setBackground(newColor);  // 배경색 변경
        repaint();               // 다시 그리기
    }
}
