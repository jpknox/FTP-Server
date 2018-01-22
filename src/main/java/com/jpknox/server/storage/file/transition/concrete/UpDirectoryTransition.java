package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.DirectoryTransition;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class UpDirectoryTransition implements DirectoryTransition {

    @Override
    public File transition(File current) {
        File parent = null;
        try {
            System.out.println("Switching to parent directory " + current.getParentFile().getPath() + ".");
            parent = current.getParentFile();
            return parent;
        } catch (NullPointerException e) {
            return current;
        }
    }
}
