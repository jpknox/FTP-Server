package com.jpknox.server.storage.file;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class DirectoryTransitioner {

    public static File performTransitions(DirectoryTransition[] transitions, File startingPoint) {
        File destination = new File(startingPoint.getPath());
        for (DirectoryTransition transition : transitions) {
            destination = transition.transition(destination);
        }

        return destination;
    }

}
