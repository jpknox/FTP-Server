package com.jpknox.server.storage.file;

import java.io.File;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public interface Transition {

    File transition(File current);

}
