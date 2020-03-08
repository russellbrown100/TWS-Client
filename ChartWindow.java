/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JPanel;


class Formatters
{
    public static java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd  HH:mm:ss");
    public static java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat("yyyyMMdd  HH:mm:ss:SSS");
    public static DecimalFormat pf2 = new DecimalFormat("0.00");
    public static DecimalFormat pf5 = new DecimalFormat("0.00000");
    
    public static DecimalFormat CalculatePrecision(int precision)
    {
        String string = "0";
        
        if (precision > 0)
        {
        
            for (int i = 0; i < precision; i++)
            {
                if (i == 0) string += ".";

                string += "0";
            }
        
        }
        
        return new DecimalFormat(string);
    }
    
}


class ChartPanel
{
    public int left;
    public int top;
    public int width;
    public int height;
    public boolean show_title;
    public boolean show_data;
    public boolean show_upper_indicators;
    public boolean show_lower_indicators;
    public boolean show_x_axis;
    public int y_axis_interval;
    public int y_axis_precision;
}




class ChartWindow extends JPanel {
    
    public BufferedImage image;
    public Chart chart;
    public int bars_per_window = 50;
    public int position;
    public int left;
    public int top;
    public int width;
    public int height;
    public int bar_shift;
    public double price_margin = 0.25;
    public double bar_margin = 0.20;
    public int mouse_x;
    public int mouse_y;
    public ChartPanel panel;
    public ChartPanel panel2;
    
