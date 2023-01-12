import java.util.TimerTask;

class TimerHelper extends TimerTask {

    private char typeTrame;
    private int numTrame;
    private Emetteur emetteur;

    public TimerHelper(Emetteur emetteur, char typeTrame, int numTrame) {
        this.emetteur = emetteur;
        this.typeTrame = typeTrame;
        this.numTrame = numTrame;
    }

    @Override
    public void run() {
        emetteur.temporisateurExpired(typeTrame);
    }
}