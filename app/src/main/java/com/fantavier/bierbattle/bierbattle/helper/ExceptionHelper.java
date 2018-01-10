package com.fantavier.bierbattle.bierbattle.helper;

/**
 * Created by Paul on 10.01.2018.
 */

public class ExceptionHelper {
    public static class VotingendException extends Exception {
        public VotingendException(){
            super("Abstimmung \n beendet");
        }
    }
}
