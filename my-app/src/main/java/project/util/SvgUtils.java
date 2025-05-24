package project.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;

public class SvgUtils {
    /* public static ImageIcon loadSvgIcon(String path, int width, int height) {
        try {
            SVGUniverse universe = new SVGUniverse();
            URI uri = universe.loadSVG(LINKer.class.getResource(path));
            SVGDiagram diagram = universe.getDiagram(uri);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            diagram.setDeviceViewport(new Rectangle(width, height));
            diagram.render(g);

            g.dispose();
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } */
    public static ImageIcon loadSvgIcon(String path, int width, int height) {
        try {
            SVGUniverse universe = new SVGUniverse();
            URI uri = SvgUtils.class.getResource(path).toURI();
            SVGDiagram diagram = universe.getDiagram(uri);
    
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
            // SVG 원본 크기 가져오기
            float svgWidth = diagram.getWidth();
            float svgHeight = diagram.getHeight();
    
            // 스케일 비율 계산
            float scaleX = width / svgWidth;
            float scaleY = height / svgHeight;
    
            // 크기에 맞게 스케일링 적용
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
