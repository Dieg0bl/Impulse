package com.impulse.og;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;import java.awt.image.BufferedImage;import java.io.ByteArrayOutputStream;import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/og")
public class OgImageController {

    @GetMapping(value = "/achievement/{retoId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> achievement(@PathVariable Long retoId) throws IOException {
        // Simplistic dynamic OG image placeholder
        int width = 1200, height = 630;
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(20,24,28)); g.fillRect(0,0,width,height);
        g.setColor(new Color(56,189,248)); g.setFont(new Font("SansSerif", Font.BOLD, 80));
        g.drawString("Reto #"+retoId, 80, 200);
        g.setFont(new Font("SansSerif", Font.PLAIN, 40));
        g.setColor(Color.WHITE);
        g.drawString("Completado el "+ LocalDate.now(), 80, 300);
        g.drawString("Impulse", 80, 400);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(img, "png", baos);
    byte[] bytes = baos.toByteArray();
    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl("public, max-age=3600");
    headers.set("Content-Type", MediaType.IMAGE_PNG_VALUE);
    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
