/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;



class SimpleMovingAverage
{
    public int period = 5;
    public double[] values;
    
    public SimpleMovingAverage(Chart chart)
    {
        values = new double[chart.length];
    }
    
    public void Calculate(Chart chart)
    {        
        double sum = 0;        
        
        if (chart.bars_loaded >= period - 1)
        {
        
            for (int i = 0; i < period; i++)
            {
                sum += chart.close[chart.length - 1 - (period - 1) + i];
            }        
            sum /= period;

        }
        
        values[chart.length - 1] = sum;
        
        
    }
}

class ExponentialMovingAverage
{
    public int period = 5;
    public double[] values;
    public int beg_id;
    
    public ExponentialMovingAverage(Chart chart)
    {
        values = new double[chart.length];
        beg_id = 0;
    }
    
    public void Calculate(Chart chart)
    {        
        double ema = 0;       
                
        if (chart.bars_loaded == period)
        {
            ema = chart.close[chart.length - 1];
            beg_id = chart.bars_loaded;
        }
        else if (chart.bars_loaded > period)
        {
            double prev_ema = values[chart.length - 2];  
            double k = 2d / (double)(period + 1);
            ema = (chart.close[chart.length - 1] * k) + (prev_ema * (1 - k));
        }
                
        
        values[chart.length - 1] = ema;
        
        
    }
}

class EMAMacd
{    
    public int period1 = 4;
    public int period2 = 8;
    public ExponentialMovingAverage ema1;
    public ExponentialMovingAverage ema2;
    public double[] values;
    public int beg_id;
    
    public EMAMacd(Chart chart)
    {
        beg_id = 0;
        
        ema1 = new ExponentialMovingAverage(chart);
        ema1.period = period1;
        
        ema2 = new ExponentialMovingAverage(chart);
        ema2.period = period2;
        
        values = new double[chart.length];
    }
    
    public void Calculate(Chart chart)
    {        
        
        ema1.Calculate(chart);
        ema2.Calculate(chart);  
        
        if (ema1.beg_id > 0 && ema2.beg_id > 0)
        {
        
            if (chart.bars_loaded >= ema1.beg_id &&
                    chart.bars_loaded >= ema2.beg_id)
            {

                values[chart.length - 1] = ema1.values[chart.length - 1] - ema2.values[chart.length - 1];                

                if (beg_id == 0)
                {
                    beg_id = chart.bars_loaded;

                }

            }
        
        }
        
        
    }
}

class VIDYA
{
    public int period;
    public double[] values;
    public int beg_id;
    
    public VIDYA(Chart chart)
    {
        
        values = new double[chart.length];
        
        beg_id = 0;
    }
    
    public void Calculate(Chart chart)
    {
        
        
        double sumH = 0;        
        double sumD = 0;
        double k = 2d / (double)(period + 1);
        
        double value = 0;
        
        
        if (chart.bars_loaded == period)
        {
            value = chart.close[chart.length - 1];
            beg_id = chart.bars_loaded;
        }
        else if (chart.bars_loaded > period)
        {
            for (int i = 1; i < period; i++)
            {
                double diff = chart.close[chart.length - 1 - (period - 1) + i] - 
                        chart.close[chart.length - 1 - (period - 1) + i - 1];
                
                if (diff > 0)
                {
                    sumH += diff;
                }
                else
                {
                    sumD += -diff;
                }
            }       
            
            double cmo = 0;
            
            if (sumH + sumD != 0)
            {
                cmo = (sumH - sumD) / (sumH + sumD);
                cmo = Math.abs(cmo);
            }
            
            
            
            double price = chart.close[chart.length - 1];
            
            
            double prev_val = values[chart.length - 2];  
            
            value = (price * k * cmo) + (prev_val * (1 - k * cmo));
          
          //  value = chart.close[chart.length - 1];
            
           // System.out.println(cmo + "  " + value);
            
            
        }
        
        
        values[chart.length - 1] = value;
        
    }
}



