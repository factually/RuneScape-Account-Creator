package core;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class Authentication {
    private JTextField codeTextField;
    private static boolean authed = false;


    public void checkAuth() throws IOException {
        String code = this.codeTextField.getText();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://website", "", "");
            PreparedStatement ps = connection.prepareStatement("SQLStatement");
            ps.setString(1, code);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                authed = true;
            }
        }
        catch (SQLException result) {
            result.printStackTrace();
        }
        switch (code) {
            case "A":
                authed = true;
                break;
            case "":
                JOptionPane.showMessageDialog(null, "Please enter something!", "ERROR ", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid code", "ERROR ", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
        if (authed) {
            System.out.println("Authed");

        } else{
            System.out.println("Auth fail");
        }
    }

    public static boolean isAuthed() {
        return authed;
    }
}


