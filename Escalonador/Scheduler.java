import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
  Scheduler.java
  Simulação de escalonamento
  - Entrada: arquivo com linhas "id;tempoEntrada;tempoIO;tempoProcessamento;prioridade"
  - Processamento: quantum CPU = 3 , quantum I/O = 6
  - Saída: arquivo "output.txt" com linhas "tempo_saida;idProcesso"

  Alunos:
  Isabella Campos Bueno 22307795
  Kauanne Júlia Ledo Calvacante 22306287
  Luiz Felipe Campos da Silva 22310854


*/

class Process {
    int id;
    int arrival;
    int remIO;
    int remCPU;
    int priority;
    boolean finalizing; // se precisa de 1 ciclo para finalizar (após I/O)
    Process next; // para as filas e as listas

    Process(int id, int arrival, int remIO, int remCPU, int priority) {
        this.id = id;
        this.arrival = arrival;
        this.remIO = remIO;
        this.remCPU = remCPU;
        this.priority = priority;
        this.finalizing = false;
        this.next = null;
    }
}

//Fila FIFO
class SimpleQueue {
    Process head;
    Process tail;

    SimpleQueue() {
        head = tail = null;
    }

    boolean isEmpty() {
        return head == null;
    }

    void enqueue(Process p) {
        p.next = null;
        if (tail == null) {
            head = tail = p;
        } else {
            tail.next = p;
            tail = p;
        }
    }

    Process dequeue() {
        if (head == null) return null;
        Process p = head;
        head = head.next;
        if (head == null) tail = null;
        p.next = null;
        return p;
    }
}

/* Fila de prioridade (menor valor = maior prioridade).
   Implementada com um array de SimpleQueue; índice 1..maxPriority */
class PriorityReadyQueue {
    SimpleQueue[] buckets;
    int maxPriority;

    PriorityReadyQueue(int maxPriority) {
        this.maxPriority = Math.max(1, maxPriority);
        buckets = new SimpleQueue[this.maxPriority + 1];
        for (int i = 0; i <= this.maxPriority; i++) buckets[i] = new SimpleQueue();
    }

    void enqueue(Process p) {
        int pr = p.priority;
        if (pr < 1) pr = 1;
        if (pr > maxPriority) pr = maxPriority;
        buckets[pr].enqueue(p);
    }

    boolean isEmpty() {
        for (int i = 1; i <= maxPriority; i++)
            if (!buckets[i].isEmpty()) return false;
        return true;
    }

    Process dequeueHighestPriority() {
        for (int i = 1; i <= maxPriority; i++) {
            if (!buckets[i].isEmpty()) {
                return buckets[i].dequeue();
            }
        }
        return null;
    }
}

public class Scheduler {
    static final int CPU_QUANTUM = 3;
    static final int IO_QUANTUM = 6;