class ChandeKrollStop
{    
    public int period = 10;
    public int multiplier = 3;
    public double[] buy_stop;
    public double[] sell_stop;
    public double[] atr;
    public int beg_id;
    
    public ChandeKrollStop(Chart chart)
    {
        beg_id = 0;
        
        
        atr = new double[chart.length];
        buy_stop = new double[chart.length];
        sell_stop = new double[chart.length];
    }
    
    public void Calculate(Chart chart)
    {        
        double val1 = 0;
        double val2 = 0;
        double val3 = 0;
        
        if (chart.bars_loaded >= period)
        {
            
            double avg_tr = 0;

            for (int i = 0; i < period; i++)
            {

                val1 = chart.high[chart.length - 1 - (period - 1) + i] - chart.low[chart.length - 1 - (period - 1) + i];
                val2 = Math.abs(chart.high[chart.length - 1 - (period - 1) + i] - chart.close[chart.length - 2 - (period - 1) + i]);
                val3 = Math.abs(chart.low[chart.length - 1 - (period - 1) + i] - chart.close[chart.length - 2 - (period - 1) + i]);

                double tr = 0;
                if (val1 > tr) tr = val1;
                if (val2 > tr) tr = val2;
                if (val3 > tr) tr = val3;
                
              //  System.out.println(val1 + "   " + val2 + "   " + val3 + "   " + tr);

                avg_tr += tr;

            }        

            avg_tr /= period;      
                
            if (chart.bars_loaded == period)
            {
                        
                
                atr[chart.length - 1] = avg_tr;
                
                
                beg_id = chart.bars_loaded+1;

            }
            else if (chart.bars_loaded > period)
            {
                atr[chart.length - 1] = (atr[chart.length - 2] * (period - 1) + avg_tr) / period;
                
                double highest = Double.MIN_VALUE;
                double lowest = Double.MAX_VALUE;
                
                for (int i = 0; i < period; i++)
                {
                    double high = chart.high[chart.length - 1 - (period - 1) + i];
                    double low = chart.low[chart.length - 1 - (period - 1) + i];
                    
                    if (high > highest) highest = high;
                    if (low < lowest) lowest = low;                    
                }
                
                buy_stop[chart.length - 1] = highest - atr[chart.length - 1] * multiplier;
                sell_stop[chart.length - 1] = lowest + atr[chart.length - 1] * multiplier;
                
            }

            
        
            
            
          //  System.out.println(chart.bars_loaded + "  " + atr[chart.length - 1]);
            

          //  System.exit(0);
            
        }
        
        
        
      //  System.out.println(atr[chart.length - 1] + "  " + val1 + "  " + val2 + "  " + val2);
        
    }
}

class DonchianChannels
{
    public int period = 12;
    public int beg_id;
    public double[] upper_channel;
    public double[] lower_channel;
    public int ucount;
    public int lcount;
    
    public DonchianChannels(Chart chart)
    {
        upper_channel = new double[chart.length];
        lower_channel = new double[chart.length];
        beg_id = -1;
        ucount = 0;
        lcount = 0;
    }
    
    public void Calculate(Chart chart)
    {        
        double highest_high = 0;        
        double lowest_low = 0;
        
        if (chart.bars_loaded >= period - 1)
        {
            if (beg_id == -1) beg_id = chart.bars_loaded;
            
            highest_high = Double.MIN_VALUE;
            lowest_low = Double.MAX_VALUE;
        
            for (int i = 0; i < period; i++)
            {
                double high = chart.high[chart.length - 1 - (period - 1) + i];
                double low = chart.low[chart.length - 1 - (period - 1) + i];
                if (high > highest_high) highest_high = high;
                if (low < lowest_low) lowest_low = low;
            }        
            

        }
        
        upper_channel[chart.length - 1] = highest_high;
        lower_channel[chart.length - 1] = lowest_low;
        
        
    }
}


