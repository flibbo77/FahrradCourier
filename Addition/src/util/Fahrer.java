/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author flibbo
 */
public class Fahrer {

    private String vname;
    private String nname;
    private int order;

    private int pnr;
    private int x;
    private int y;

    public Fahrer(String vName, String nName, int pnr, int x, int y) {
        vname = vName;
        nname = nName;
        this.pnr = pnr;
        this.x = x;
        this.y = y;
        order = -1;
    }

    public void setName(String vName, String nName) {
        vname = vName;
        nname = nName;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPNR() {
        return pnr;
    }

    public String getFahrerAsString() {
        return vname + " " + nname + " pnr: " + pnr + " x: " + x + " y: " + y;
    }

    public String getVName() {
        return vname;
    }

    public String getNName() {
        return nname;
    }

    public int getOrder() {
        return order;
    }
    
    public boolean hasActualJob(){
        if(order == -1){
            return false;
        }
        return true;
    }
}
