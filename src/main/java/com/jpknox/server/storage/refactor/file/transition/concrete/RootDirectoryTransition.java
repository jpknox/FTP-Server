package com.jpknox.server.storage.refactor.file.transition.concrete;


import com.jpknox.server.storage.refactor.file.DirectoryTransition;
import com.jpknox.server.utility.FTPServerConfig;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class RootDirectoryTransition implements DirectoryTransition {

    @Override
    public File transition(File current) {
        System.out.println("Switching to root directory " + FTPServerConfig.root.getPath() + ".");
        return new File(FTPServerConfig.root.getPath());
    }
}
