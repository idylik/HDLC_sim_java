import java.math.BigInteger;

//Transforme un objet de type Trame en chaîne prete a etre envoyee
public class Encodeur {

    public Encodeur() { }

    //Construit chaque champ de la chaîne qui servira de trame
    public String getString(Trame trame) {
        String f = "01111110";

        //Type: chaîne de 1 et 0 (longueur 8)
        String typeBinaire = String.format("%8s", Integer.toBinaryString(trame.getType())).
                replace(' ', '0');

        //Num: chaîne de 1 et 0 (longueur 8)
        String numBinaire = String.format("%8s", Integer.toBinaryString(trame.getNum())).
                replace(' ', '0');

        //Convertir caracteres en String de 1 et 0 (longueur 8 chaque caractere)
        String donneesBinaire = stringTostringBinaire(trame.getDonnees());

        BigInteger crc = CRC.CalculCRC.diviser(new BigInteger(typeBinaire + numBinaire + donneesBinaire, 2),
                true);
        String stringCRC = String.format("%16s", crc.toString(2)).replace(' ', '0');

        //Ajouter le stuffing:
        String trameStuffing = (typeBinaire + numBinaire + donneesBinaire + stringCRC).
                replaceAll("11111", "111110");

        return f + trameStuffing + f;
    }

    //Transforme un String quelconque en chaîne de 0 et 1
    public String stringTostringBinaire(String str) {
        String strBinaire = "";
        for (int i = 0; i < str.length(); i++) {
            strBinaire += String.format("%8s", Integer.toBinaryString(str.charAt(i))).replace(' ', '0');
        }
        return strBinaire;
    }
}
