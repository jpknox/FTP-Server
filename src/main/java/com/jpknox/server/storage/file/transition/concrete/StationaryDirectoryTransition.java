package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.DirectoryTransition;

import java.io.File;

/**
 * Created by JoaoPaulo on 05-Jan-18.
 */
public class StationaryDirectoryTransition implements DirectoryTransition {


    @Override
    public File transition(File current) {
        return current;
    }
}
