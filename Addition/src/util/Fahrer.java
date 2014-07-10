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
    private int pnr;
    private int x;
    private int y;
    
    public Fahrer(String vName, String nName, int pnr, int x, int y){
        vname = vName;
        nname = nName;
        this.pnr = pnr;
        this.x = x;
        this.y = y;
    }
}
