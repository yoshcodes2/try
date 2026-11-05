/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project_dsa;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class attendees extends JFrame implements ActionListener{

    private JLabel  evt, regAttlbl, waitlistlbl, attdlbl, namelbl, emaillbl, priolbl;
    private JTextField nametf, emailtf;
    private JComboBox priocbb;
    private JButton regbtn, checkinbtn, addfbbtn, cancelregbtn, evtmainbtn, rpbtn;
    private JPanel attdpnl;
    private JTable registeredtbl, waitingtbl;
    private DefaultTableModel wlmodel, rtmodel;
    private JComboBox evtcbb;
    private JScrollPane regscpane, waitscpane;
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    //linked list para sa registered attendees
    private LinkedList<Person> registeredList = new LinkedList<>();
    //priority queue para sa waiting list (VIP > Member > Regular)
    private PriorityQueue<Person> waitingQueue = new PriorityQueue<>((a, b) -> {
        if (a.rank() != b.rank()){
            return Integer.compare(a.rank(), b.rank());
        }
        return Integer.compare(a.id, b.id); //pareho ang priority, first come first served
    });
    
    private static class Person {
        int id, rating;
        String name, email, priority;
        boolean checkedIn;
        
        int rank(){
            if (priority.equals("VIP")){
                return 1;
            } else if (priority.equals("Member")){
                return 2;
            }
            return 3;
        }
    }
    
    private void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_management","root","");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    attendees(){
        Connect();
        setSize(940, 660);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setTitle("Attendance Details");
        
        evt = new JLabel("Event: ");
        evt.setBounds(15, 15, 45, 30);
        add(evt);
        
        evtcbb = new JComboBox<>();
        evtcbb.setBounds(60, 15, 250, 30);
        add(evtcbb);
        loadEventCombo();
        evtcbb.addActionListener(this);
        
        regAttlbl = new JLabel("Registered Attendees");
        regAttlbl.setBounds(20, 50, 150, 30);
        add(regAttlbl);
        
        //registed table
        rtmodel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Priority", "Checked In", "Rating"},0);
        
        registeredtbl = new JTable(rtmodel);
        
        regscpane = new JScrollPane(registeredtbl);
        regscpane.setBounds(10, 75, 900, 180);
        regscpane.setSize(900, 180);
        add(regscpane);
        
        //waitng list table
        
        waitlistlbl = new JLabel("Waiting list (Priority queue: VIP > Member > Regular)");
        waitlistlbl.setBounds(20, 250, 400, 30);
        add(waitlistlbl);
        
        wlmodel = new DefaultTableModel(new String[] {"Queue #", "Id", "Name", "Email", "Priority"},0);
        
        waitingtbl = new JTable(wlmodel);
        
        waitscpane = new JScrollPane(waitingtbl);
        waitscpane.setBounds(10, 275, 900, 180);
        waitscpane.setSize(900, 180);
        add(waitscpane);
        
        
        
        attdlbl = new JLabel("Attendee Details: ");
        attdlbl.setBounds(25, 465, 130, 30);
        add(attdlbl);
        
        namelbl = new JLabel("Name: ");
        namelbl.setBounds(35, 490, 80, 30);
        add(namelbl);
        
        nametf = new JTextField();
        nametf.setBounds(35, 520, 350, 30);
        add(nametf);
        
        emaillbl = new JLabel("Email: ");
        emaillbl.setBounds(405, 490, 80, 30);
        add(emaillbl);
        
        emailtf = new JTextField();
        emailtf.setBounds(405, 520, 300, 30);
        add(emailtf);
        
        priolbl = new JLabel("Priority: ");
        priolbl.setBounds(725, 490, 80, 30);
        add(priolbl);
        
        priocbb = new JComboBox<>(new String [] {"VIP","Member","Regular"});
        priocbb.setBounds(725, 520, 150, 30);
        add(priocbb);
        
        regbtn = new JButton("Register");
        regbtn.setBounds(35, 555, 90, 30);
        regbtn.addActionListener(this);
        add(regbtn);
        
        checkinbtn = new JButton("Check in Selected ");
        checkinbtn.setBounds(135, 555, 140, 30);
        checkinbtn.addActionListener(this);
        add(checkinbtn);
        
        addfbbtn = new JButton("Add Feedback");
        addfbbtn.setBounds(295, 555, 130, 30);
        addfbbtn.addActionListener(this);
        add(addfbbtn);
        
        cancelregbtn = new JButton("Cancel Register");
        cancelregbtn.setBounds(445, 555, 130, 30);
        cancelregbtn.addActionListener(this);
        add(cancelregbtn);
        
        attdpnl = new JPanel();
        attdpnl.setBounds(15, 465, 890, 130);
        attdpnl.setBorder(BorderFactory.createLineBorder(Color.gray));
        add(attdpnl);
        
        //button to back in eventmngmtsys
        evtmainbtn = new JButton("Back to Event");
        evtmainbtn.setBounds(630, 15, 130, 30);
        evtmainbtn.addActionListener(this);
        add(evtmainbtn);
       
        rpbtn = new JButton("Go to Reports");
        rpbtn.setBounds(770, 15, 130, 30);
        rpbtn.addActionListener(this);
        add(rpbtn);
        
        loadAttendees();
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == evtmainbtn){
           Event_Management_System app = new Event_Management_System();
           app.setVisible(true);
           dispose();
       }else if (e.getSource() == rpbtn){
           Reports app = new Reports();
           app.setVisible(true);
           dispose();
       }else if (e.getSource() == evtcbb){
           loadAttendees();
       }else if (e.getSource() == regbtn){
           registerAttendee();
       }else if (e.getSource() == checkinbtn){
           checkInAttendee();
       }else if (e.getSource() == addfbbtn){
           addFeedback();
       }else if (e.getSource() == cancelregbtn){
           cancelRegistration();
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
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //id ng napiling event, -1 kung wala
    private int getEventId(){
        if (evtcbb.getSelectedItem() == null){
            return -1;
        }
        return Integer.parseInt(evtcbb.getSelectedItem().toString().split(" - ")[0]);
    }
    
    private int getCapacity(int eventId){
        try{
            pst = con.prepareStatement("SELECT capacity FROM evt WHERE id=?");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            
            if (rs.next()){
                return rs.getInt("capacity");
            }
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    //ina-update ang bilang ng attendees at waiting sa events table
    private void updateEventCounts(int eventId){
        String sql = "UPDATE evt SET attendees=(SELECT COUNT(*) FROM attendee WHERE event_id=? AND status='Registered'), "
                + "waiting=(SELECT COUNT(*) FROM attendee WHERE event_id=? AND status='Waiting') WHERE id=?";
        try{
            pst = con.prepareStatement(sql);
            pst.setInt(1, eventId);
            pst.setInt(2, eventId);
            pst.setInt(3, eventId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //nilo-load ang linked list at priority queue, tapos ipapakita sa dalawang table
    private void loadAttendees(){
        registeredList.clear();
        waitingQueue.clear();
        rtmodel.setRowCount(0);
        wlmodel.setRowCount(0);
        
        int eventId = getEventId();
        if (eventId == -1){
            return;
        }
        
        try{
            pst = con.prepareStatement("SELECT id, name, email, priority, checked_in, rating, status FROM attendee WHERE event_id=? ORDER BY id");
            pst.setInt(1, eventId);
            rs = pst.executeQuery();
            
            while(rs.next()){
                Person p = new Person();
                p.id = rs.getInt("id");
                p.name = rs.getString("name");
                p.email = rs.getString("email");
                p.priority = rs.getString("priority");
                p.checkedIn = rs.getInt("checked_in") == 1;
                p.rating = rs.getInt("rating");
                
                if (rs.getString("status").equals("Registered")){
                    registeredList.add(p);
                } else {
                    waitingQueue.add(p);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (Person p : registeredList){
            rtmodel.addRow(new Object[]{
                p.id,
                p.name,
                p.email,
                p.priority,
                p.checkedIn ? "Yes" : "No",
                p.rating == 0 ? "-" : p.rating
            });
        }
        
        //kopya ng queue para hindi mawala ang laman habang nagpo-poll
        PriorityQueue<Person> copy = new PriorityQueue<>(waitingQueue);
        int queueNo = 1;
        while (!copy.isEmpty()){
            Person p = copy.poll();
            wlmodel.addRow(new Object[]{queueNo, p.id, p.name, p.email, p.priority});
            queueNo++;
        }
    }
    
    private void registerAttendee(){
        int eventId = getEventId();
        if (eventId == -1){
            JOptionPane.showMessageDialog(null, "Please select an event!");
            return;
        }
        
        String name = nametf.getText().trim();
        String email = emailtf.getText().trim();
        if (name.isEmpty() || email.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please enter name and email!");
            return;
        }
        if (!email.contains("@")){
            JOptionPane.showMessageDialog(null, "Please enter a valid email!");
            return;
        }
        
        try{
            pst = con.prepareStatement("SELECT id FROM attendee WHERE event_id=? AND email=?");
            pst.setInt(1, eventId);
            pst.setString(2, email);
            rs = pst.executeQuery();
            if (rs.next()){
                JOptionPane.showMessageDialog(null, "This email is already registered in this event!");
                return;
            }
            
            //kung puno na ang event, sa waiting list mapupunta
            String status = "Registered";
            if (registeredList.size() >= getCapacity(eventId)){
                status = "Waiting";
            }
            
            pst = con.prepareStatement("INSERT INTO attendee (name, email, priority, event_id, checked_in, rating, status) VALUES (?, ?, ?, ?, 0, 0, ?)");
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, priocbb.getSelectedItem().toString());
            pst.setInt(4, eventId);
            pst.setString(5, status);
            pst.executeUpdate();
            
            updateEventCounts(eventId);
            loadAttendees();
            nametf.setText("");
            emailtf.setText("");
            
            if (status.equals("Registered")){
                JOptionPane.showMessageDialog(null, "Attendee registered!");
            } else {
                JOptionPane.showMessageDialog(null, "Event is full. Attendee added to the waiting list!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void checkInAttendee(){
        int row = registeredtbl.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(null, "Please select a registered attendee!");
            return;
        }
        if (rtmodel.getValueAt(row, 4).equals("Yes")){
            JOptionPane.showMessageDialog(null, "Attendee is already checked in!");
            return;
        }
        
        try{
            pst = con.prepareStatement("UPDATE attendee SET checked_in=1 WHERE id=?");
            pst.setInt(1, (int) rtmodel.getValueAt(row, 0));
            pst.executeUpdate();
            
            loadAttendees();
            JOptionPane.showMessageDialog(null, "Attendee checked in!");
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addFeedback(){
        int row = registeredtbl.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(null, "Please select a registered attendee!");
            return;
        }
        if (rtmodel.getValueAt(row, 4).equals("No")){
            JOptionPane.showMessageDialog(null, "Only checked in attendees can give feedback!");
            return;
        }
        
        String input = JOptionPane.showInputDialog(null, "Enter rating (1-5):");
        if (input == null){
            return;
        }
        
        try{
            int rating = Integer.parseInt(input.trim());
            if (rating < 1 || rating > 5){
                JOptionPane.showMessageDialog(null, "Rating must be from 1 to 5!");
                return;
            }
            
            pst = con.prepareStatement("UPDATE attendee SET rating=? WHERE id=?");
            pst.setInt(1, rating);
            pst.setInt(2, (int) rtmodel.getValueAt(row, 0));
            pst.executeUpdate();
            
            loadAttendees();
            JOptionPane.showMessageDialog(null, "Feedback saved!");
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Rating must be a number!");
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cancelRegistration(){
        int eventId = getEventId();
        int row = registeredtbl.getSelectedRow();
        int waitrow = waitingtbl.getSelectedRow();
        
        if (row == -1 && waitrow == -1){
            JOptionPane.showMessageDialog(null, "Please select an attendee to cancel!");
            return;
        }
        
        int id;
        if (row != -1){
            id = (int) rtmodel.getValueAt(row, 0);
        } else {
            id = (int) wlmodel.getValueAt(waitrow, 1);
        }
        
        try{
            pst = con.prepareStatement("DELETE FROM attendee WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            loadAttendees();
            
            //may nawalang registered, ang una sa waiting list ang papalit
            if (row != -1 && !waitingQueue.isEmpty() && registeredList.size() < getCapacity(eventId)){
                Person next = waitingQueue.poll();
                pst = con.prepareStatement("UPDATE attendee SET status='Registered' WHERE id=?");
                pst.setInt(1, next.id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, next.name + " was moved from the waiting list to registered!");
            }
            
            updateEventCounts(eventId);
            loadAttendees();
        } catch (SQLException ex) {
            Logger.getLogger(attendees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
