package utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.sql.*;
import java.util.Random;

public class Utils {

    public static String[] readFileLines(File file) throws IOException {
        return Files.readAllLines(file.toPath()).toArray(new String[0]);
    }


    public static <T> T randomFromArray(T[] array, Random random) {
        if (array.length == 1) {
            return array[0];
        }
        if (array.length > 1) {
            return array[random.nextInt(array.length - 1)];
        }
        return null;
    }

    public static <T> T readFromArray(T[] array, int i) {
        if (array.length == 1) {
            return array[0];
        }
        if (array.length > 1) {
            return array[i];
        }
        return null;
    }

    public static void createFileIfNotExists(File file) throws IOException {
        if (!file.exists() && file.createNewFile()) {
            System.out.println("Created file: " + file.getName());
        }
    }

    public static char randomFromArrayChar(final char[] array, final Random random) {
        if (array.length == 1) {
            return array[0];
        }
        if (array.length > 1) {
            return array[random.nextInt(array.length - 1)];
        }
        return '\0';
    }

    public void switchProxy(String ip, String port) {
        System.out.println("Switching TO " + ip + ":" + port);
        System.getProperties().put("https.proxyHost", ip);
        System.getProperties().put("https.proxyPort", port);
        System.getProperties().put("https.proxySet", "true");
    }

    public void switchProxy(String ip, String port, String username, String password) {

        System.out.println("Switching to " + ip + ":" + port + ":" + username + ":" + password);

        System.setProperty("socksProxyHost", ip);
        System.setProperty("socksProxyPort", port);
        System.setProperty("java.net.socks.username", username);
        System.setProperty("java.net.socks.password", password);
        Authenticator.setDefault(new ProxyAuth(username, password));
    }


    private void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component.getClass().getName().equals("javax.swing.JPanel")) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    public class ProxyAuth extends Authenticator {
        private PasswordAuthentication auth;

        private ProxyAuth(String user, String password) {
            auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return auth;
        }
    }


}