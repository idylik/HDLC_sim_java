import java.io.FileNotFoundException;

//Classe qui contient la classe statique Tests
public class IntercepteurTests {

    //Classe qui contient les tests effectues
    public static class Tests {

        private static boolean modeTest = true;
        private static int compteurEnvois = 0;

        public Tests() throws FileNotFoundException {
        }

        public static boolean isModeTest() {
            return modeTest;
        }

        public static void setModeTest(boolean modeTest) {
            Tests.modeTest = modeTest;
        }

        //Retourne une trame modifiee, ou String vide si trame perdue
        public static String intercepterTrameSortante(String expediteur, String trame) {

            char type = (char) Integer.parseInt(trame.substring(8, 16), 2);
            int num = Integer.parseInt(trame.substring(16, 24), 2);
            //System.out.println(compteurEnvois+": "+expediteur+" "+type+" "+num+" "+trame);
            //System.out.println(trame);

            switch (compteurEnvois) {
                case 6: //Trame I erronee
                    trame = "011111100100100100000100011000110110100101101110011101010111010101101001111010000110110101100101001000000110110001101001011001110110111001100101010011010010000101111110";
                    break;
                case 15: //Trame I perdue
                    trame = "";
                    break;
                case 25: //Trame RR erronee
                    trame = "01111110010000010000000000110110011111010101111110";
                    break;
                case 26: //Trame P-bit perdue
                    trame = "";
                    break;
                case 28: //Trame RR perdue
                    trame = "";
                    break;
                case 33: //Trame I perdue
                    trame = "";
                    break;
                case 35: //Trame REJ perdue
                    trame = "";
                    break;
                case 50: //Trame I erronee
                    trame = "0111111001001001000000110100100001100001011010110110100101101101001001100110000001111110";
                    break;
                case 53: //Trame REJ erronee
                    trame = "0111111001011010000000100100100010011111001111110";
                    break;
                case 63: //Trame P erronee
                    trame = "0111111001010000000000000000111010111110101111010";
                    break;
            }
            compteurEnvois++;
            return trame;
        }
    }
}