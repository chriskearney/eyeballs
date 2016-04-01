package com.comandante.eyeballs;

import com.dropbox.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class DropwizardToken {

    public static void main(String[] args) throws IOException, DbxException {

        if (args.length < 2) {
            System.out.println("Please provide a Dropbox appKey and an appSecret");
            printHelp();
            System.exit(1);
        }

        String appKey = args[0];
        String appSecret = args[1];

        System.out.println("Using App Key: " + appKey);
        System.out.println("Using App Secret: " + appSecret);

        DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.getAccessToken();
        System.out.println("\r\n ACCESS TOKEN: " + accessToken);
        System.out.println("Save " + accessToken + " in the eyeballs.yaml file as dropboxAccessToken");
    }

    private static void printHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dropwizard access token retrieval program.\r\n\r\n");
        sb.append("Register Eyeballs as a Dropbox app to obtain key/secret: https://www.dropbox.com/developers-v1/apps\r\n\r\n");
        sb.append("java -cp ./eyeballs.jar com.comandante.eyeballs.DropwizardToken APP_KEY APPSECRET\r\n");
        System.out.printf(sb.toString());
    }

}


