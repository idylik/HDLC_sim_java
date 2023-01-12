import java.io.IOException;
import java.net.Socket;

//Classe associee a l’Emetteur qui recoit les messages en provenance du Recepteur
public class EmetteurListener extends Listener implements Runnable {

    private Emetteur emetteur;

    public EmetteurListener(Socket sockExp, Emetteur em) throws IOException {
        super(sockExp, em);
        emetteur = em;
    }

    //Envoie les trames recues (sous forme de chaînes de 0 et 1) au Decodeur
    //pour creer un objet de type Trame et l’achemine au destinataire (Emetteur ou Recepteur selon le cas)
    @Override
    void decoderTrame(String messageRecu) throws IOException {
        //Faire appel au decodeur pour avoir le bon type de trame
        Trame trame = this.decodeur.getTrame(messageRecu);

        //Si la trame est erronee (type 'E') ne rien faire:
        if (trame.getType() == 'E') {
            return;
        }
        //Envoyer la trame a emetteur:
        emetteur.traiterTrame(trame);
    }
}