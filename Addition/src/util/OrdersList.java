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
public class OrdersList extends ArrayList<Order> {

    private int aNrOfClosest = -1;

    public Order getOrderByANR(int aNr) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getANr() == aNr) {
                return this.get(i);
            }
        }
        return null;
    }

    public ArrayList<String> getAllOrdersAsStringList() {
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < this.size(); i++) {
            temp.add(this.get(i).getOrderAsString());
        }
        return temp;
    }

    public void findClosestFromPosition(int fahrerX, int fahrerY) {
        int dist = 1000;
        for (Order order : this) {
            if (order.isOpen()) {
                int tempDist = getManhattanDist(fahrerX, fahrerY, order.getStartX(), order.getStartY());
                if (tempDist < dist) {
                    dist = tempDist;
                    aNrOfClosest = order.getANr();
                }
            }
        }
    }

    private int getManhattanDist(int p1X, int p1Y, int p2X, int p2Y) {
        int temp = Math.abs(p1X - p2X) + Math.abs(p1Y - p2Y);
        return temp;

    }

    public int getANrOfClosest() {
        return aNrOfClosest;
    }
}
