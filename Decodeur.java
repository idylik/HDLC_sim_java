import java.math.BigInteger;

//Transforme une chaîne recue en objet de type Trame
public class Decodeur {

    public Decodeur() { }

    //Extrait et transforme les caracteres pour construire un objet de type Trame
    public Trame getTrame(String trameBrute) {

        //1) Verifier que les deux flags sont valides
        String f = "01111110";
        String flagDebut = trameBrute.substring(0, 8);
        String flagFin = trameBrute.substring(trameBrute.length() - 8);
        if (!flagDebut.equals(f) || !flagFin.equals(f)) {
            System.out.println("Decodeur: Trame erronee");
            return new Trame('E', '0', ""); //Trame Erreur
        }

        //2) Enlever Bit Stuffing (ailleurs que dans les flags)
        String trameSansFlags = trameBrute.substring(8, trameBrute.length() - 8);
        String trameSansStuffing = trameSansFlags.replaceAll("111110", "11111");

        //3) Verifier si CRC est valide
        BigInteger numerateur = new BigInteger(trameSansStuffing, 2);
        boolean trameCorrecte = CRC.CalculCRC.trameCorrecte(numerateur);

        if (!trameCorrecte) {
            System.out.println("Decodeur:   Trame erronee");
            return new Trame('E', '0', ""); // Trame Erreur
        }

        //4) Instancier le bon Type de Trame et le Num
        int typeInt = Integer.parseInt(trameSansStuffing.substring(0, 8), 2);
        char typeChar = (char) typeInt;

        int numInt = Integer.parseInt(trameSansStuffing.substring(8, 16), 2);

        String donneesBinaire = trameSansStuffing.substring(16, trameSansStuffing.length() - 16);

        //Convertir le String de 0 et 1 en String  de caracteres
        String donnees = "";
        for (int i = 0; i < donneesBinaire.length(); i = i + 8) {
            donnees += (char) Integer.parseInt(donneesBinaire.substring(i, i + 8), 2);
        }

        //Verifier que le type de trame est un type connu
        String types = "ICARFP";
        if (types.contains(Character.toString(typeChar))) {
            return new Trame(typeChar, numInt, donnees);
        } else {
            return new Trame('E', '0', ""); // Trame Erreur
        }
    }
}