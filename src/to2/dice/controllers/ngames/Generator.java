package to2.dice.controllers.ngames;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Generator {

    private SecureRandom random;
    private int diceNumber;

    public Generator(int diceNumber) {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.diceNumber = diceNumber;
    }

    public int generateSum(){
        int sumToThrow = 0;

        for(int i = 0; i < diceNumber; i++ ){
            sumToThrow += Math.abs((new Integer(random.nextInt()) % 6)) + 1;
        }

        return sumToThrow;
    }

    public int generateMul(){
        int mulToThrow = 1;

        for(int i = 0 ; i < diceNumber; i++ ){
            mulToThrow *= Math.abs(new Integer(random.nextInt() % 6)) + 1;
        }

        return mulToThrow;
    }

    public int generatePlayerIndex(int length){
        return Math.abs(new Integer(random.nextInt() % length));
    }
}