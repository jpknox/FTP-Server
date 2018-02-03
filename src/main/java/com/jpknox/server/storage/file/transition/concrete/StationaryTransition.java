package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.Transition;

import java.io.File;

/**
 * Created by JoaoPaulo on 05-Jan-18.
 */
public class StationaryTransition implements Transition {


    @Override
    public File transition(File current) {
        return current;
    }
}
