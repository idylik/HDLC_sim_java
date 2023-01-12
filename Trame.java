//Contient les champs de chaque trame sous forme d’attributs
public class Trame {

    private char type; //type en char
    private int num; //numero en char, longueur en bits = 8
    private String donnees; //longueur variable

    public Trame(char type, int num, String donnees) {
        this.type = type;
        this.num = num;
        this.donnees = donnees;
    }

    public char getType() {
        return this.type;
    }

    public int getNum() {
        return num;
    }

    public String getDonnees() {
        return donnees;
    }
}