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
public class Order {

    private int aNr;
    private int pNr;
    private int startX;
    private int startY;
    private int zielX;
    private int zielY;

    private String status;
    private String date;
    private String time;
    

    public Order(int aNr, int pNr, String date, String time, int startx, int starty, int zielx, int ziely, String status) {
        this.aNr = aNr;
        startX = startx;
        startY = starty;
        zielX = zielx;
        zielY = ziely;
        this.status = status;
        this.pNr = pNr;
        this.date = date;
        this.time = time;
    }

    public String getOrderAsString() {
        return aNr + "    " + pNr + "  " + date + "   " +time + "   start: " + startX + " / "
                + startY + "   ziel: " + zielX + " / " + zielY + "    " + status;
    }

    public int getANr() {
        return aNr;
    }

    public int getStartY() {
        return startY;
    }

    public int getStartX() {
        return startX;
    }
    
     public int getGoalY() {
        return zielY;
    }

    public int getGoalX() {
        return zielX;
    }

    public String getStatus() {
        return status;
    }

    public int getPNr() {
        return pNr;
    }

    boolean isOpen() {
        if (status.equals("nicht in Bearbeitung")) {
            return true;
        }
        return false;
    }
}
