import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//Entite qui envoie des trames au RecepteurListener
public class Emetteur extends EntiteComm implements Runnable {

    private static String RECEPTEUR_IP;
    private static int RECEPTEUR_PORT;
    private static String NOM_FICHIER;
    private Socket socketRecepteur;
    private Thread emetteurListenerThread;
    private ArrayList<String> lignesFichier;
    private int indexLigneDebut = 0;
    private int indexLigneFin;

    private Timer temporisateur = new Timer(); //laisser new Timer();
    private TimerTask tacheTemporisateur;

    public Emetteur(String recepteurIp, int port, String nomFichier) throws IOException, InterruptedException {
        RECEPTEUR_IP = recepteurIp;
        RECEPTEUR_PORT = port;
        NOM_FICHIER = System.getProperty("user.dir") + "/" + nomFichier;
        lignesFichier = new ArrayList<String>();
    }

    @Override
    public void run() {
        //Insere lignes du fichier dans ArrayList:
        try (BufferedReader br = new BufferedReader(new FileReader(NOM_FICHIER))) {
            String ligne;
            while (((ligne = br.readLine()) != null)) {
                lignesFichier.add(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexLigneFin = Math.min(lignesFichier.size() - 1, tailleFenetre - 1);

        try {
            socketRecepteur = new Socket(RECEPTEUR_IP, RECEPTEUR_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[EMETTEUR]  Pret");

        //Messages envoyes au recepteur
        try {
            messageOut = new PrintWriter(socketRecepteur.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Creer emetteur listener:
        EmetteurListener emetteurListener = null;
        try {
            emetteurListener = new EmetteurListener(socketRecepteur, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        emetteurListenerThread = new Thread(emetteurListener);
        emetteurListenerThread.start();

        //Debut de la communication
        envoiTrame(new Trame('C', 0, "0"), true);
    }

    //Envoie une seule trame au RecepteurListener, peut activer un temporisateur selon la valeur de createTemporisateur
    private void envoiTrame(Trame trame, boolean createTemporisateur) {
        String trameString = encodeur.getString(trame);

        if (IntercepteurTests.Tests.isModeTest()) {
            trameString = IntercepteurTests.Tests.intercepterTrameSortante("EME", trameString);
        }

        if (!trameString.equals("")) {
            System.out.println("[EMETTEUR]  Trame " + trame.getType() + " " + trame.getNum() + " envoyee.");
            messageOut.println(trameString);
        } else {
            System.out.println("[EMETTEUR]  Trame " + trame.getType() + " " + trame.getNum() + " perdue.");
        }

        if (createTemporisateur) {
            startTemporisateur(trame.getType(), trame.getNum());
        }
    }

    //Envoie plusieurs trames selon la grosseur de la fenetre
    public void envoiTramesI() {
        int i = indexLigneDebut;
        while (i <= indexLigneFin) {
            Trame trame = new Trame('I', i % modulo, lignesFichier.get(i));
            envoiTrame(trame, false);
            i++;
        }
        startTemporisateur('I', 999);
        //Si on est a la fin du fichier, laisser le temporisateur expirer
    }

    //Traiter les trames recues
    @Override
    public void traiterTrame(Trame trame) throws IOException {
        char type = trame.getType();
        int numRecu = trame.getNum();

        switch (type) {
            //DEMANDE DE CONNEXION
            case 'C':
                if (connexionEtablie) {
                    System.out.println("[EMETTEUR]  Connexion deja etablie");
                } else {
                    connexionEtablie = true;
                    System.out.println("[EMETTEUR]  Connexion etablie");
                    System.out.println("[EMETTEUR]  Debut de l'envoi des trames");
                    envoiTramesI();
                }
                break;
            //RR
            case 'A':
                cancelTemporisateur();
                //Si l'envoi precedent etait la fin du fichier:
                if (indexLigneFin == lignesFichier.size() - 1) {
                    //Si la derniere ligne est confirmee:
                    if ((indexLigneFin + 1) % modulo == numRecu) {
                        envoiTrame(new Trame('F', 0, ""), true);
                    } else {
                        indexLigneDebut = getLigneFichier(numRecu);
                        indexLigneFin = Math.min(indexLigneDebut + tailleFenetre - 1, lignesFichier.size() - 1);
                        envoiTramesI();
                    }
                } else { //Si on n'etait pas a la fin du fichier
                    if ((indexLigneFin + 1) % modulo == numRecu) {
                        indexLigneDebut = indexLigneFin + 1;
                    } else {
                        indexLigneDebut = getLigneFichier(numRecu);
                    }
                    indexLigneFin = Math.min(indexLigneDebut + tailleFenetre - 1, lignesFichier.size() - 1);

                    envoiTramesI();
                }
                break;
            //REJ
            case 'R':
                cancelTemporisateur();
                //On trouve l'index de la ligne du fichier correspondant au Num de la trame:
                indexLigneDebut = getLigneFichier(numRecu);
                indexLigneFin = Math.min(indexLigneDebut + tailleFenetre - 1, lignesFichier.size() - 1);
                envoiTramesI();
                break;
            //FIN CONNEXION
            case 'F':
                System.out.println("[eMETTEUR ] Fin de la communication confirmee");
                closeConnection();
                cancelTemporisateur();
                break;
        }
    }

    //Cree un nouveau temporisateur et annule le precedent
    private void startTemporisateur(char typeTrame, int numTrame) {
        cancelTemporisateur();
        temporisateur = new Timer();
        tacheTemporisateur = new TimerHelper(this, typeTrame, numTrame);
        temporisateur.schedule(tacheTemporisateur, 3000);
    }

    //Annule le temporisateur
    private void cancelTemporisateur() {
        temporisateur.cancel();
        temporisateur.purge();
        tacheTemporisateur = null;
    }

    //Definit le comportement a adopter si le temporisateur expire, suite a l’envoi d’un type de trame
    public void temporisateurExpired(char typeTrame) {
        System.out.println("[EMETTEUR]  Temporisateur expire");
        //Type d'action que l'on doit repeter si le temporisateur expire
        switch (typeTrame) {
            case 'C':
                envoiTrame(new Trame('C', 0, "0"), true);
                break;
            case 'P':
            case 'I':
                envoiTrame(new Trame('P', 0, ""), true);
                break;
            case 'F':
                envoiTrame(new Trame('F', 0, ""), true);
                break;
        }
    }

    //Retourne l’index de la ligne dans le fichier texte qui correspond au numero de la trame contenue
    //dans la fenetre d’envoi actuelle
    private int getLigneFichier(int numTrame) {
        for (int i = indexLigneDebut; i <= indexLigneFin; i++) {
            if (i % modulo == numTrame) {
                return i;
            }
        }
        return -1;
    }

    //Ferme le Socket
    public void closeConnection() throws IOException {
        emetteurListenerThread.interrupt();
        if (!socketRecepteur.isClosed()) {
            socketRecepteur.close();
        }
        System.out.println("[EMETTEUR]  Fin");
        System.exit(0);
    }
}