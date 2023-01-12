import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Entite qui envoie des trames a l’EmetteurListener
public class Recepteur extends EntiteComm implements Runnable {

    private static int PORT;
    private ServerSocket listener;
    private Socket socketEmetteur;
    private Thread recepteurListenerThread;
    private int dernierNumAck = -1;
    private int fenetreDebut = 0;
    private int fenetreFin = tailleFenetre - 1;
    private boolean rejetTramesISuivantes = false;

    public Recepteur(int port) throws IOException {
        PORT = port;
    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socketEmetteur = listener.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Messages envoyes a l'emetteur
        try {
            messageOut = new PrintWriter(socketEmetteur.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Creer recepteur listener:
        RecepteurListener recepteurListener = null;
        try {
            recepteurListener = new RecepteurListener(socketEmetteur, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recepteurListenerThread = new Thread(recepteurListener);
        recepteurListenerThread.start();
    }

    //Traiter les trames recues
    public void traiterTrame(Trame trame) {

        char type = trame.getType();
        int numRecu = trame.getNum();

        switch (type) {
            //DEMANDE CONNEXION
            case 'C':
                Trame connexionAcceptation = new Trame('C', 0, "");
                envoiTrame(connexionAcceptation);
                connexionEtablie = true;
                System.out.print("[RECEPTEUR] Demande de connexion recue");
                if (numRecu == 0) {
                    System.out.println(" Go-Back-N = true");
                }
                break;
            //TRAME INFORMATION
            case 'I':
                //Ignorer trames qui viennent apres REJ:
                if (rejetTramesISuivantes && numRecu % modulo > (dernierNumAck + 1) % modulo) {
                    System.out.println("[RECEPTEUR] Trame " + numRecu + " ignoree.");
                    return;
                    //REJ:
                } else if (numRecu % modulo > (dernierNumAck + 1) % modulo) {
                    rejetTramesISuivantes = true;
                    Trame REJ = new Trame('R', (dernierNumAck + 1) % modulo, "");
                    envoiTrame(REJ);
                    fenetreDebut = (dernierNumAck + 1) % modulo;
                    fenetreFin = (fenetreDebut + tailleFenetre - 1) % modulo;
                    System.out.println("[RECEPTEUR] Rejet trame " + numRecu + ". Renvoyer a partir de " + fenetreDebut);
                    //RR:
                } else if (numRecu == fenetreFin) {
                    Trame RR = new Trame('A', (numRecu + 1) % modulo, "");
                    envoiTrame(RR);
                    dernierNumAck = numRecu;
                    fenetreDebut = (dernierNumAck + 1) % modulo;
                    fenetreFin = (fenetreDebut + tailleFenetre - 1) % modulo;
                    System.out.println("[RECEPTEUR] Trame " + numRecu + " recue. Pret a recevoir trame " + (numRecu + 1) % modulo + ". Message recu: " + trame.getDonnees());
                } else {
                    System.out.println("[RECEPTEUR] Trame " + numRecu + " recue." + " Message recu: " + trame.getDonnees());
                    dernierNumAck = numRecu;
                    rejetTramesISuivantes = false;
                }
                break;
            //FIN CONNEXION
            case 'F':
                Trame trameF = new Trame('F', 0, "");
                envoiTrame(trameF);
                System.out.println("[RECEPTEUR] Fin de la communication acceptee");
                break;
            //TRAME P
            case 'P':
                //Repondre par RR en indiquant le prochain Num attendu:
                System.out.println("[RECEPTEUR] Trame P recue. Trame attendue: " + (dernierNumAck + 1) % modulo);
                Trame RR = new Trame('A', (dernierNumAck + 1) % modulo, "");
                envoiTrame(RR);
                break;
        }
    }

    //Envoie une seule trame a l’EmetteurListener
    private void envoiTrame(Trame trame) {
        String trameString = encodeur.getString(trame);

        if (IntercepteurTests.Tests.isModeTest()) {
            trameString = IntercepteurTests.Tests.intercepterTrameSortante("REC", trameString);
        }
        if (!trameString.equals("")) {
            System.out.println("[RECEPTEUR] Trame " + trame.getType() + " " + trame.getNum() + " envoyee.");
            messageOut.println(trameString);
        } else {
            System.out.println("[RECEPTEUR] Trame " + trame.getType() + " " + trame.getNum() + " perdue.");
        }
    }

    //Ferme le Socket
    public void closeConnection() throws IOException {
        recepteurListenerThread.interrupt();
        if (!socketEmetteur.isClosed()) {
            socketEmetteur.close();
        }
        if (!listener.isClosed()) {
            listener.close();
        }
        System.out.println("[RECEPTEUR] Fin");
    }
}