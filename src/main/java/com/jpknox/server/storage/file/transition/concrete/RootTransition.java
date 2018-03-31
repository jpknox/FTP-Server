package com.jpknox.server.storage.file.transition.concrete;


import com.jpknox.server.storage.file.Transition;
import com.jpknox.server.utility.FTPServerConfig;

import java.io.File;

import static com.jpknox.server.utility.Logger.log;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class RootTransition implements Transition {

    @Override
    public File transition(File current) {
        log("Switching to root directory " + FTPServerConfig.root.getPath() + ".");
        return new File(FTPServerConfig.root.getPath());
    }
}
