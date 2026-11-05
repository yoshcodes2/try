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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Event_Management_System extends JFrame implements ActionListener{
    
    private JLabel searchlbl, addeventlbl, namelbl, datelbl, categorylbl, capacitylbl;
    private JTextField search, addname, adddate, addcategory, addcapacity;
    private JButton searchbtn, showallbtn, attendeesbtn, reportbtn, addeventbtn, updatebtn, deletebtn, clearbtn;
    private JComboBox categorysearch;
    private JTable table;
    private JPanel panel;
    private JScrollPane sp;
    private DefaultTableModel model;
    
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
    
    
    Event_Management_System(){
        Connect();
        setSize(940, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Event Management System");
        setLayout(null);
        
           
        searchlbl = new JLabel("Search By: ");
        searchlbl.setBounds(10, 20, 65, 30);
        add(searchlbl);
        
        categorysearch = new JComboBox<>(new String[]{"Date DD-MM-YY", "Category"});
        categorysearch.setBounds(90, 23, 120, 25);
        add(categorysearch);
        
        search = new JTextField();
        search.setBounds(230, 23, 200, 25);
        add(search);
        
        searchbtn = new JButton("Search");
        searchbtn.setBounds(435, 25, 80, 20);
        searchbtn.addActionListener(this);
        add(searchbtn);
        
        showallbtn = new JButton("Show All");
        showallbtn.setBounds(520, 25, 100, 20);
        showallbtn.addActionListener(this);
        add(showallbtn);

        //go to attendees or reports page/file or what 
        
        attendeesbtn = new JButton("Go to Attendees");
        attendeesbtn.setBounds(630, 20, 130, 30);
        attendeesbtn.addActionListener(this);
        add(attendeesbtn);
        
        reportbtn = new JButton("Go to Reports");
        reportbtn.setBounds(770, 20, 140, 30);
        reportbtn.addActionListener(this);
        add(reportbtn);
        
        ///tables?

        model = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Category", "Attendees", "Waiting","Guest(s)"}, 0);
        
        table = new JTable(model);
        table.setBorder(BorderFactory.createLineBorder(Color.black));
        
        sp = new JScrollPane(table);
        sp.setBounds(10, 65, 900, 400);
        sp.setSize(900, 400);
        add(sp);
        
        //pag nag select ng event sa table, mapupunta sa form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1){
                int row = table.getSelectedRow();
                addname.setText(model.getValueAt(row, 1).toString());
                adddate.setText(model.getValueAt(row, 2).toString());
                addcategory.setText(model.getValueAt(row, 3).toString());
                addcapacity.setText(model.getValueAt(row, 6).toString());
            }
        });
        
        loadEvents();
        
        
        
        
        //add event
        
        addeventlbl = new JLabel("Add Event");
        addeventlbl.setBounds(35, 475, 100, 50);
        add(addeventlbl); 
        
        namelbl = new JLabel("Name:");
        namelbl.setBounds(50, 500, 100, 50);
        add(namelbl);
        
        datelbl = new JLabel("Date(DD-MM-YY):");
        datelbl.setBounds(320, 500, 100, 50);
        add(datelbl);
        
        categorylbl = new JLabel("Category :");
        categorylbl.setBounds(530, 500, 100, 50);
        add(categorylbl);
        
        capacitylbl = new JLabel("Capacity :");
        capacitylbl.setBounds(750, 500, 100, 50);
        add(capacitylbl);
        
        addname = new JTextField();
        addname.setBounds(50, 545, 250, 25);
        add(addname);
        
        adddate = new JTextField();
        adddate.setBounds(320, 545, 190, 25);
        add(adddate);
        
        addcategory = new JTextField();
        addcategory.setBounds(530, 545, 200, 25);
        add(addcategory);
        
        addcapacity = new JTextField();
        addcapacity.setBounds(750, 545, 120, 25);
        add(addcapacity);
        
        addeventbtn = new JButton("Add");
        addeventbtn.setBounds(50, 585, 100, 30);
        addeventbtn.addActionListener(this);
        add(addeventbtn);
        
        updatebtn = new JButton("Update Selected");
        updatebtn.setBounds(170, 585, 130, 30);
        updatebtn.addActionListener(this);
        add(updatebtn);
        
        deletebtn = new JButton("Delete Selected");
        deletebtn.setBounds(320, 585, 130, 30);
        deletebtn.addActionListener(this);
        add(deletebtn);
        
        clearbtn = new JButton("Clear Form");
        clearbtn.setBounds(470, 585, 130, 30);
        clearbtn.addActionListener(this);
        add(clearbtn);
        
        panel = new JPanel();
        panel.setBounds(25, 485, 870, 140);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(panel);
        
        
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == attendeesbtn){
            attendees app = new attendees();
            app.setVisible(true);
            dispose();
        }else if (e.getSource() == reportbtn){
            Reports app = new Reports();
            app.setVisible(true);
            dispose();
        }else if (e.getSource() == searchbtn){
            searchEvents();
        }else if (e.getSource() == showallbtn){
            search.setText("");
            loadEvents();
        }else if(e.getSource() == addeventbtn){
            addEvent();
        }else if(e.getSource() == updatebtn){
            updateEvent();
        }else if(e.getSource() == deletebtn){
            deleteEvent();
        }else if(e.getSource() == clearbtn){
            clearForm();
        }
    }
    
    private void clearForm(){
        addname.setText("");
        adddate.setText("");
        addcategory.setText("");
        addcapacity.setText("");
        table.clearSelection();
    }
    
    //checker kung tama ang laman ng form
    private boolean validInput(){
        if (addname.getText().trim().isEmpty() || adddate.getText().trim().isEmpty()
                || addcategory.getText().trim().isEmpty() || addcapacity.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please fill in all fields!");
            return false;
        }
        if (!adddate.getText().trim().matches("\\d{2}-\\d{2}-\\d{2}")){
            JOptionPane.showMessageDialog(null, "Date must be in DD-MM-YY format!");
            return false;
        }
        try{
            if (Integer.parseInt(addcapacity.getText().trim()) <= 0){
                JOptionPane.showMessageDialog(null, "Capacity must be greater than 0!");
                return false;
            }
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Capacity must be a number!");
            return false;
        }
        return true;
    }
    
    private void addEvent(){
        if (!validInput()){
            return;
        }
        
        String sql = "INSERT INTO evt (name, date, category, attendees, waiting, capacity) VALUES (?, ?, ?, 0, 0, ?)";
        try{
            pst = con.prepareStatement(sql);
            pst.setString(1, addname.getText().trim());
            pst.setString(2, adddate.getText().trim());
            pst.setString(3, addcategory.getText().trim());
            pst.setInt(4, Integer.parseInt(addcapacity.getText().trim()));
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Event added!");
            loadEvents();
            clearForm();
        } catch (SQLException ex) {
            Logger.getLogger(Event_Management_System.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateEvent(){
        int row = table.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(null, "Please select an event to update!");
            return;
        }
        if (!validInput()){
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        String sql = "UPDATE evt SET name=?, date=?, category=?, capacity=? WHERE id=?";
        try{
            pst = con.prepareStatement(sql);
            pst.setString(1, addname.getText().trim());
            pst.setString(2, adddate.getText().trim());
            pst.setString(3, addcategory.getText().trim());
            pst.setInt(4, Integer.parseInt(addcapacity.getText().trim()));
            pst.setInt(5, id);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Event updated!");
            loadEvents();
            clearForm();
        } catch (SQLException ex) {
            Logger.getLogger(Event_Management_System.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deleteEvent(){
        int row = table.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(null, "Please select an event to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, "Delete this event and all its attendees?");
        if (confirm != JOptionPane.YES_OPTION){
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        try{
            pst = con.prepareStatement("DELETE FROM attendee WHERE event_id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            
            pst = con.prepareStatement("DELETE FROM evt WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Event deleted!");
            loadEvents();
            clearForm();
        } catch (SQLException ex) {
            Logger.getLogger(Event_Management_System.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //linear search: chine-check isa-isa ang bawat event sa table
    private void searchEvents(){
        String key = search.getText().trim();
        if (key.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please enter what to search!");
            return;
        }
        
        int by = categorysearch.getSelectedIndex(); //0 = date, 1 = category
        loadEvents();
        
        ArrayList<Object[]> found = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++){
            boolean match;
            if (by == 0){
                match = model.getValueAt(i, 2).toString().equals(key);
            } else {
                match = model.getValueAt(i, 3).toString().toLowerCase().contains(key.toLowerCase());
            }
            
            if (match){
                Object[] row = new Object[model.getColumnCount()];
                for (int j = 0; j < row.length; j++){
                    row[j] = model.getValueAt(i, j);
                }
                found.add(row);
            }
        }
        
        model.setRowCount(0);
        for (Object[] row : found){
            model.addRow(row);
        }
        
        if (found.isEmpty()){
            JOptionPane.showMessageDialog(null, "No events found!");
        }
    }
    
    private void loadEvents(){
            model.setRowCount(0);
            
            String sql = "SELECT id, name, date, category, attendees, waiting, capacity FROM evt";
            try{
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                
                while(rs.next()){
                    model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    rs.getString("category"),
                    rs.getInt("attendees"),
                    rs.getInt("waiting"),
                    rs.getInt("capacity")
                    });
                }
            } catch (SQLException ex) {
            Logger.getLogger(Event_Management_System.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    
}
