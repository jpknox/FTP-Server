package com.jpknox.server.storage.file.transition.factory;


import com.jpknox.server.storage.file.Transition;
import com.jpknox.server.storage.file.transition.concrete.*;
import com.jpknox.server.storage.file.transition.concrete.RootTransition;
import com.jpknox.server.storage.file.transition.concrete.UpTransition;
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
public class TransitionFactory {

    /**
     * Given the URL of a directory to navigate towards, this method will create
     * the necessary concrete (@code Transition} instances to get to
     * the desired directory.
     * @param commands
     * @return
     */
    public static Transition[] createTransitions(String commands) {
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
        Transition[] transitions = new Transition[segments.size() + startsAtRoot];

        //Account for navigation relative to root
        if (startsAtRoot == 1) {
            transitions[i++] = new RootTransition();
        }

        //Create all the transitions
        Iterator iter = segments.iterator();
        String command;
        while (iter.hasNext()) {
            command = (String)iter.next();
            if (command.equals("..")) {
                transitions[i++] = new UpTransition();
                continue;
            } else if (command.equals(".")) {
                transitions[i++] = new StationaryTransition();
                continue;
            } else if (Pattern.matches("(\\w+\\.\\w+)", command)) {
                transitions[i++] = new DestinationFileTransition(command);
                continue;
            } else {
                transitions[i++] = new DownTransition(command);
                continue;
            }
        }
        return transitions;
    }

}
