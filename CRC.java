import java.math.BigInteger;

//Contenant de la classe statique CalculCRC
public class CRC {

    //Sert a calculer et a verifier le CRC d’une trame
    public static class CalculCRC {

        //Constante de division (denominateur)
        private static BigInteger polynome = new BigInteger("10001000000100001", 2);

        //Prends un nombre binaire a diviser et retourne le resultat binaire de la division
        public static BigInteger diviser(BigInteger numerateur, boolean encoder) {
            int longueurDenominateur = polynome.bitLength(); // longueur du denominateur

            BigInteger numerateurShifte;
            if (encoder) {
                // padding du numerateur avec des 0 a droite
                numerateurShifte = numerateur.shiftLeft(longueurDenominateur - 1);
            } else {
                numerateurShifte = numerateur;
            }
            // Division iterative par le denominateur
            BigInteger denominateurShifte;
            do {
                denominateurShifte = polynome.shiftLeft(numerateurShifte.bitLength() - longueurDenominateur);

                numerateurShifte = numerateurShifte.xor(denominateurShifte);
            } while ((numerateurShifte.bitLength() >= longueurDenominateur));
            return numerateurShifte;
        }

        //Verifie si une trame est valide ou a ete endommagee/modifiee
        public static boolean trameCorrecte(BigInteger trame) {
            BigInteger res = diviser(trame, false);
            return res.intValue() == 0;
        }
    }
}