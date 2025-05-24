package project.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;

public class SvgUtils {
    public static ImageIcon loadSvgIcon(String path, int width, int height) {
        try {
            SVGUniverse universe = new SVGUniverse();
            URI uri = SvgUtils.class.getResource(path).toURI();
            SVGDiagram diagram = universe.getDiagram(uri);
    
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
            // SVG 원본 viewBox 크기 확인
            double svgWidth = diagram.getViewRect().getWidth();
            double svgHeight = diagram.getViewRect().getHeight();

            if (svgWidth <= 0 || svgHeight <= 0) {
                svgWidth = 100;
                svgHeight = 100;
            }
    
            // 스케일 비율 계산
            double scaleX = (double) width / svgWidth;
            double scaleY = (double) height / svgHeight;
    
            // 좌표계 맞춤 (기본적으로 diagram.render는 viewBox에 맞게 그림)
            g.scale(scaleX, scaleY);
            diagram.render(g);
    
            g.dispose();
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
