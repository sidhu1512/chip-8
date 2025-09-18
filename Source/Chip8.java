import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public  class Chip8{
    public Memory memory;
    public Cpu cpu;
    public Display display;
    public Keypad keypad;

    public Chip8(){
        memory =  new Memory();
        cpu = new Cpu();
        display = new Display();
        keypad = new Keypad();
    }


    public void setKeypad(Keypad keypad) {
        this.keypad = keypad;
    }

    public void loadRom(File file) {
        try {
            byte[] program = Files.readAllBytes(file.toPath());
            memory.loadProgram(program);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cycle(){

        int opcode = ((memory.read(cpu.PC) & 0xFF) << 8) | (memory.read(cpu.PC + 1) & 0xFF);

        int instruction = (opcode & 0xF000) >> 12;
        int x = (opcode & 0x0F00) >> 8;
        int y = (opcode & 0x00F0) >> 4;
        int lastNibble = opcode & 0x000F;
        int nn = opcode & 0x00FF;
        int nnn = opcode & 0x0FFF;

        switch (instruction) {
            case 0x0:
                switch (nn) {
                    case 0xE0: //clear screen
                        display.clear();
                        cpu.PC += 2;
                        break;

                    case 0xEE:
                        cpu.SP--;
                        cpu.PC = cpu.stack[cpu.SP];
                        cpu.PC += 2;
                        break;
                }
                break;


            case 0x1:
                cpu.PC = (short) nnn;
                break;

            case 0x2: // CALL addr
                cpu.stack[cpu.SP] = cpu.PC;
                cpu.SP++;
                cpu.PC = (short) nnn;
                break;

            case 0x3: // SE Vx, byte
                if ((cpu.V[x] & 0xFF) == nn) cpu.PC += 4;
                else cpu.PC += 2;
                break;

            case 0x4: // SNE Vx, byte
                if ((cpu.V[x] & 0xFF) != nn) cpu.PC += 4;
                else cpu.PC += 2;
                break;

            case 0x5: // SE Vx, Vy
                if (lastNibble == 0 && cpu.V[x] == cpu.V[y]) cpu.PC += 4;
                else cpu.PC += 2;
                break;

            case 0x6: //6xnn : Vx = NN
                cpu.V[x] = (byte) nn;
                cpu.PC += 2;
                break;

            case 0x7: //vx+= nn
                cpu.V[x] += (byte) nn;
                cpu.PC += 2;
                break;

            case 0x8: //8000 series

                switch (lastNibble){
                    case 0x0:
                        cpu.V[x] = cpu.V[y];
                        break;

                    case 0x1:
                        cpu.V[x] |=  cpu.V[y];
                        break;

                    case 0x2:
                        cpu.V[x] &= cpu.V[y];
                        break;

                    case 0x3:
                        cpu.V[x] ^= cpu.V[y];
                        break;

                    case 0x4:
                        int sum = (cpu.V[x] & 0xFF) + (cpu.V[y] & 0xFF);
                        cpu.V[0xF] = (byte) (sum > 255 ? 1 : 0);
                        cpu.V[x] = (byte) (sum & 0xFF);
                        break;

                    case 0x5:
                        int sub1 = (cpu.V[x] & 0xFF) - (cpu.V[y] & 0xFF);
                        cpu.V[0xF] = (byte) (sub1 >= 0 ? 1 : 0);
                        cpu.V[x] = (byte) (sub1 & 0xFF);
                        break;

                    case 0x6:
                        cpu.V[0xF] = (byte)((cpu.V[x] & 0x1) );
                        cpu.V[x] = (byte)((cpu.V[x] & 0xFF) >> 1);
                        break;

                    case 0x7:
                        int sub2 = (cpu.V[y] & 0xFF) - (cpu.V[x] & 0xFF);
                        cpu.V[0xF] = (byte) (sub2 >= 0 ? 1 : 0);
                        cpu.V[x] = (byte) (sub2 & 0xFF);
                        break;

                    case 0xE:
                        cpu.V[0xF] = (byte)((cpu.V[x] & 0x80) >> 7);
                        cpu.V[x] = (byte)((cpu.V[x] & 0xFF) << 1);
                        break;

                }
                cpu.PC += 2;
                break;

            case 0x9:
                if (lastNibble == 0) {
                    if (cpu.V[x] != cpu.V[y]) {
                        cpu.PC += 2;
                    }
                    cpu.PC += 2;
                }
                break;

            case 0xA: //I = NNN
                cpu.I = (short)nnn;
                cpu.PC += 2;
                break;

            case 0xB:
                cpu.PC = (short) (nnn + (cpu.V[0] & 0xFF));
                break;

            case 0xC:
                int rand = (int) (Math.random() * 256);
                cpu.V[x] = (byte) (rand & nn);
                cpu.PC += 2;
                break;

            case 0xD:
                int vx = cpu.V[x] & 0xFF;
                int vy = cpu.V[y] & 0xFF;
                byte[] sprite = new byte[lastNibble];
                for (int i = 0; i < lastNibble; i++) {
                    sprite[i] = memory.read(cpu.I + i);
                }
                boolean collision = display.drawSprite(vx, vy, sprite);
                cpu.V[0xF] = (byte) (collision ? 1 : 0);
                cpu.PC += 2;
                break;

            case 0xE:
                switch (nn) {
                    case 0x9E:
                        if (keypad.isPressed(cpu.V[x] & 0xF)) cpu.PC += 4;
                        else cpu.PC += 2;
                        break;

                    case 0xA1:
                        if (!keypad.isPressed(cpu.V[x] & 0xF)) cpu.PC += 4;
                        else cpu.PC += 2;
                        break;
                }
                break;

            case 0xF:
                switch (nn) {
                    case 0x07: // LD Vx, DT
                        cpu.V[x] = cpu.delay;
                        cpu.PC += 2;
                        break;

                    case 0x15: // LD DT, Vx
                        cpu.delay = cpu.V[x];
                        cpu.PC += 2;
                        break;

                    case 0x18: // LD ST, Vx
                        cpu.sound = cpu.V[x];
                        cpu.PC += 2;
                        break;

                    case 0x1E: // ADD I, Vx
                        cpu.I += (short)(cpu.V[x] & 0xFF);
                        cpu.PC += 2;
                        break;

                    case 0x0A: // LD Vx, K
                        for (int i = 0; i < 16; i++) {
                            if (keypad.isPressed(i)) {
                                cpu.V[x] = (byte) i;
                                cpu.PC += 2;
                                return;
                            }
                        }

                        break;

                    case 0x29: // LD F, Vx
                        cpu.I = (short) ((cpu.V[x] & 0xFF) * 5); // font sprite address
                        cpu.PC += 2;
                        break;

                    case 0x33: { // LD B, Vx (binary-coded decimal)
                        int value = cpu.V[x] & 0xFF;
                        memory.write(cpu.I, (byte)(value / 100));
                        memory.write(cpu.I + 1, (byte)((value / 10) % 10));
                        memory.write(cpu.I + 2, (byte)(value % 10));
                        cpu.PC += 2;
                        break;
                    }

                    case 0x55: // LD [I], Vx
                        for (int i = 0; i <= x; i++) {
                            memory.write(cpu.I + i, cpu.V[i]);
                        }
                        cpu.PC += 2;
                        break;

                    case 0x65: // LD Vx, [I]
                        for (int i = 0; i <= x; i++) {
                            cpu.V[i] = memory.read(cpu.I + i);
                        }
                        cpu.PC += 2;
                        break;
                }
                break;

            default :
                System.out.printf("Unknown instruction: 0x%04X at PC: 0x%03X%n", opcode, cpu.PC & 0xFFFF);
                cpu.PC += 2;

        }

    }
}