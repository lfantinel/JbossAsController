package br.com.fantinel.jboss.as.controller;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;


public class AuthenticationCallbackHandler implements CallbackHandler {

    private final String realm;
    private final String username;
    private final char[] password;

    public AuthenticationCallbackHandler(final String username, String password, final String realm) {
        this.username = username;
        this.password = password.toCharArray();
        this.realm = realm;
    }

    public AuthenticationCallbackHandler(final String username, final String password) {
        this(username, password, null);
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback current : callbacks) {
            if (current instanceof RealmCallback) {
                RealmCallback realmCallback = (RealmCallback) current;
                if (realm == null || realm.trim().length() == 0) {
                    String defaultRealm = realmCallback.getDefaultText();
                    realmCallback.setText(defaultRealm);
                } else {
                    realmCallback.setText(realm);
                }
            } else if (current instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) current;
                nameCallback.setName(username);
            } else if (current instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) current;
                passwordCallback.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(current);
            }
        }
    }
}