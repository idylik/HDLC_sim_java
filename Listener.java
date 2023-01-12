import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Classe abstraite qui recoit les messages arrivants pour chaque EntiteComm
public abstract class Listener implements Runnable {

    private BufferedReader messageIn;
    protected EntiteComm entitePremiere;
    protected Decodeur decodeur;

    public Listener(Socket sockExp, EntiteComm entiteP) throws IOException {
        this.decodeur = new Decodeur();
        entitePremiere = entiteP;
        messageIn = new BufferedReader(new InputStreamReader(sockExp.getInputStream()));
    }

    @Override
    public void run() {
        while (true) {
            String messageRecu = null;
            try {
                messageRecu = messageIn.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (messageRecu == null) {
                try {
                    entitePremiere.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } else {
                try {
                    decoderTrame(messageRecu);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Envoie les trames recues (sous forme de chaînes de 0 et 1) au Decodeur pour creer un objet de type Trame et
    //l’achemine au destinataire (Emetteur ou Recepteur selon le cas)
    abstract void decoderTrame(String messageRecu) throws IOException;
}