import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final int[] buffer;          // buffer de tamanho fixo (7 posições)
    private int count = 0;               // quantidade atual de itens
    private int in = 0;                  // índice de inserção
    private int out = 0;                 // índice de remoção

    private final Semaphore empty;       // controla espaços vazios
    private final Semaphore full;        // controla espaços cheios
    private final ReentrantLock mutex;   // controla acesso exclusivo

    private final BufferedWriter logWriter;

    public Buffer(int tamanho, String logFile) throws IOException {
        buffer = new int[tamanho];
        empty = new Semaphore(tamanho);
        full = new Semaphore(0);
        mutex = new ReentrantLock();
        logWriter = new BufferedWriter(new FileWriter(logFile));
    }

    // Método de produção (adiciona item ao buffer)
    public void produzir(int item, String produtorNome) throws InterruptedException, IOException {
        empty.acquire();  // espera espaço vazio
        mutex.lock();     // garante exclusão mútua

        try {
            buffer[in] = item;
            in = (in + 1) % buffer.length;
            count++;
            registrarLog("Produtor - Inserido item " + item + " no buffer – espaços disponíveis: " + (buffer.length - count), produtorNome);
        } finally {
            mutex.unlock();  // libera exclusão mútua
            full.release();  // sinaliza que há um item disponível
        }
    }

    // Método de consumo (remove item do buffer)
    public int consumir(String consumidorNome) throws InterruptedException, IOException {
        full.acquire();   // espera item disponível
        mutex.lock();     // garante exclusão mútua

        int item;
        try {
            item = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            registrarLog("Consumidor - Consumido item " + item + " do buffer – espaços disponíveis: " + (buffer.length - count), consumidorNome);
        } finally {
            mutex.unlock(); // libera exclusão mútua
            empty.release(); // sinaliza espaço vazio
        }
        return item;
    }

    // Escreve no log
    private synchronized void registrarLog(String mensagem, String threadNome) throws IOException {
        logWriter.write(threadNome + ": " + mensagem);
        logWriter.newLine();
        logWriter.flush();
    }

    public void fecharLog() throws IOException {
        logWriter.close();
    }
}
