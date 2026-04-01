package com.sidhu.chip8.core;

/**
 * Represents the CPU state of the CHIP-8 emulator.
 * Contains registers, program counter, index register, timers, and the call stack.
 */
public class Cpu {

    /**
     * The program counter (PC) holds the memory address of the next instruction to execute.
     */
    private int pc;

    /**
     * 16 general-purpose 8-bit registers (V0 to VF). Represented as ints internally.
     */
    private final int[] v;

    /**
     * The 16-bit Index register (I). Used to store memory addresses.
     */
    private int i;

    /**
     * The call stack, used to remember the current location before a jump (subroutine call).
     */
    private final int[] stack;

    /**
     * The stack pointer (SP), pointing to the top of the stack.
     */
    private int sp;

    /**
     * The delay timer. When non-zero, it automatically decrements at a rate of 60Hz.
     */
    private int delayTimer;

    /**
     * The sound timer. When non-zero, it automatically decrements at a rate of 60Hz and plays a sound.
     */
    private int soundTimer;

    public Cpu() {
        this.v = new int[16];
        this.stack = new int[16];
        reset();
    }

    public void reset() {
        this.pc = Memory.PROGRAM_START_ADDRESS;
        this.i = 0;
        this.sp = 0;
        this.delayTimer = 0;
        this.soundTimer = 0;
        for (int idx = 0; idx < 16; idx++) {
            this.v[idx] = 0;
            this.stack[idx] = 0;
        }
    }

    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }
    public void incrementPc(int amount) { this.pc += amount; }

    public int getRegister(int index) { return v[index]; }
    public void setRegister(int index, int value) { v[index] = value & 0xFF; }
    
    public void setFlag(int value) { v[0xF] = value & 0xFF; }

    public int getI() { return i; }
    public void setI(int i) { this.i = i; }
    public void incrementI(int amount) { this.i += amount; }

    public void pushStack(int value) { 
        stack[sp] = value; 
        sp++; 
    }
    
    public int popStack() { 
        sp--; 
        return stack[sp]; 
    }

    public int getDelayTimer() { return delayTimer; }
    public void setDelayTimer(int delayTimer) { this.delayTimer = delayTimer & 0xFF; }

    public int getSoundTimer() { return soundTimer; }
    public void setSoundTimer(int soundTimer) { this.soundTimer = soundTimer & 0xFF; }

    /**
     * Decrements the active timers. This is meant to be called exactly 60 times a second.
     */
    public void decrementTimers() {
        if (delayTimer > 0) delayTimer--;
        if (soundTimer > 0) soundTimer--;
    }
}
