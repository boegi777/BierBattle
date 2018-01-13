package com.fantavier.bierbattle.bierbattle.helper;

/**
 * Created by Paul on 10.01.2018.
 */

public class ExceptionHelper {
    public static class VotingendException extends Exception {
        public VotingendException(){
            super("Abstimmung\n beendet");
        }
    }

    public static class StarttimeException extends Exception {
        public StarttimeException(){
            super("Termin aktiv");
        }
    }

    public static class AppointmentStartsException extends Exception {
        public AppointmentStartsException(){
            super("Aktuell läuft kein Termin");
        }
    }

    public static class MemberNotFoundException extends Exception {
        public MemberNotFoundException(){
            super("Gruppenmitglied existiert nicht");
        }
    }

    public static class ThreadListException extends Exception {
        public ThreadListException(){
            super("ThreadList wurde nicht initialisiert");
        }
    }
}
