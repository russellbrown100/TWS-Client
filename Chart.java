/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.util.ArrayList;
import java.util.Calendar;



class Time
{
    public int hour;
    public int min;
    public int count;
    
    public Time(int hour, int min)
    {
        this.hour = hour;
        this.min = min;
        count = 0;
    }
}

class TradingSession
{
    public int day;
    public Time start_time;
    public Time end_time;
    
    public TradingSession(int day, Time start_time, Time end_time)
    {
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    
    public String GetString()
    {
        return day + "  " + start_time.hour + ":" + start_time.min + " " + end_time.hour + ":" + end_time.min;
    }
}

class PriceRecord
{
    public Calendar date;
    public double open;
    public double high;
    public double low;
    public double close;
    public long volume;
    
    public PriceRecord(Calendar date, double open, double high, double low, double close, long volume)
    {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}

class Chart
{
    
    public int length;
    public int period;
    public int period_id;
    public int[] periods;
    public long[] date;
    public double[] open;
    public double[] high;
    public double[] low;
    public double[] close;
    public double[] volume;
    public double[] open_trades;
    public double[] close_trades;
    public Calendar next_date;
    public int bars_loaded;
    public ArrayList indicators;
    public ArrayList indicator_properties;
    public PriceRecord price_record;
    public boolean chart_loaded;
    public String chart_description;
        
    public Chart(int length, int period)
    {
        indicators = new ArrayList();
        indicator_properties = new ArrayList();
        bars_loaded = 0;
        next_date = null;
        this.period_id = 0;
        this.periods = new int[]{period};
        this.length = length;
        this.period = period;
        this.date = new long[length];
        this.open = new double[length];
        this.high = new double[length];
        this.low = new double[length];
        this.close = new double[length];
        this.volume = new double[length];
        this.open_trades = new double[length];
        this.close_trades = new double[length];
    }
    
    public Chart(int length, int[] periods)
    {
        indicators = new ArrayList();
        indicator_properties = new ArrayList();
        bars_loaded = 0;
        next_date = null;
        this.period_id = 0;
        this.periods = periods;
        this.length = length;
        this.period = this.periods[this.period_id];
        this.date = new long[length];
        this.open = new double[length];
        this.high = new double[length];
        this.low = new double[length];
        this.close = new double[length];
        this.volume = new double[length];
        this.open_trades = new double[length];
        this.close_trades = new double[length];
    }
    
    
        
    
}

