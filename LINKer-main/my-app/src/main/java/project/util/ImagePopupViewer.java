package project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImagePopupViewer {

    public static void showImagePopup(JFrame parent, ImageIcon icon) {
        final JDialog dialog = new JDialog(parent, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 220));
        dialog.setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize);
        dialog.setLocationRelativeTo(null);

        int closeBtnSize = 40;
        ImageIcon closeIcon = SvgUtils.resizeSvgIcon("/img/Close.svg", closeBtnSize, closeBtnSize);
        JButton closeButton = new JButton(closeIcon);
        closeButton.setBounds(screenSize.width - closeBtnSize - 20, 20, closeBtnSize, closeBtnSize);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton);

        ZoomableImagePanel imagePanel = new ZoomableImagePanel(icon.getImage());
        imagePanel.setBounds(0, 0, screenSize.width, screenSize.height);
        dialog.add(imagePanel);

        dialog.setVisible(true);
    }

    // 이미지 확대 및 이동 기능
    private static class ZoomableImagePanel extends JPanel {
        private Image image;
        private double scale = 1.0;
        private int offsetX, offsetY;
        private Point dragStart;

        public ZoomableImagePanel(Image image) {
            this.image = image;
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    dragStart = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }

                public void mouseReleased(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    offsetX += dx;
                    offsetY += dy;
                    dragStart = e.getPoint();
                    repaint();
                }
            });

            addMouseWheelListener(e -> {
                double delta = 0.05f * e.getPreciseWheelRotation();
                scale -= delta;
                scale = Math.max(0.1, Math.min(scale, 5));
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int iw = (int) (image.getWidth(null) * scale);
            int ih = (int) (image.getHeight(null) * scale);

            int x = (getWidth() - iw) / 2 + offsetX;
            int y = (getHeight() - ih) / 2 + offsetY;

            g2.drawImage(image, x, y, iw, ih, null);
        }
    }
}
