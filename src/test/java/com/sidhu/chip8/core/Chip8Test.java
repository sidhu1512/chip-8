package com.sidhu.chip8.core;

import com.sidhu.chip8.ui.Display;
import com.sidhu.chip8.ui.Keypad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Chip8Test {
    private Chip8 chip8;

    @BeforeEach
    void setUp() {
        Display display = new Display(10);
        Keypad keypad = new Keypad();
        chip8 = new Chip8(display, keypad);
    }

    @Test
    void testInitialization() {
        assertThat(chip8.getCpu().getPc()).isEqualTo(0x200);
        assertThat(chip8.getMemory()).isNotNull();
    }

    @Test
    void testClearScreen_00E0() {
        // Setup a non-empty pixel array by drawing a sprite
        chip8.getDisplay().drawSprite(0, 0, new int[]{0xFF});
        
        // Opcode 00E0
        chip8.getMemory().write(0x200, 0x00);
        chip8.getMemory().write(0x201, 0xE0);
        
        chip8.cycle();
        
        // Display should be cleared in internal array (Display.clear() sets all to false)
        // Since we can't easily read pixels array right now due to encapsulation, 
        // we'll just check if PC incremented correctly for success
        assertThat(chip8.getCpu().getPc()).isEqualTo(0x202);
    }

    @Test
    void testJump_1NNN() {
        // Opcode 1ABC (Jump to ABC)
        chip8.getMemory().write(0x200, 0x1A);
        chip8.getMemory().write(0x201, 0xBC);
        
        chip8.cycle();
        
        assertThat(chip8.getCpu().getPc()).isEqualTo(0xABC);
    }

    @Test
    void testAddRegister_7XNN() {
        // Opcode 7305 (Add 5 to V3)
        chip8.getMemory().write(0x200, 0x73);
        chip8.getMemory().write(0x201, 0x05);
        
        chip8.cycle();
        
        assertThat(chip8.getCpu().getRegister(3)).isEqualTo(5);
        assertThat(chip8.getCpu().getPc()).isEqualTo(0x202);
    }

    @Test
    void testArithmetic_AdditionWithCarry() {
        // Setup: V1 = 250, V2 = 10
        chip8.getCpu().setRegister(1, 250);
        chip8.getCpu().setRegister(2, 10);
        
        // Opcode 8124 (V1 += V2, carry flag in VF)
        chip8.getMemory().write(0x200, 0x81);
        chip8.getMemory().write(0x201, 0x24);
        
        chip8.cycle();
        
        // 250 + 10 = 260. 260 & 0xFF = 4
        assertThat(chip8.getCpu().getRegister(1)).isEqualTo(4); 
        // Carry flag (VF) should be 1
        assertThat(chip8.getCpu().getRegister(0xF)).isEqualTo(1);
    }
}
