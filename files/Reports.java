/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_dsa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Reports extends JFrame implements ActionListener{
    
    private JLabel evtlbl;
    private JButton attrepbtn, fbrepbtn, evtmainbtn, attdsbtn;
    private JComboBox evtcbb;
    private JTextArea report;
    private JScrollPane reportspane;
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    private void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_management","root","");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Reports(){
        Connect();
        setSize(940, 660);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setTitle("Reports");
        
        evtlbl = new JLabel("Event: ");
        evtlbl.setBounds(15, 10, 50, 30);
        add(evtlbl);
        
        evtcbb = new JComboBox<>();
        evtcbb.setBounds(65, 15, 160, 25);
        add(evtcbb);
        loadEventCombo();
        
        attrepbtn = new JButton("Attendance Report");
        attrepbtn.setBounds(235, 15, 150, 25);
        attrepbtn.addActionListener(this);
        add(attrepbtn);
        
        fbrepbtn = new JButton("Feedback Report");
        fbrepbtn.setBounds(400, 15, 150, 25);
        fbrepbtn.addActionListener(this);
        add(fbrepbtn);
        
        //report
        report = new JTextArea();
        report.setEditable(false);
        report.setText("No report yet.");
        
        reportspane = new JScrollPane(report);
        reportspane.setBounds(10, 65, 900, 520);
        reportspane.setSize(900,520);
        add(reportspane);
        
        //button para maka balik sa eventmngmntsys or attendees
        evtmainbtn = new JButton("Back to Event");
        evtmainbtn.setBounds(620, 10, 130, 30);
        evtmainbtn.addActionListener(this);
        add(evtmainbtn);
        
        attdsbtn = new JButton("Back to Attendees");
        attdsbtn.setBounds(760, 10, 140, 30);
        attdsbtn.addActionListener(this);
        add(attdsbtn);
        
            
        
}

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == evtmainbtn){
            Event_Management_System app = new Event_Management_System();
            app.setVisible(true);
            dispose();
        }else if(e.getSource() == attdsbtn){
            attendees app = new attendees();
            app.setVisible(true);
            dispose();
        }else if(e.getSource() == attrepbtn){
            attendanceReport();
        }else if(e.getSource() == fbrepbtn){
            feedbackReport();
        }
                
    }
    
    //kinukuha ang mga event sa database para sa combo box
    private void loadEventCombo(){
        try{
            pst = con.prepareStatement("SELECT id, name FROM evt");
            rs = pst.executeQuery();
            
            while(rs.next()){
                evtcbb.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //id ng napiling event, -1 kung wala
    private int getEventId(){
        if (evtcbb.getSelectedItem() == null){
            return -1;
        }
        return Integer.parseInt(evtcbb.getSelectedItem().toString().split(" - ")[0]);
    }
    
    private void attendanceReport(){
        int eventId = getEventId();
        if (eventId == -1){
            JOptionPane.showMessageDialog(null, "Please select an event!");
            return;
        }
        
        try{
            pst = con.prepareStatement("SELECT name, date, category, capacity FROM evt WHERE id=?");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            rs.next();
            
            String text = "ATTENDANCE REPORT\n";
            text += "Event: " + rs.getString("name") + "\n";
            text += "Date: " + rs.getString("date") + "\n";
            text += "Category: " + rs.getString("category") + "\n";
            text += "Capacity: " + rs.getInt("capacity") + "\n\n";
            
            pst = con.prepareStatement("SELECT name, email, priority, checked_in, status FROM attendee WHERE event_id=? ORDER BY id");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            
            int registered = 0, checkedIn = 0, waiting = 0;
            String list = "";
            while(rs.next()){
                if (rs.getString("status").equals("Registered")){
                    registered++;
                    String checked = "Absent";
                    if (rs.getInt("checked_in") == 1){
                        checkedIn++;
                        checked = "Present";
                    }
                    list += rs.getString("name") + " | " + rs.getString("email") + " | " + rs.getString("priority") + " | " + checked + "\n";
                } else {
                    waiting++;
                }
            }
            
            text += "Registered: " + registered + "\n";
            text += "Checked in: " + checkedIn + "\n";
            text += "Not checked in: " + (registered - checkedIn) + "\n";
            text += "On waiting list: " + waiting + "\n\n";
            text += "Registered Attendees:\n";
            if (list.isEmpty()){
                text += "None\n";
            } else {
                text += list;
            }
            
            report.setText(text);
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void feedbackReport(){
        int eventId = getEventId();
        if (eventId == -1){
            JOptionPane.showMessageDialog(null, "Please select an event!");
            return;
        }
        
        try{
            pst = con.prepareStatement("SELECT name FROM evt WHERE id=?");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            rs.next();
            
            String text = "FEEDBACK REPORT\n";
            text += "Event: " + rs.getString("name") + "\n\n";
            
            pst = con.prepareStatement("SELECT name, rating FROM attendee WHERE event_id=? AND rating > 0 ORDER BY id");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            
            int count = 0, total = 0;
            String list = "";
            while(rs.next()){
                count++;
                total += rs.getInt("rating");
                list += rs.getString("name") + " - " + rs.getInt("rating") + "/5\n";
            }
            
            if (count == 0){
                text += "No feedback yet.";
            } else {
                text += "Feedbacks received: " + count + "\n";
                text += "Average rating: " + String.format("%.2f", (double) total / count) + " / 5\n\n";
                text += "Ratings:\n" + list;
            }
            
            report.setText(text);
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
