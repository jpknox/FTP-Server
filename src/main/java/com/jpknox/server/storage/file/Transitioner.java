package com.jpknox.server.storage.file;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class Transitioner {

    public static File performTransitions(Transition[] transitions, File startingPoint) {
        File destination = new File(startingPoint.getPath());
        for (Transition transition : transitions) {
            destination = transition.transition(destination);
        }

        return destination;
    }

}
