import javax.swing.*;
import java.io.File;

public class GameWindow {
    public static void main(String[] args) {
        Chip8 chip8 = new Chip8();

        File romFile = new File(System.getProperty("user.home") + "/Desktop/games/gm9.ch8");
        if (romFile.exists()) {
            chip8.loadRom(romFile);
        } else {
            System.err.println("ROM not found at: " + romFile.getAbsolutePath());
            System.exit(1);
        }



        // Set up the window
        JFrame window = new JFrame("CHIP-8 Emulator");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(chip8.display); // add display panel
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Add keypad listener
        Keypad keypad = new Keypad();
        window.addKeyListener(new Key(keypad));
        chip8.setKeypad(keypad);
        window.setFocusable(true);
        window.requestFocus();


        while (true) {
            chip8.cycle();
            if (chip8.cpu.delay > 0) chip8.cpu.delay--;
            if (chip8.cpu.sound > 0) {
                chip8.cpu.sound--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
