public class Consumidor extends Thread {
    private final Buffer buffer;
    private final int quantidadeConsumir;

    public Consumidor(Buffer buffer, int quantidadeConsumir, String nome) {
        super(nome);
        this.buffer = buffer;
        this.quantidadeConsumir = quantidadeConsumir;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= quantidadeConsumir; i++) {
                buffer.consumir(getName());
                Thread.sleep(300); // simula tempo de consumo
            }
        } catch (Exception e) {
            System.err.println(getName() + " teve erro: " + e.getMessage());
        }
    }
}
