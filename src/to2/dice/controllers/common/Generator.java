package to2.dice.controllers.common;

import to2.dice.game.Dice;

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

        for(int i = 0; i < 5; i++ ){
            sumToThrow += Math.abs((new Integer(random.nextInt()) % 6)) + 1;
        }

        return sumToThrow;
    }

    public int generateMul(){
        int mulToThrow = 1;

        for(int i = 0 ; i < 5; i++ ){
            mulToThrow *= Math.abs(new Integer(random.nextInt() % 6)) + 1;
        }

        return mulToThrow;
    }

    public int generatePlayerIndex(int length){
        return Math.abs(new Integer(random.nextInt() % length));
    }

    public int diceThrow(){
        return Math.abs((new Integer(random.nextInt()) % 6)) + 1;
    }

    public Dice rollDice() {
        Dice dice = new Dice(this.diceNumber);

        int[] dice_tab = new int[this.diceNumber];
        for (int i = 0; i < this.diceNumber; i++) {
            dice_tab[i] = diceThrow();
        }

        dice.setDice(dice_tab);
        return dice;
    }
}