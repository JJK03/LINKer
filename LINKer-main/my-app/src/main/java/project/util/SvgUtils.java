package project.util;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class SvgUtils {

    /**
     * SVG 파일을 원하는 크기의 ImageIcon으로 변환합니다.
     *
     * @param svgPath 리소스 경로 (예: "/emoji/1f604.svg")
     * @param width   원하는 출력 가로 크기 (px)
     * @param height  원하는 출력 세로 크기 (px)
     * @return 변환된 ImageIcon 또는 null
     */
    public static ImageIcon resizeSvgIcon(String svgPath, int width, int height) {
        try (InputStream svgInputStream = SvgUtils.class.getResourceAsStream(svgPath)) {
            if (svgInputStream == null) {
                System.err.println("❌ SVG 파일을 찾을 수 없습니다: " + svgPath);
                return null;
            }

            // Transcoder 설정
            TranscoderInput input = new TranscoderInput(svgInputStream);
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();

            // 해상도 및 출력 크기 설정
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 0.264583333f); // 96 DPI

            // SVG -> BufferedImage 변환
            transcoder.transcode(input, null);
            BufferedImage bufferedImage = transcoder.getBufferedImage();

            return new ImageIcon(bufferedImage);
        } catch (Exception e) {
            System.err.println("❌ SVG 변환 중 오류 발생: " + svgPath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 내부용: SVG를 BufferedImage로 렌더링하기 위한 Transcoder 구현
     */
    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage image;

        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput out) {
            this.image = img;
        }

        public BufferedImage getBufferedImage() {
            return image;
        }
    }
}