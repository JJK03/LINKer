package project.panel;

import project.core.AppContext;
import project.login.RoundedPanel;
import project.ui.LINKer;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FriendPanel extends JPanel implements ThemeManager.ThemeChangeListener {

    private static final int THUMB_SIZE = 6;

    private List<String> friends;
    private JPanel contentPanel;
    private JScrollPane scrollPane;

    public FriendPanel(List<String> friends) {
        this.friends = friends;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        JLabel label = new JLabel("친구 목록");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        contentPanel = new JPanel(null);
        contentPanel.setBackground(ThemeManager.getBackgroundColor());

        scrollPane = new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

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

        add(label, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        renderFriendList();

        ThemeManager.addThemeChangeListener(this);
    }


    private void renderFriendList() {
        contentPanel.removeAll();
        int y = 10;

        for (String friend : friends) {
            String[] parts = friend.split("\\|\\|");
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

            //  우클릭시 친구삭제 뜸
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem deleteItem = new JMenuItem("삭제");
            popupMenu.add(deleteItem);

            // 삭제 버튼 누르면 삭제
            deleteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(
                            FriendPanel.this,
                            friend + " 친구를 삭제하시겠습니까?",
                            "친구 삭제",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        friends.remove(friend);
                        renderFriendList(); // 목록 다시 그리기
                    }
                }
            });

            panel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    panel.setBackground(new Color(0, 174, 255));
                }

                public void mouseExited(MouseEvent evt) {
                    panel.setBackground(new Color(45, 45, 47));
                }
                // 클릭시 뜨는 말
                public void mouseClicked(MouseEvent evt) {
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        popupMenu.show(panel, evt.getX(), evt.getY());
                    } else if (SwingUtilities.isLeftMouseButton(evt)) {
                        new LINKer(friend).setVisible(true);
                        String userid = AppContext.getCurrentUserID();
                        AppContext.getServerConn().CreateChat(userid,friend);
                        AppContext.getServerConn().sendChatRecords(userid,parts[1]);
                    }
                }
            });

            contentPanel.add(panel);
            y += 55;
        }

        contentPanel.setPreferredSize(new Dimension(720, Math.max(y + 20, 400)));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void onThemeChanged(Color newColor) {
        setBackground(newColor);
        contentPanel.setBackground(newColor);
        repaint();
    }
}