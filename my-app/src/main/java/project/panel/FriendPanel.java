package project.panel;

import project.login.RoundedPanel;
import project.ui.LINKer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FriendPanel extends JPanel {

    private List<String> friends;  // 참조로 사용

    public FriendPanel(List<String> friends) {
        this.friends = friends;  // <-- 참조만 유지함 (복사 제거)
        setLayout(null);
        setBackground(new Color(0, 0, 0));

        JLabel label = new JLabel("친구 목록");
        label.setBounds(30, 20, 200, 30);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(label);

        renderFriendList();
    }

    private void renderFriendList() {
        removeAll();  // 기존 컴포넌트 제거 (제목 포함하므로 다시 추가 필요)

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

            panel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                public void mouseExited(MouseEvent evt) {
                    panel.setBackground(new Color(45, 45, 47));
                }

                public void mouseClicked(MouseEvent evt) {
                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        new LINKer(friend).setVisible(true);
                    } else if (SwingUtilities.isRightMouseButton(evt)) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("삭제");

                        deleteItem.addActionListener(e -> {
                            int confirm = JOptionPane.showConfirmDialog(
                                    null,
                                    friend + " 친구를 삭제하시겠습니까?",
                                    "삭제 확인",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                friends.remove(friend);  // 원본에서 삭제
                                renderFriendList();       // UI 갱신
                                revalidate();
                                repaint();
                            }
                        });

                        popupMenu.add(deleteItem);
                        popupMenu.show(panel, evt.getX(), evt.getY());
                    }
                }
            });

            add(panel);
            y += 55;
        }
    }
}

