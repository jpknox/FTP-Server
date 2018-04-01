package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.Transition;

import java.io.File;
import java.io.IOException;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class DownTransition implements Transition {

    private String next;

    public DownTransition(String next) {
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
            //log("Switching to subdirectory " + nextDir.getPath() + ".");
            return nextDir;
        } else if (nextDir.isFile()) {
            //log("Switching to file " + nextDir.getPath() + ".");
            return nextDir;
        } else {
            return null;
        }
    }
}
