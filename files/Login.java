/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_dsa;

import java.sql.*; //para sa con, pst, at rs
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {

    private JLabel titlelbl, userlbl, passlbl;
    private JTextField usertf;
    private JPasswordField passtf;
    private JButton loginbtn, clearbtn;
    private JPanel panel;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    private void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_management","root","");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    Login() {
        Connect();//connector sa databaes
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setTitle("Login");

        titlelbl = new JLabel("Event Management System");
        titlelbl.setBounds(130, 30, 200, 30);
        add(titlelbl);

        userlbl = new JLabel("Username:");
        userlbl.setBounds(50, 90, 80, 30);
        add(userlbl);

        usertf = new JTextField();
        usertf.setBounds(130, 90, 240, 30);
        add(usertf);

        passlbl = new JLabel("Password:");
        passlbl.setBounds(50, 140, 80, 30);
        add(passlbl);

        passtf = new JPasswordField();
        passtf.setBounds(130, 140, 240, 30);
        add(passtf);

        loginbtn = new JButton("Login");
        loginbtn.setBounds(130, 200, 110, 30);
        loginbtn.addActionListener(this);
        add(loginbtn);

        clearbtn = new JButton("Clear");
        clearbtn.setBounds(260, 200, 110, 30);
        clearbtn.addActionListener(this);
        add(clearbtn);

        panel = new JPanel();
        panel.setBounds(25, 20, 385, 260);
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginbtn) {
            String Username = usertf.getText();
            String Password = new String(passtf.getPassword());
            
            if (Username.isEmpty() || Password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter username and password!");
                return;
            }
            
            try{
                pst = con.prepareStatement("SELECT * FROM login WHERE username=? AND password=?");
                pst.setString(1, Username);
                pst.setString(2, Password);
                rs = pst.executeQuery();
                
                if (rs.next()){
                    JOptionPane.showMessageDialog(null, "ACCESS GRANTED!");
                    Event_Management_System app = new Event_Management_System();
                    app.setVisible(true);
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(null, "ACCESS DENIED!");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (e.getSource() == clearbtn) {
            usertf.setText("");
            passtf.setText("");
        }
    }
}
