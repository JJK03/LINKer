package project.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.Optional;

public class ImageMessageHandler {

    // === DB 연결 정보 ===
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    // === 이미지 파일 → byte[] 변환 ===
    public static Optional<byte[]> imageFileToBytes(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos); // 확장자에 맞게 변경 가능
            return Optional.of(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // === byte[] → DB 저장 ===
    public static boolean saveImageToDatabase(int senderId, int receiverId, byte[] imageBytes) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_type, image_data, timestamp) " +
                "VALUES (?, ?, 'image', ?, NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setBytes(3, imageBytes);
            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === DB에서 byte[] 불러와 ImageIcon 생성 ===
    public static Optional<ImageIcon> getImageIconFromDatabase(int messageId) {
        String sql = "SELECT image_data FROM messages WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("image_data");

                if (imageBytes != null && imageBytes.length > 0) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage bufferedImage = ImageIO.read(bais);
                    return Optional.of(new ImageIcon(bufferedImage));
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // === DB에서 JLabel 형태로 바로 꺼내기 (UI용) ===
    public static Optional<JLabel> getImageLabelFromDatabase(int messageId) {
        return getImageIconFromDatabase(messageId).map(JLabel::new);
    }
}
