package com.jpknox.server.storage.refactor.file.transition.factory;


import com.jpknox.server.storage.refactor.file.DirectoryTransition;
import com.jpknox.server.storage.refactor.file.transition.concrete.DownDirectoryTransition;
import com.jpknox.server.storage.refactor.file.transition.concrete.RootDirectoryTransition;
import com.jpknox.server.storage.refactor.file.transition.concrete.StationaryDirectoryTransition;
import com.jpknox.server.storage.refactor.file.transition.concrete.UpDirectoryTransition;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by JoaoPaulo on 04-Jan-18.
 */
public class DirectoryTransitionFactory {

    public static DirectoryTransition[] createDirectoryTransitions(String[] commands) {



        DirectoryTransition[] directoryTransitions = new DirectoryTransition[commands.length];
        int i = 0;
        for (String command : commands) {
            if (command.equals("..")) {
                directoryTransitions[i++] = new UpDirectoryTransition();
                continue;
            } else if (command.equals("")) {
                directoryTransitions[i++] = new RootDirectoryTransition();
                continue;
            } else if (command.equals(".")) {
                directoryTransitions[i++] = new StationaryDirectoryTransition();
                continue;
            } else {
                directoryTransitions[i++] = new DownDirectoryTransition(command);
                continue;
            }
        }
        return directoryTransitions;
    }

    /**
     * Given the URL of a directory to navigate towards, this method will create
     * the necessary concrete (@code DirectoryTransition} instances to get to
     * the desired directory.
     * @param commands
     * @return
     */
    public static DirectoryTransition[] createDirectoryTransitions(String commands) {
        //Remove quotes " and '
        List<String> quotesToRemove = Arrays.asList("\"", "\'");
        String noQuotesCommands = Pattern.compile("")
                                         .splitAsStream(commands)
                                         .filter(s -> !quotesToRemove.contains(s))
                                         .collect(Collectors.joining());
        //Split command into tokens
        LinkedList segments = new LinkedList<String>(Arrays.asList(
                StringUtils.split(noQuotesCommands, "\\\\|/|" + System.getProperty("line.separator"))));

        //Find if navigation is relative to root
        int startsAtRoot = 0;
        if (Stream.of("\\", "/", File.separator).anyMatch(noQuotesCommands.substring(0, 1)::equals)) {
            startsAtRoot = 1;
        }

        int i = 0;
        DirectoryTransition[] transitions = new DirectoryTransition[segments.size() + startsAtRoot];

        //Account for navigation relative to root
        if (startsAtRoot == 1) {
            transitions[i++] = new RootDirectoryTransition();
        }

        //Create all the transitions
        Iterator iter = segments.iterator();
        String command;
        while (iter.hasNext()) {
            command = (String)iter.next();
            if (command.equals("..")) {
                transitions[i++] = new UpDirectoryTransition();
                continue;
            } else if (command.equals(".")) {
                transitions[i++] = new StationaryDirectoryTransition();
                continue;
            } else {
                transitions[i++] = new DownDirectoryTransition(command);
                continue;
            }
        }
        return transitions;
    }

}
