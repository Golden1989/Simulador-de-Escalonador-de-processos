Simulador de Escalonamento de Processos

Este projeto implementa um simulador de escalonamento de processos em Java, utilizando fila de prioridade para a CPU e fila FIFO para operações de I/O.
Ele reproduz o funcionamento de um sistema operacional que gerencia processos com diferentes tempos de chegada, prioridades, tempo de CPU e de I/O.

🖥️ Funcionalidades

Leitura de processos a partir de um arquivo de entrada (input.txt).

Escalonamento por prioridade (maior prioridade = executado primeiro).

Fatia de tempo (quantum) para CPU (3 ciclos) e I/O (6 ciclos).

Processos passam entre as filas de CPU e I/O até serem concluídos.

Geração de arquivo de saída (output.txt) com o tempo de finalização de cada processo.

📂 Estrutura do Código

Scheduler.java → Contém a lógica principal da simulação.

Process.java → Representa um processo, com atributos como id, arrival, remCPU, remIO e priority.

SimpleQueue.java → Implementa uma fila FIFO simples.

PriorityReadyQueue.java → Implementa uma fila de prontos baseada em prioridade (usa várias filas FIFO, uma por prioridade).

📥 Formato do Arquivo de Entrada (input.txt)

Cada linha do arquivo deve conter as informações de um processo, separadas por ;:

id;arrival;tIO;tCPU;priority

Exemplos:
1;0;4;6;2
2;2;3;5;1
3;4;2;8;3


Onde:

id → Identificador do processo.

arrival → Tempo de chegada no sistema.

tIO → Tempo total de I/O necessário.

tCPU → Tempo total de CPU necessário.

priority → Prioridade (quanto maior, mais alta).

📤 Saída (output.txt)

O arquivo de saída lista o tempo em que cada processo finalizou:

tempo_saida;id

Exemplo:
12;1
18;2
25;3

▶️ Como Executar

Compile os arquivos Java:

   javac *.java


Execute a simulação passando o arquivo de entrada e saída:

  java Scheduler input.txt output.txt


Se não passar parâmetros, por padrão serão usados:

Entrada → input.txt

Saída → output.txt

🔧 Ajustes Possíveis

Alterar o quantum da CPU (CPU_QUANTUM) no código (Scheduler.java).

Alterar o quantum de I/O (IO_QUANTUM).

Adicionar novos processos no arquivo input.txt.

📚 Conceitos Envolvidos

Escalonamento de processos (CPU e I/O).

Filas de prioridade e FIFO.

Simulação de tempo discreto (cada iteração do loop = 1 ciclo de tempo).

Quantum (time slice) para limitar quanto tempo cada processo pode ocupar a CPU/I/O antes de revezar.

🚀 Tecnologias

Java (código escrito sem uso de coleções da biblioteca padrão, apenas arrays e filas próprias).
