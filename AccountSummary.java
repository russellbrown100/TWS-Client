/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.util.ArrayList;



class AccountSummary
{
    public ArrayList records;
    public ArrayList activity_records;
    public int length;
    public double starting_equity;
    public double ending_equity;
    public double deviation;
    public double profit;    
    
    public AccountSummary()
    {
        records = new ArrayList();
        activity_records = new ArrayList();
        
    }
    
    public void Calculate()
    {
        Object[] beg_record = (Object[])records.get(0);
        Object[] end_record = (Object[])records.get(records.size() - 1);
        
        starting_equity = (double)beg_record[2];
        ending_equity = (double)end_record[2];
        
        profit = ending_equity - starting_equity;
        
        double inc = (ending_equity - starting_equity) / (records.size() - 1);
        double value = starting_equity;
        double max_dev = Double.MIN_VALUE;
        for (int i = 0; i < records.size(); i++)
        {
            Object[] record = (Object[])records.get(i);
            double equity = (double)record[2];
            double dev = Math.abs(equity - value);
            if (dev > max_dev) max_dev = dev;
            value += inc;
        }
        
        deviation = max_dev / profit;
        
        
    }
    
    public String GetSummary()
    {
        return starting_equity + "  " + ending_equity + "  " + deviation + "  " + profit;
    }
    
}


