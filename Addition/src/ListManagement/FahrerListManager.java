/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ListManagement;

import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author flibbo
 */
public class FahrerListManager extends javax.swing.JFrame {
    private JList myList;
    private DefaultListModel listModel;
    
    public FahrerListManager(JList list, DefaultListModel model){
        myList = list;
        listModel = model;
    }
    
    public void addItem(String str){
        listModel.addElement(str);
    }
    
    public void removeElement(int index){
        listModel.remove(index);
    }
    
    public void changeElement( int index, String str){
        listModel.set(index, str);
    }
    
    public void updateFahrerList(ArrayList<String> list){
        int i = 0;
        listModel.clear();
        while(i < list.size()){
            addItem(list.get(i));
            System.out.println(listModel.get(i));
            i++;
        }
        
    }
    
}