    public ChartWindow()
    {
        mouse_x = 0;
        mouse_y = 0;
        
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mouse_x = evt.getX();
                mouse_y = evt.getY();
                repaint();
            }
        });
    }
    
    public void init()
    {
        position = 0;
        bars_per_window = 80;
        bar_shift = 2;
        
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black)); 
        
        setBounds(1, 1, width, height);          
        setBackground(Color.white);
        
        int step = 320;
        
        panel = new ChartPanel();
        panel.left = left;
        panel.top = top;
        panel.width = width - step;
        panel.height = height - 160;
        panel.y_axis_interval = 20;
        panel.y_axis_precision = 5;
        panel.show_title = true;
        panel.show_x_axis = false;
        panel.show_data = true;
        panel.show_upper_indicators = true;
        panel.show_lower_indicators = false;
        
        
        panel2 = new ChartPanel();
        panel2.left = left;
        panel2.top = panel.top + panel.height;
        panel2.width = width - step;
        panel2.height = 80;
        panel2.y_axis_interval = 20;
        panel2.y_axis_precision = 5;
        panel2.show_title = false;
        panel2.show_x_axis = true;
        panel2.show_data = false;
        panel2.show_upper_indicators = false;
        panel2.show_lower_indicators = true;
        
    }
    
    public int CalculateDataID()
    {
        int id = 0;
        
        if (chart.bars_loaded < chart.length)
        {
            id = chart.length - chart.bars_loaded;
            
            
            
        }
        
        return id;
    }
    
    public void PaintPanel(Graphics2D graphics, ChartPanel panel)
    {
        try
        {
            
        double bar_width = (double)panel.width / bars_per_window;
        

        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        graphics.setColor(Color.black);                       
        graphics.draw(new Rectangle.Double(panel.left, panel.top, panel.width, panel.height));
        
        
        
        
        if (chart == null) return;
        
        
            
        int last_id1 = 0;
        
        
        // calculate price range
        
        double min_price = Double.MAX_VALUE;
        double max_price = Double.MIN_VALUE;
        
        
        if (panel.show_data == true)
        {            

            
            for (int i = 0; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
             {
                int id = CalculateDataID() + position + i + bar_shift;      
        
                //System.out.println(id + "  " + i + "  " + position);
                                  
                if (chart.high[id] > max_price) max_price = chart.high[id];
                if (chart.low[id] < min_price) min_price = chart.low[id]; 
                                    
                 
             }
       
            
        }
        else
        {
            
            for (int i = 0; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
            {
                int id = CalculateDataID() + position + i + bar_shift;    
                
                last_id1 = id;
                
                for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
                {
                    Object[] ind = (Object[])chart.indicator_properties.get(i2);
                    
                    if (panel.show_upper_indicators == true)
                    {
                        if (ind[0].toString().equals("simple moving average"))
                        {
                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2); 
                            if (ob.values[id] > max_price) max_price = ob.values[id];
                            if (ob.values[id] < min_price) min_price = ob.values[id];
                        }
                        else if (ind[0].toString().equals("exponential moving average"))
                        {
                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);   
                            if (id >= ob.beg_id)
                            {
                                if (ob.values[id] > max_price) max_price = ob.values[id];
                                if (ob.values[id] < min_price) min_price = ob.values[id];
                            }
                        }
                        else if (ind[0].toString().equals("vidya"))
                        {
                            VIDYA ob = (VIDYA)chart.indicators.get(i2);   
                            if (id >= ob.beg_id)
                            {
                                if (ob.values[id] > max_price) max_price = ob.values[id];
                                if (ob.values[id] < min_price) min_price = ob.values[id];
                            }
                        }
                        else if (ind[0].toString().equals("donchian channels"))
                        {
                            DonchianChannels ob = (DonchianChannels)chart.indicators.get(i2);   
                            if (id >= ob.beg_id)
                            {                                
                                if (ob.upper_channel[id] > max_price) max_price = ob.upper_channel[id];
                                if (ob.lower_channel[id] < min_price) min_price = ob.lower_channel[id];
                            }
                        }
                        else if (ind[0].toString().equals("chande kroll stop"))
                        {
                            ChandeKrollStop ob = (ChandeKrollStop)chart.indicators.get(i2);   
                            if (id >= ob.beg_id)
                            {
                                if (ob.atr[id] > max_price) max_price = ob.atr[id];
                                if (ob.atr[id] < min_price) min_price = ob.atr[id];
                                
                                if (ob.buy_stop[id] > max_price) max_price = ob.buy_stop[id];
                                if (ob.buy_stop[id] < min_price) min_price = ob.buy_stop[id];
                                
                                if (ob.sell_stop[id] > max_price) max_price = ob.sell_stop[id];
                                if (ob.sell_stop[id] < min_price) min_price = ob.sell_stop[id];
                            }
                        }
                    }
                    
                    if (panel.show_lower_indicators == true)
                    {
                    
                        if (ind[0].toString().equals("ema macd"))
                        {
                            EMAMacd ob = (EMAMacd)chart.indicators.get(i2); 
                            if (id >= ob.beg_id)
                            {
                                if (ob.values[id] > max_price) max_price = ob.values[id];
                                if (ob.values[id] < min_price) min_price = ob.values[id];
                            }

                        }
                    
                    }
                    
                }
            
            }
            
        }
        
    //    System.out.println("chart_window:  last id = " + last_id1 + "  " + position);
        
       
        
        double price_range = max_price - min_price;
        
        
        price_range = max_price - min_price;

        if (price_range == 0) price_range = 1;

        max_price = max_price + (price_range * price_margin);
        min_price = min_price - (price_range * price_margin);

        price_range = max_price - min_price;
        
        
        graphics.setClip(panel.left, panel.top, panel.width, panel.height);
        
        // candles
        
        Color up_color = Color.green;
        Color dn_color = Color.red;

        double x = 0;
                
        
        
        
        if (chart.bars_loaded > bars_per_window)
        {        
            if (chart.length > bars_per_window)
            {            
                x = panel.left;
            }
            else
            {
                x = panel.left + panel.width - (chart.length * bar_width);
            }
            
        }
        else
        {            
            if (chart.bars_loaded < chart.length)
            {
                x = panel.left + panel.width - (chart.bars_loaded * bar_width);
            }
            else
            {
                x = panel.left + panel.width - (chart.length * bar_width);
            }
            
        }
        
        if (panel.show_data == true)
        {
        
            
            for (int i = 0; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
            {
                int id = CalculateDataID() + position + i + bar_shift;    
                
                

//                Calendar dt = Calendar.getInstance();
//                dt.setTimeInMillis(chart.date[id]);
//                System.out.println("chart date:  " + dt.getTime()  + "  " + id + "  " + chart.length);

                if (    chart.open[id] > 0 &&
                            chart.high[id] > 0 &&
                            chart.low[id] > 0 &&
                            chart.close[id] > 0)
                {

                    double op = (panel.top + panel.height) - (panel.height * ((chart.open[id] - min_price) / price_range));
                    double hp = (panel.top + panel.height) - (panel.height * ((chart.high[id] - min_price) / price_range));
                    double lp = (panel.top + panel.height) - (panel.height * ((chart.low[id] - min_price) / price_range));
                    double cp = (panel.top + panel.height) - (panel.height * ((chart.close[id] - min_price) / price_range));

                    if (op < cp)
                    {       
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), op));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), lp, x + (bar_width / 2), cp));

                        graphics.setColor(dn_color);
                        graphics.fill( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), op, bar_width - ((bar_width / 2) * (bar_margin * 2)), cp - op));
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), op, bar_width - ((bar_width / 2) * (bar_margin * 2)), cp - op));

                    }
                    else if (op > cp)
                    {       
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), cp));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), lp, x + (bar_width / 2), op));

                        graphics.setColor(up_color);
                        graphics.fill( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), cp, bar_width - ((bar_width / 2) * (bar_margin * 2)), op - cp));
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), cp, bar_width - ((bar_width / 2) * (bar_margin * 2)), op - cp));

                    }
                    else if (op == cp)
                    {                                
                        graphics.setColor(Color.black);
                        graphics.draw( new Line2D.Double(x + ((bar_width / 2) * bar_margin), op, x + (bar_width / 2), op));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), cp, x + (bar_width / 2) + ((bar_width / 2) * (1 - bar_margin)), cp));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), lp));
                    }

                }
                
                
                graphics.setColor(Color.DARK_GRAY);
                
                if (id > 0)
                {
                
                    Calendar date = Calendar.getInstance();
                    date.clear();
                    date.setTimeInMillis(chart.date[id]);

                    Calendar date2 = Calendar.getInstance();
                    date2.clear();
                    date2.setTimeInMillis(chart.date[id-1]);

                    if (date.get(Calendar.DAY_OF_WEEK) != date2.get(Calendar.DAY_OF_WEEK))
                    {
                        graphics.draw( new Line2D.Double(x + ((bar_width / 2) * bar_margin), panel.top, x + ((bar_width / 2) * bar_margin), panel.top + panel.height));
                    }
                
                }
                

                graphics.setColor(Color.black);

                x += bar_width;


            }

        }
        
        
        // indicators
        
        
                
        
        
        if (chart.bars_loaded > bars_per_window)
        {         
            if (chart.length > bars_per_window)
            {            
                x = panel.left + bar_width;
            }
            else
            {
                x = panel.left + panel.width - (chart.length * bar_width);
            }
        }
        else
        {            
            if (chart.bars_loaded < chart.length)
            {
                x = panel.left + panel.width - (chart.bars_loaded * bar_width) + bar_width;
            }
            else
            {
                x = panel.left + panel.width - (chart.length * bar_width) + bar_width;
            }
            
        }
        
            
        //for (int i = 1; i < bars_per_window - bar_shift; i++)
       // for (int i = 1; i < chart.length - bar_shift; i++)
        for (int i = 1; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
        {
            
            graphics.setColor(Color.blue);
        
            int id = CalculateDataID() + position + i + bar_shift;
                     
            
            int normalized_id = id - (chart.length - 1 - chart.bars_loaded) - 1;
            
                
            for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
            {                
                Object[] ind = (Object[])chart.indicator_properties.get(i2);
                    
                if (panel.show_upper_indicators)
                {
                        
                    if (ind[0].toString().equals("exponential moving average"))
                    {
                        ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);
                        double p = ob.values[id];
                        double p2 = ob.values[id - 1];

                        if (normalized_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }   
                    else if (ind[0].toString().equals("vidya"))
                    {
                        VIDYA ob = (VIDYA)chart.indicators.get(i2);
                        double p = ob.values[id];
                        double p2 = ob.values[id - 1];

                        if (normalized_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                    else if (ind[0].toString().equals("donchian channels"))
                    {
                        DonchianChannels ob = (DonchianChannels)chart.indicators.get(i2);
                        double p = ob.upper_channel[id];
                        double p2 = ob.upper_channel[id - 1];

                        if (normalized_id >= ob.beg_id && id < chart.length - 1)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                
                                graphics.setColor(Color.blue);
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }
                        
                        
                        p = ob.lower_channel[id];
                        p2 = ob.lower_channel[id - 1];
                        
                        if (normalized_id >= ob.beg_id && id < chart.length - 1)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.setColor(Color.magenta);
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                    else if (ind[0].toString().equals("chande kroll stop"))
                    {
                        ChandeKrollStop ob = (ChandeKrollStop)chart.indicators.get(i2);
                        double p = ob.buy_stop[id];
                        double p2 = ob.buy_stop[id - 1];

                        if (normalized_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                
                                graphics.setColor(Color.blue);
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }
                        
                        
                        p = ob.sell_stop[id];
                        p2 = ob.sell_stop[id - 1];

                        if (normalized_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.setColor(Color.magenta);
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                
                }
                
                if (panel.show_lower_indicators)
                {
                    if (ind[0].toString().equals("ema macd"))
                    {
                        EMAMacd ob = (EMAMacd)chart.indicators.get(i2);
                        double p = ob.values[id];
                        double p2 = ob.values[id - 1];

                        if (normalized_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                }
                
                

            }
            
            if (id < chart.length - 1)
            {
                          
                if (chart.open_trades[id+1] > 0)
                {
                    graphics.setColor(Color.magenta);
                    double bar_center = x + bar_width + (bar_width / 2); 
                    java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath ();
                    double sp = (panel.top + panel.height) - (panel.height * ((chart.open_trades[id+1] - min_price) / price_range));
                    path.moveTo(bar_center, sp);
                    path.lineTo(bar_center - 6, sp + 6);
                    path.lineTo(bar_center - 6, sp - 6);
                    path.closePath();
                    graphics.fill(path);
                }

                if (chart.close_trades[id+1] > 0)
                {
                    graphics.setColor(Color.blue);
                    double bar_center = x + bar_width + (bar_width / 2); 
                    java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath ();
                    double sp = (panel.top + panel.height) - (panel.height * ((chart.close_trades[id+1] - min_price) / price_range));
                    path.moveTo(bar_center, sp);
                    path.lineTo(bar_center + 6, sp + 6);
                    path.lineTo(bar_center + 6, sp - 6);
                    path.closePath();
                    graphics.fill(path);
                }
                
            }
            
            

            x += bar_width;

            
        }
        
        
        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        
        
        
        // y axis
        
        
        graphics.setColor(Color.black);
                                
        double intervals = panel.y_axis_interval;

        double gap = (double)(panel.height - 10) / intervals;
        double dy = (double)(panel.top + 5);
        double dx = panel.left + panel.width;
        
        
        for (int i = 0; i <= intervals; i++)
        {                        


            graphics.draw(new Line2D.Double(dx, dy, dx + 10, dy));


            int ix = panel.left + panel.width;
            int iy = panel.top;
            while (iy <= panel.top + panel.height+1)
            {
                if (iy > dy && iy - 1 <= dy)
                {
                    double price_percent = (double)(iy - panel.top) / (double)panel.height;
                    
                    
                    double price = max_price - (price_percent * price_range);
                    
                    String price_string = "";
                                        
                    price_string = Formatters.CalculatePrecision(panel.y_axis_precision).format(price);
                    
                    graphics.drawString(price_string, ix + 15, iy + 4);

                    break;

                }
                iy++;
            }




            dy += gap;
        }
        
        
        // x axis
        
        

        if (panel.show_x_axis == true)
        {
            

        
            dy = panel.top + panel.height;
            
            dx = panel.left + panel.width - (bar_width * (bar_shift + 1));


            Calendar date = Calendar.getInstance();
            date.clear();
            Font font = graphics.getFont();
            FontMetrics metrics = graphics.getFontMetrics(font);

            int bar_count = 0;
            boolean first_bar = true;
            
            double max_w = 0;
            
            int last_id = 0;
            
            for (int i = 0; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
            {
                int id = CalculateDataID() + position + i + bar_shift;   
                date.setTimeInMillis(chart.date[id]);
                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();

                if (w > max_w) max_w = w;
                
                last_id = id;
                
            }
            
            
            
            max_w += 10;
            
            int bars_per_interval = 1;
            double bw = bar_width;
            while (true)
            {
                bw += bar_width;
                bars_per_interval++;
                if (bw >= max_w) break;
            }
            
            
                
          //  for (int i = bars_per_window - bar_shift - 1; i >= 0; i--)
          
            for (int i = last_id; i >= 0; i--)
            {
                int id = i;    

                
                date.setTimeInMillis(chart.date[id]);
                

                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();
                double h = metrics.getStringBounds(string, graphics).getHeight();

                if (first_bar == true)
                {
                    graphics.draw(new Line2D.Double(dx + (bar_width / 2d), dy, dx + (bar_width / 2d), dy + 10));
                    graphics.drawString(string,(int) (dx - (w / 2)), (int)(dy + 10 + h));          
                    bar_count = 0;
                    first_bar = false;
                }
                else
                {

                    if (bar_count >= bars_per_interval-1)
                    {
                        if ((int) (dx - (w / 2)) > 0)
                        {
                            graphics.draw(new Line2D.Double(dx + (bar_width / 2d), dy, dx + (bar_width / 2d), dy + 10));
                            graphics.drawString(string,(int) (dx - (w / 2)), (int)(dy + 10 + h));                          
                        }
                        bar_count = 0;
                    }
                    else
                    {
                        bar_count++;
                    }

                }


                dx -= bar_width;

            }
        
        }
        
        
        }
        catch (Exception ex)
        {
           ExceptionHandler.HandleException(ex);
            
        }
        
        
        
    }
    
    
    
    public void PaintPriceInfo(Graphics2D graphics)
    {
        try
        {
        
        // price info:


        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        graphics.setColor(Color.black);
        
        
        graphics.drawString("Symbol: USDCAD", panel.left + 50, 15);   
        
        if (frmMain.f.connection.current_time != null)
        {
        
            graphics.drawString("Current Time: " + Formatters.df.format(frmMain.f.connection.current_time.getTime()), panel.left + 250, 15);
        }
        
        
        graphics.drawString("Next Period: " + Formatters.df.format(chart.next_date.getTime()), panel.left + 450, 15);

        
        
        int step = 70;
        int step2 = 170;
        
        DonchianChannels dch = (DonchianChannels)chart.indicators.get(0);   

        graphics.drawString("Price:", panel.left + panel.width + step, top + (15 * 1));
        graphics.drawString("Upper Channel:", panel.left + panel.width + step, top + (15 * 2));
        graphics.drawString("Lower Channel:", panel.left + panel.width + step, top + (15 * 3));

        graphics.drawString(Formatters.pf5.format(chart.close[chart.length - 1]), panel.left + panel.width + step2, top + (15 * 1));   
        graphics.drawString(Formatters.pf5.format(dch.upper_channel[chart.length - 2]), panel.left + panel.width + step2, top + (15 * 2));
        graphics.drawString(Formatters.pf5.format(dch.lower_channel[chart.length - 2]), panel.left + panel.width + step2, top + (15 * 3));

        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
    }
    
    public void PaintCrossHair(Graphics2D graphics, ChartPanel panel)
    {

        try
        {
            
        double bar_width = (double)panel.width / bars_per_window;
        
        
        
        // calculate price range
        
        double min_price = 0;
        double max_price = 0;
        
      
         for (int i = 0; i < Math.min(bars_per_window - bar_shift, chart.bars_loaded - bar_shift) && i < chart.length - bar_shift; i++)
            {
                int id = CalculateDataID() + position + i + bar_shift;   
                        
            if (i == 0)
            {
                max_price = chart.high[id];
                min_price = chart.low[id];
            }
            else
            {
                if (chart.high[id] > max_price) max_price = chart.high[id];
                if (chart.low[id] < min_price) min_price = chart.low[id]; 
            }
        }
        
        double price_range = max_price - min_price;
        
        
        price_range = max_price - min_price;

        if (price_range == 0) price_range = 1;

        max_price = max_price + (price_range * price_margin);
        min_price = min_price - (price_range * price_margin);

        price_range = max_price - min_price;
        
              
        
        
        // paint cross hair
        

        if (mouse_x > panel.left && mouse_x < panel.left + panel.width &&
            mouse_y > panel.top && mouse_y < panel.top + panel.height)
        {
            
            

            
            graphics.setColor(Color.black);
            
            
            Font font = graphics.getFont();
            FontMetrics metrics = graphics.getFontMetrics(font);

            double price_percent = (double)(mouse_y - panel.top) / (double)panel.height;
            double price = max_price - (price_percent * price_range);
            graphics.drawLine(panel.left, mouse_y - 1, panel.left + panel.width + 10, mouse_y - 1); 
            graphics.drawLine(mouse_x, top, mouse_x, top + this.panel.height + this.panel2.height + 10);
            
            // price marker
            
            graphics.setColor(Color.black);              
            String price_string = Formatters.CalculatePrecision(panel.y_axis_precision).format(price);   
            
            
            
                        
            double pw = metrics.getStringBounds(price_string, graphics).getWidth();
            
            graphics.fill(new Rectangle.Double(panel.left + panel.width + 10, mouse_y - 6, pw + 5, 12));
            
            
            graphics.drawLine(mouse_x, panel.top, mouse_x, this.panel.top + panel.height + 10);
            
            
            graphics.setColor(Color.white);
            
            graphics.drawString(price_string, panel.left + panel.width + 15, mouse_y + 4);

            // date marker
            
            double dx = panel.left + panel.width - (bar_width * (bar_shift + 1));
             double dy = this.top + this.panel.height + this.panel2.height;
            Calendar date = Calendar.getInstance();
            date.clear();
   
           
            
            for (int i = chart.length - 1; i >= 0; i--)
            {
                int id = i;

                date.setTimeInMillis(chart.date[id]);
                

                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();
                double h = metrics.getStringBounds(string, graphics).getHeight();

                if (dx <= mouse_x)
                {
                    
                    int sx = (int)(dx - (w / 2));
                    int sy = (int)(dy + 10 + h);
                    
                    graphics.setColor(Color.black);
                    graphics.fill(new Rectangle.Double(dx - 2 - (w / 2), dy + 15 - 2, w + 2, h));
                    
                    graphics.setColor(Color.white);
                    graphics.drawString(string, sx, sy);
                    
                    
                    graphics.setColor(Color.black);
                    
                    int step = 70;
                    int step2 = 170;
                    int adjustment = 80;
                    
                    
                    graphics.drawString("Date:", panel.left + panel.width + step, top + 15 + adjustment);
                    graphics.drawString("Open:", panel.left + panel.width + step, top + (15 * 2) + adjustment);
                    graphics.drawString("High:", panel.left + panel.width + step, top + (15 * 3) + adjustment);
                    graphics.drawString("Low:", panel.left + panel.width + step, top + (15 * 4) + adjustment);
                    graphics.drawString("Close:", panel.left + panel.width + step, top + (15 * 5) + adjustment);
                    
                    double open = chart.open[id];
                    double high = chart.high[id];
                    double low = chart.low[id];
                    double close = chart.close[id];

                    graphics.drawString(Formatters.df.format(date.getTime()), panel.left + panel.width + step2, top + 15 + adjustment);   
                    graphics.drawString(Formatters.pf5.format(open), panel.left + panel.width + step2, top + (15 * 2) + adjustment);
                    graphics.drawString(Formatters.pf5.format(high), panel.left + panel.width + step2, top + (15 * 3) + adjustment);
                    graphics.drawString(Formatters.pf5.format(low), panel.left + panel.width + step2, top + (15 * 4) + adjustment);
                    graphics.drawString(Formatters.pf5.format(close), panel.left + panel.width + step2, top + (15 * 5) + adjustment);

                    
                    break;
                }
                
                dx -= bar_width;
            }
            
            

                     
            
            
            
            

        }
        
        
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
    }
    
    public static boolean allow = true;
    
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        try
        {
        
      //  PaintPanel((Graphics2D)g, panel);
     //   PaintCrossHair((Graphics2D)g, panel);
        
        if (chart != null)
        {
            image = new BufferedImage(this.getWidth(), this.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
            
            //image = new BufferedImage(600, 400, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();            
            g2.setBackground(Color.white);
            g2.fill(new Rectangle.Double(0, 0, image.getWidth(), image.getHeight()));      
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
            
           
            
            PaintPanel(g2, panel);
            PaintPanel(g2, panel2);         
            PaintPriceInfo(g2);
            PaintCrossHair(g2, panel);
            
            
            g.drawImage(image, 0, 0, null);
            
            frmMain.f.ts.send_data();
            
        }
        else if (image != null)
        {
            g.drawImage(image, 0, 0, null);     
            
            
            frmMain.f.ts.send_data();
            
            
        }
        
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
    }
    
    
}
