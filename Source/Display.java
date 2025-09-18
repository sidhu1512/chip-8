import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 32;
    public static final int SCALE = 10;

    private final boolean[][] pixels = new boolean[WIDTH][HEIGHT];

    public Display() {
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setBackground(Color.BLACK);
    }

    public void clear() {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                pixels[x][y] = false;
        repaint();
    }

    public boolean drawSprite(int x, int y, byte[] sprite) {
        boolean collision = false;

        for (int row = 0; row < sprite.length; row++) {
            byte currentByte = sprite[row];
            for (int col = 0; col < 8; col++) {
                int pixelX = (x + col) % WIDTH;
                int pixelY = (y + row) % HEIGHT;

                boolean spriteBit = (currentByte & (0x80 >> col)) != 0;

                if (spriteBit) {
                    if (pixels[pixelX][pixelY]) {
                        collision = true;
                    }
                    pixels[pixelX][pixelY] ^= true;
                }
            }
        }

        repaint();
        return collision;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (pixels[x][y]) {
                    g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
                }
            }
        }
    }
}
