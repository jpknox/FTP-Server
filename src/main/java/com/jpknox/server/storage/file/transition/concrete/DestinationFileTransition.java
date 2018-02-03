package com.jpknox.server.storage.file.transition.concrete;

import com.jpknox.server.storage.file.Transition;
import com.jpknox.server.utility.FTPServerConfig;
import com.jpknox.server.utility.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by JoaoPaulo on 03-Feb-18.
 */
public class DestinationFileTransition implements Transition {

    private String next;

    public DestinationFileTransition(String next) {
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
        File returnFile = new File(current.getPath() + File.separator + correctCasingName);
        if (returnFile.isFile()) {
            Logger.log("Switching to destination file named " + current + ".");
            return returnFile;
        } else {
            return null;
        }
    }
}
