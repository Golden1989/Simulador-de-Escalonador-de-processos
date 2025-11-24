/*
 Isabella Campos Bueno 22307795
 Kauanne Julia Ledo Cavalcante 22306287
 Luiz Felipe Campos da Silva 22310854
 Carolina Marques Vinhal de Carvalho 22303413
 Letícia Seto Yen 22306613
*/








public class Main {
    public static void main(String[] args) {
        try {
            Buffer buffer = new Buffer(7, "log.txt");

            Produtor produtor = new Produtor(buffer, 15, "Produtor-1");
            Consumidor consumidor = new Consumidor(buffer, 12, "Consumidor-1");

            produtor.start();
            consumidor.start();

            produtor.join();
            consumidor.join();

            buffer.fecharLog();

            System.out.println("Execução concluída. Verifique o arquivo log.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
