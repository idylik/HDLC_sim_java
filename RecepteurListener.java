import java.io.IOException;
import java.net.Socket;

//Classe associee au Recepteur qui recoit les messages en provenant de l’Emetteur
public class RecepteurListener extends Listener implements Runnable {

    private Recepteur recepteur;

    public RecepteurListener(Socket sockExp, Recepteur rec) throws IOException {
        super(sockExp, rec);
        recepteur = rec;
    }

    @Override
    void decoderTrame(String messageRecu) {

        //Faire appel au decodeur pour avoir le bon type de trame
        Trame trame = this.decodeur.getTrame(messageRecu);

        //Si la trame est erronee (type 'E') ne rien faire:
        if (trame.getType() == 'E') {
            return;
        }
        //Sinon, traiter la trame et generer la reponse
        recepteur.traiterTrame(trame);
    }
}