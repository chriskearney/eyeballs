package com.comandante.eyeballs.api;

import com.comandante.eyeballs.EyeballsConfiguration;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.security.auth.Subject;
import java.security.Principal;

public class BasicAuthenticator implements Authenticator<BasicCredentials, BasicAuthenticator.EyeballUser> {

    private final EyeballsConfiguration eyeballsConfiguration;

    public BasicAuthenticator(EyeballsConfiguration eyeballsConfiguration) {
        this.eyeballsConfiguration = eyeballsConfiguration;
    }

    @Override
    public Optional<EyeballUser> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        String password = basicCredentials.getPassword();
        String username = basicCredentials.getUsername();
        if (username.equals(eyeballsConfiguration.getUsername()) && password.equals(eyeballsConfiguration.getPassword())) {
            return Optional.of(new EyeballUser());
        }
        return Optional.absent();
    }

    public static class EyeballUser implements Principal {

        private String name = "eyeballs-user";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean implies(Subject subject) {
            return false;
        }
    }
}