    // leitura do arquivo: retorna array de processos (não usam coleções)
    static Process[] readInput(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        // primeiro é contado as linhas para alocar array
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() > 0) count++;
        }
        br.close();

        Process[] procs = new Process[count];
        br = new BufferedReader(new FileReader(filename));
        int idx = 0;
        int maxPriority = 1;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) continue;
           
            String[] parts = line.split(";");
            if (parts.length != 5) {
                br.close();
                throw new IOException("Formato inválido na linha: " + line);
            }
            int id = Integer.parseInt(parts[0].trim());
            int arrival = Integer.parseInt(parts[1].trim());
            int tIO = Integer.parseInt(parts[2].trim());
            int tCPU = Integer.parseInt(parts[3].trim());
            int pr = Integer.parseInt(parts[4].trim());
            if (pr > maxPriority) maxPriority = pr;
            procs[idx++] = new Process(id, arrival, tIO, tCPU, pr);
        }
        br.close();
        return procs;
    }

    // escreve  a saída
    static void writeOutput(String filename, String[] lines) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for (String l : lines) {
            bw.write(l);
            bw.newLine();
        }
        bw.close();
    }

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";
        if (args.length >= 1) inputFile = args[0];
        if (args.length >= 2) outputFile = args[1];

        try {
            Process[] all = readInput(inputFile);
            if (all.length == 0) {
                System.out.println("Arquivo de entrada vazio.");
                return;
            }

            // determinar max prioridade a partir dos processos (para construir PriorityReadyQueue)
            int maxP = 1;
            for (Process p : all) if (p.priority > maxP) maxP = p.priority;

            PriorityReadyQueue ready = new PriorityReadyQueue(maxP);
            SimpleQueue ioQueue = new SimpleQueue();

            int total = all.length;
            int finished = 0;
            String[] outputLines = new String[total]; // iremos armazenar "tempo_saida;id"

            // ordena os processos por arrival simples (sem usar bibliotecas)
            for (int i = 0; i < all.length - 1; i++) {
                for (int j = 0; j < all.length - 1 - i; j++) {
                    if (all[j].arrival > all[j+1].arrival) {
                        Process tmp = all[j]; all[j] = all[j+1]; all[j+1] = tmp;
                    }
                }
            }

            int time = 0;
            int nextArrivalIndex = 0;

            // CPU e IO ocupados atualmente:
            Process cpuProc = null;
            int cpuSliceLeft = 0; // quanto falta da fatia atual
            Process ioProc = null;
            int ioSliceLeft = 0;

            // Simulação ciclo-a-ciclo
            while (finished < total) {
                
                //adicionar chegadas no tempo atual
                while (nextArrivalIndex < all.length && all[nextArrivalIndex].arrival <= time) {
                    Process p = all[nextArrivalIndex++];
                    // Ao entrar no sistema, insere na fila de prioridade
                    ready.enqueue(p);
                }

                
                //se CPU livre, escalona próximo da fila de prioridade
                if (cpuProc == null) {
                    if (!ready.isEmpty()) {
                        cpuProc = ready.dequeueHighestPriority();
                        // define a pare/slice
                        if (cpuProc.finalizing) {
                            cpuSliceLeft = 1; // caso de finalização pós-IO
                        } else {
                            cpuSliceLeft = Math.min(CPU_QUANTUM, cpuProc.remCPU);
                        }
                    }
                }

                
                //se IO livre, pega próximo da fila de IO (FIFO)
                if (ioProc == null) {
                    if (!ioQueue.isEmpty()) {
                        ioProc = ioQueue.dequeue();
                        ioSliceLeft = Math.min(IO_QUANTUM, ioProc.remIO);
                    }
                }

                
                //executar 1 ciclo (CPU e IO em paralelo)
                // CPU execução:
                if (cpuProc != null) {
                    cpuProc.remCPU = Math.max(0, cpuProc.remCPU - 1);
                    cpuSliceLeft--;
                }

                // IO execução:
                if (ioProc != null) {
                    ioProc.remIO = Math.max(0, ioProc.remIO - 1);
                    ioSliceLeft--;
                }

                // avanço de tempo
                time++;

                //pós-ciclo é tratado eventos terminados ou fatias esgotadas

                //tratar CPU término / slice esgotado
                if (cpuProc != null) {
                    boolean cpuFinished = (cpuProc.remCPU == 0);
                    if (cpuFinished) {
                        // Se não há mais processamento e nem I/O finalizamos (saída)
                        if (cpuProc.remIO == 0) {
                            
                            // Registra saída no tempo atual
                            outputLines[finished] = time + ";" + cpuProc.id;
                            finished++;
                            cpuProc = null;
                            cpuSliceLeft = 0;
                        } else {
                            // Se CPU terminou, mas existe I/O vai para fila de I/O
                            cpuProc.finalizing = false; // não finalizando
                            ioQueue.enqueue(cpuProc);
                            cpuProc = null;
                            cpuSliceLeft = 0;
                        }
                    } else {
                        // CPU não terminou, mas a fatia pode ter acabado
                        if (cpuSliceLeft == 0) {
                            // Se após a fatia, processo ainda possui remCPU
                            if (cpuProc.remIO > 0) {
                                // envia para I/O
                                ioQueue.enqueue(cpuProc);
                                cpuProc = null;
                            } else {
                                // remCPU > 0 e remIO == 0 reentrar na fila de processamento por prioridade
                                ready.enqueue(cpuProc);
                                cpuProc = null;
                            }
                            cpuSliceLeft = 0;
                        }
                    }
                }

                //tratando IO término / slice esgotado
                if (ioProc != null) {
                    boolean ioFinishedNow = (ioProc.remIO == 0);
                    if (ioFinishedNow) {
                        // Ao finalizar o I/O:
                        if (ioProc.remCPU > 0) {
                            // tem ainda CPU volta para fila de processamento
                            ioProc.finalizing = false;
                            ready.enqueue(ioProc);
                        } else {
                            // se não tem CPU, porém ainda possuía I/O (agora zerou):
                            // Logo, marcamos como finalizing e o colocamos na fila de processamento para gastar 1 ciclo e finalizar.
                            ioProc.finalizing = true;
                            ready.enqueue(ioProc);
                        }
                        ioProc = null;
                        ioSliceLeft = 0;
                    } else {
                        // IO não terminou, mas fatia pode ter acabado
                        if (ioSliceLeft == 0) {
                            // ainda possui remIO > 0
                            ioQueue.enqueue(ioProc);
                            ioProc = null;
                        }
                    }
                }

                // Observação: se novas chegadas ocorreram exatamente no mesmo tempo, elas já foram
                // inseridas no início do ciclo e consideradas no escalonamento seguinte.
                // Loop continua até todos finalizarem.
           
            } // fim while

            // Gravando saída: ordenar saída por tempo de saída crescente pode ser desejado.
            // Aqui foi gravado em ordem de finalização (finished crescente).
            writeOutput(outputFile, outputLines);
            System.out.println("Simulação concluída. Saída escrita em: " + outputFile);

        } catch (IOException e) {
            System.err.println("Erro I/O: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

