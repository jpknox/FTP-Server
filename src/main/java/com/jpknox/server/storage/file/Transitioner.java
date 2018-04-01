package com.jpknox.server.storage.file;

import com.jpknox.server.storage.file.transition.concrete.DownTransition;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class Transitioner {

    public static File performTransitions(Transition[] transitions, File startingPoint, boolean makeNewDir) {
        File destination = new File(startingPoint.getPath());
        for (Transition transition : transitions) {
            if (makeNewDir && transition instanceof DownTransition) {
                destination = ((DownTransition)transition).transition(destination, makeNewDir);
            } else {
                destination = transition.transition(destination);
            }
        }

        return destination;
    }

}
