public class Produtor extends Thread {
    private final Buffer buffer;
    private final int quantidadeProduzir;

    public Produtor(Buffer buffer, int quantidadeProduzir, String nome) {
        super(nome);
        this.buffer = buffer;
        this.quantidadeProduzir = quantidadeProduzir;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= quantidadeProduzir; i++) {
                int item = (int) (Math.random() * 100);
                buffer.produzir(item, getName());
                Thread.sleep(200); // simula tempo de produção
            }
        } catch (Exception e) {
            System.err.println(getName() + " teve erro: " + e.getMessage());
        }
    }
}
