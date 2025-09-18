public class Cpu{

    public byte[] V = new byte[16];
    public short I;
    public short PC;
    public short[] stack = new short[16];
    public byte SP;
    public byte delay;
    public byte sound;

    public Cpu() {
        PC = 512;
    }

}