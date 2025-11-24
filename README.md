📘 Simulador de Escalonamento de Processos + Sistema Produtor–Consumidor (Threads)

Este repositório contém dois projetos acadêmicos em Java, ambos relacionados a Sistemas Operacionais:

Simulador de Escalonamento de Processos (fila de prioridades + FIFO).

Sistema Produtor–Consumidor usando Threads, Semáforos e Mutex.

Os dois projetos foram implementados sem o uso de coleções da biblioteca padrão (como ArrayList), utilizando apenas estruturas próprias como filas simples e filas por prioridade.

🖥️ 1) Simulador de Escalonamento de Processos

Simula o funcionamento de um escalonador de CPU e I/O com operações de chegada, preempção por quantum, filas de prioridade e alternância CPU ↔ I/O.

✅ Funcionalidades

Leitura de processos a partir de input.txt.

Escalonamento por prioridade (maior prioridade executa primeiro).

Quantum de CPU = 3 ciclos.

Quantum de I/O = 6 ciclos.

Processos circulam entre CPU, I/O e finalização.

Geração de output.txt contendo o tempo de finalização de cada processo.

📂 Organização de Pastas do Simulador

projeto-escalonador/
│── src/
│   ├── Scheduler.java
│   ├── Process.java
│   ├── SimpleQueue.java
│   └── PriorityReadyQueue.java
│
│── input.txt     # entrada com processos
│── output.txt    # saída gerada
│── README.md


📥 Formato do Arquivo input.txt

id;arrival;tIO;tCPU;priority

Exemplo:
1;0;4;6;2
2;2;3;5;1
3;4;2;8;3

📤 Formato do output.txt

tempo_saida;id

Exemplo:

12;1
18;2
25;3

▶️ Como Executar (Simulador)

Compile:

javac *.java


Execute:

java Scheduler input.txt output.txt


Se não especificado, usa:

entrada → input.txt

saída → output.txt

🔧 Ajustes Possíveis

Alterar o quantum da CPU e I/O direto em Scheduler.java.

Adicionar mais processos.

Ajustar prioridades.

📚 Conceitos Aplicados (Simulador)

Escalonamento por prioridade.

Simulação de ciclos de tempo.

Filas FIFO e filas de prioridades.

Quantum (time slice).

Alternância entre CPU e I/O.

🔧 2) Sistema Produtor–Consumidor (Threads, Semáforos, Mutex)

Implementação completa do problema clássico Produtor–Consumidor, usando:

Semaphore

ReentrantLock

Thread com classes separadas para produtor e consumidor

Log automático das operações em arquivo texto

O buffer tem 7 posições, com:

Produtor produz até 15 itens

Consumidor consome até 12 itens

📂 Organização de Pastas do Produtor–Consumidor
trabalho-threads/
│── Buffer.java
│── Produtor.java
│── Consumidor.java
│── Main.java
│── log.txt
│── README.md

📝 Funcionamento

O produtor só insere se houver espaço.

O consumidor só remove se houver itens.

Todas as operações são registradas no arquivo log.txt.

Exemplo:

Produtor-1: Produtor - Inserido item 42 no buffer – espaços disponíveis: 6
Consumidor-1: Consumidor - Consumido item 42 do buffer – espaços disponíveis: 7

▶️ Como Executar (Threads)

Compile:

javac *.java


Execute:

java Main


Gera automaticamente:

log.txt

📚 Conceitos Aplicados (Threads)

Semaphore: controla espaços cheios/vazios do buffer.

Mutex (ReentrantLock): garante exclusão mútua.

Região crítica: métodos produtor e consumidor.

Thread Synchronization: evita race conditions.

Buffer circular.

📦 Conclusão

Este repositório reúne dois projetos essenciais de Sistemas Operacionais:

✔️ Um escalonador completo com filas, prioridades e quantum.
✔️ Um sistema robusto produtor–consumidor com sincronização real.

Ambos são implementados de forma didática, modular e com logs/saídas para análise.

