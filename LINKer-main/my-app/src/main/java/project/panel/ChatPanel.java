package project.panel;

import project.login.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import project.core.AppContext;
import project.ui.LINKer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatPanel extends JPanel implements ThemeManager.ThemeChangeListener {

    private static final int THUMB_SIZE = 6;  // 스크롤바 너비

    private JPanel contentPanel;
    private JScrollPane scrollPane;
    String Cuser = AppContext.getCurrentUserID();

    public ChatPanel(List<String> chatList) {
        setLayout(new BorderLayout());  // 전체 레이아웃 BorderLayout으로 변경
        setBackground(ThemeManager.getBackgroundColor());

        // 상단 제목 라벨 생성 및 설정
        JLabel label = new JLabel("채팅 목록");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        add(label, BorderLayout.NORTH);

        // 채팅 목록이 들어갈 패널 생성 (null 레이아웃)
        contentPanel = new JPanel(null);
        contentPanel.setBackground(ThemeManager.getBackgroundColor());

        // contentPanel을 스크롤 가능하게 JScrollPane으로 감싸기
        scrollPane = new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(760, 450)); // 적당한 크기 지정 (필요시 조절)

        // 스크롤바 커스터마이징 (얇고 회색톤)
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);  // 스크롤 속도 설정
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected Dimension getMinimumThumbSize() {
                return new Dimension(THUMB_SIZE, THUMB_SIZE);
            }

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(120, 120, 120);
                this.trackColor = new Color(50, 50, 50);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(THUMB_SIZE, Integer.MAX_VALUE));

        add(scrollPane, BorderLayout.CENTER);

        // 친구 목록 렌더링과 비슷하게 채팅 목록 렌더링
        renderChatList(chatList);

        // 테마 변경 리스너 등록
        ThemeManager.addThemeChangeListener(this);
    }

    // 채팅 목록을 contentPanel에 추가하는 메서드
    private void renderChatList(List<String> chatList) {
        contentPanel.removeAll();
        int y = 10;

        for (String chat : chatList) {
            String[] parts = chat.split("\\|\\|");
            RoundedPanel panel = new RoundedPanel(15);
            panel.setLayout(new BorderLayout());
            panel.setBounds(20, y, 700, 45);
            panel.setBackground(new Color(45, 45, 47));

            JLabel nameLabel = new JLabel(parts[0]);
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

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent evt) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                @Override
                public void mouseExited(MouseEvent evt) {
                    panel.setBackground(new Color(45, 45, 47));
                }

                @Override
                public void mouseClicked(MouseEvent evt) {
                    new LINKer(chat).setVisible(true);
                    AppContext.getServerConn().sendChatRecords(Cuser, parts[1]);
                }
            });

            contentPanel.add(panel);
            y += 55;
        }

        // contentPanel 크기 조정 (채팅 항목 수에 따라 높이 증가, 최소 400 유지)
        contentPanel.setPreferredSize(new Dimension(720, Math.max(y + 20, 400)));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 테마 변경 콜백
    @Override
    public void onThemeChanged(Color newColor) {
        setBackground(newColor);
        contentPanel.setBackground(newColor);
        repaint();
    }
}
