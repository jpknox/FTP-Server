package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.DirectoryTransition;

import java.io.File;
import java.io.IOException;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class DownDirectoryTransition implements DirectoryTransition {

    private String next;

    public DownDirectoryTransition(String next) {
        this.next = next;
    }

    @Override
    public File transition(File current) {
        String correctCasingName = next;
        try {
            correctCasingName = (new File(current.getPath() + File.separator + next).getCanonicalFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File nextDir = new File(current.getPath() + File.separator + correctCasingName);
        if (nextDir.isDirectory()) {
            System.out.println("Switching to subdirectory " + nextDir.getPath() + ".");
            return nextDir;
        } else {
            return null;
        }
    }
}
