package com.fixit.feature.auth.presentation;

import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.UserRole;

public class AuthEvent {
    public enum Type {
        NAVIGATE_TO_CUSTOMER,
        NAVIGATE_TO_WORKER,
        REGISTER_SUCCESS
    }

    private final Type type;
    private final Session session;

    private AuthEvent(Type type, Session session) {
        this.type = type;
        this.session = session;
    }

    public static AuthEvent navigate(Session session) {
        if (session != null && session.getUser() != null && session.getUser().getRole() == UserRole.WORKER) {
            return new AuthEvent(Type.NAVIGATE_TO_WORKER, session);
        }
        return new AuthEvent(Type.NAVIGATE_TO_CUSTOMER, session);
    }

    public static AuthEvent registerSuccess(Session session) {
        return new AuthEvent(Type.REGISTER_SUCCESS, session);
    }

    public Type getType() {
        return type;
    }

    public Session getSession() {
        return session;
    }
}
