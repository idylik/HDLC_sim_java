import java.io.IOException;
import java.io.PrintWriter;

//Classe abstraite qui definit les entites qui communiquent ensemble (emetteur, Recepteur)
public abstract class EntiteComm {

    protected PrintWriter messageOut;
    protected int nbBitsNum = 3;
    protected int modulo = (int) Math.pow(2, nbBitsNum);
    protected int tailleFenetre = modulo - 1;
    protected boolean connexionEtablie = false;
    protected Encodeur encodeur;

    public EntiteComm() {
        encodeur = new Encodeur();
    }

    //Ferme le Socket
    public abstract void closeConnection() throws IOException;

    //Implemente les actions a entreprendre selon la trame recue et son type
    public abstract void traiterTrame(Trame trame) throws IOException;
}