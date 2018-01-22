package com.jpknox.server.storage.file;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public interface DirectoryTransition {

    File transition(File current);

}
