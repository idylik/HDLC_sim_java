/*
Michel Adant C1176
Maryna Starastsenka 20166402
 */

import java.io.IOException;
import java.util.Scanner;

//Classe principale qui contient la methode Main
public class ProtocoleHDLC {

    private static Scanner scanner = new Scanner(System.in);
    private Emetteur emetteur;
    private Recepteur recepteur;

    public static void main(String[] args) throws IOException, InterruptedException {

        //FONCTIONNEMENT EN MODE RAPIDE:
        /*int numPort = 9091;
        Recepteur recepteur = new Recepteur(numPort);
        Thread recepteurThread = new Thread(recepteur);
        recepteurThread.start();

        String nomMachine = "localhost";
        int numeroPort = 9091;
        String nomFichier = "fichier.txt";
        Emetteur emetteur = new Emetteur(nomMachine, numeroPort, nomFichier);
        Thread emetteurThread = new Thread(emetteur);
        emetteurThread.start();*/

        //FONCTIONNEMENT NORMAL
        //Lit l'input du clavier:
        whileLoop:
        while (true) {
            System.out.print("%");
            String commande = scanner.nextLine();
            String[] commandeMots = commande.split(" ");

            switch (commandeMots[0]) {
                case "exit":
                    break whileLoop;
                case "Sender":
                    String nomMachine = commandeMots[1];
                    int numeroPort = Integer.parseInt(commandeMots[2]);
                    String nomFichier = commandeMots[3];
                    Emetteur emetteur = new Emetteur(nomMachine, numeroPort, nomFichier);
                    Thread emetteurThread = new Thread(emetteur);
                    emetteurThread.start();
                    break;
                case "Receiver":
                    int numPort = Integer.parseInt(commandeMots[1]);
                    Recepteur recepteur = new Recepteur(numPort);
                    Thread recepteurThread = new Thread(recepteur);
                    recepteurThread.start();
                    break;
                case "test":
                    if (commandeMots[1].equals("on")) {
                        IntercepteurTests.Tests.setModeTest(true);
                        System.out.println("Mode test active");
                    } else if (commandeMots[1].equals("off")) {
                        IntercepteurTests.Tests.setModeTest(false);
                        System.out.println("Mode test desactive");
                    }
                    break;
                default:
                    System.out.println("Commande non reconnue");
            }
        }
        System.exit(0);
    }
}