import java.io.IOException;
import java.util.Scanner;

public class ClientApp {

    static Scanner scanner = new Scanner(System.in);

    public static String readConsole() {
        String str = scanner.next();
        return str;
    }

    public static void main(String[] args) {
        boolean active = true;
        try {

            while (active) {
                Client client = new Client();
                String chosen = getString(client);
                while (!client.getAnswer().contains(Message.goodChoice())) {
                    chosen = getString(client);
                }
                if (chosen.charAt(0) == '0') {
                    break;
                }
                System.out.println("Choisissez la classe");
                String _class = readConsole();
                System.out.println("Choisissez la méthode");
                String method = readConsole();
                System.out.println("Donnez moi 1 paramètres (int)");
                String a = readConsole();
                System.out.println("Donnez moi 1 paramètres (int)");
                String b = readConsole();
                System.out.println(chosen);
                System.out.println(Message.getObjectColl());
                int i = Integer.parseInt(a);
                int j = Integer.parseInt(b);
                if (Message.getObjectColl().contains(chosen)) {
                    client.objectColl(_class, method, i, j);
                } else if (Message.getByteColl().contains(chosen)) {
                    client.byteColl(_class, method, i, j);
                } else if (Message.getSourceColl().contains(chosen)) {
                    client.sourceColl(_class, method, i, j);
                } else {
                    System.out.println("rien");
                }
                String answer = client.getAnswer();
                System.out.println("ANSWER =");
                System.out.println(answer +"\n\n");
                client.quit();
            }

        } catch (IOException e) {
            System.out.println("Problème dans l'instanciation du client");
            e.printStackTrace();
            return;
        }

    }

    private static String getString(Client client) throws IOException {
        System.out.println(Message.choices());
        String chosen = readConsole();
        client.getCommunication().write(chosen);
        return chosen;
    }

}
