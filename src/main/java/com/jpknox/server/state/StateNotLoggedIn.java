package com.jpknox.server.state;

import com.jpknox.server.session.ClientSession;

/**
 * Created by João Paulo Knox on 24/09/2017.
 */
public class StateNotLoggedIn extends AbstractSessionState {

    public StateNotLoggedIn(ClientSession session) {
        super(session);
    }

}
