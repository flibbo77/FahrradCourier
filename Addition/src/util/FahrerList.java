/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author flibbo
 */
public class FahrerList extends ArrayList<Fahrer> {

    public Fahrer getFahrerByPNR(int pNr) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getPNR() == pNr) {
                return this.get(i);
            }
        }
        return null;
    }
    
    public ArrayList<String> getAllFahrerAsStringList() {   
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < this.size(); i++) {
            temp.add(this.get(i).getFahrerAsString());
        }
        return temp;
    }
    
    
}
