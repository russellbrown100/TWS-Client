/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import java.util.ArrayList;
import java.util.Calendar;


class TradingSystem {
    
    public Contract contract;
    public static int chart_length = 1000;
    public Chart chart2;
    public Chart chart3;
    public ArrayList trading_sessions;
    
    public ArrayList positions;
    
    public int order_quantity;
    
    public boolean allow_exit;
    public double saved_position;
    
    
    public boolean send_data = true;
    public boolean send_price_data = false;
    
    
   // public Account account;
    
   // public AccountSummary account_summary;
    
    
    public TradingSystem()
    {
    }
    
    public void send_data()
    {
        try
        {
            if (WebSocketServer.server != null)
            {
                
            
                String string = "";
                
                int length = frmMain.f.chart_window.bars_per_window;
                
                string += length + "|";
                
                Calendar date = Calendar.getInstance();
                date.clear();
                
                for (int i = 0; i < length; i++)
                {
                    int id = chart2.length - 1 - i;
                    
                    date.setTimeInMillis(chart2.date[id]);                    
                    string += Formatters.df.format(date.getTime()) + "|";                    
                    string += chart2.open[id] + "|";
                    string += chart2.high[id] + "|";
                    string += chart2.low[id] + "|";
                    string += chart2.close[id] + "|";
                }
                


                if (chart2.indicator_properties.size() > 0)
                {
                    for (int i2 = 0; i2 < chart2.indicator_properties.size(); i2++)
                    {
                        Object[] ind = (Object[])chart2.indicator_properties.get(i2);
                        if (ind[0].toString().equals("simple moving average"))
                        {
                            SimpleMovingAverage ob = (SimpleMovingAverage)chart2.indicators.get(i2); 
                        }
                        else if (ind[0].toString().equals("exponential moving average"))
                        {
                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart2.indicators.get(i2); 
                        }
                        else if (ind[0].toString().equals("ema macd"))
                        {
                            EMAMacd ob = (EMAMacd)chart2.indicators.get(i2); 
                        }
                        else if (ind[0].toString().equals("vidya"))
                        {
                            VIDYA ob = (VIDYA)chart2.indicators.get(i2); 
                        }
                        else if (ind[0].toString().equals("donchian channels"))
                        {
                            DonchianChannels ob = (DonchianChannels)chart2.indicators.get(i2); 

                            string += "donchian channels" + "|";

                            for (int i = 0; i < length; i++)
                            {
                                int id = chart2.length - 1 - i;
                                string += ob.upper_channel[id] + "|";
                                string += ob.lower_channel[id] + "|";
                            }

                        }
                        else if (ind[0].toString().equals("chande kroll stop"))
                        {
                            ChandeKrollStop ob = (ChandeKrollStop)chart2.indicators.get(i2); 

                        }
                    }
                }


                WebSocketServer.send_data(string);


            }
            
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    
    
    public void init()
    {
        
        
        chart2 = new Chart(chart_length, 20);        
        DonchianChannels donch = new DonchianChannels(chart2);       
        donch.period = 12;   
        chart2.indicators.add(donch);
        chart2.indicator_properties.add(new Object[]{"donchian channels"});   
        chart2.chart_loaded = false;
        
        chart3 = new Chart(chart_length, 60);     
        donch = new DonchianChannels(chart3);       
        donch.period = 12;   
        chart3.indicators.add(donch);
        chart3.indicator_properties.add(new Object[]{"donchian channels"});
        chart3.chart_loaded = false;


//        contract = new Contract();
//        contract.symbol("USD");
//        contract.secType("CASH");
//        contract.currency("CAD");
//        contract.exchange("IDEALPRO");

        contract = new Contract();
        contract.symbol("QQQ");
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        
        
        trading_sessions = new ArrayList();
        trading_sessions.add(new TradingSession(Calendar.SUNDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.FRIDAY, new Time(0, 0), new Time(17, 0)));
        
        
        
        
        int count = 0;
        for (int i = 0; i < 24; i++)
        {
            for (int m = 0; m < 60; m++)
            {
//                System.out.println(i + "  " + m);
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    
//                    System.out.println("    " + session.start_time.hour + " " + session.start_time.min);
                    
                    if (i == session.start_time.hour &&
                            m == session.start_time.min)
                    {
                        session.start_time.count = count;
                    }
                }
                
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    if (i == session.end_time.hour &&
                            m == session.end_time.min)
                    {
                        session.end_time.count = count;
                    }
                }
                
                count++;
            }
        }
        
        
    }
    
    
    public void init2()
    {
  
        /*
        chart2 = new Chart(chart_length, 10);    
        chart2.chart_description = "chart 1";   
        DonchianChannels donch = new DonchianChannels(chart2);       
        donch.period = 10;   
        chart2.indicators.add(donch);
        chart2.indicator_properties.add(new Object[]{"donchian channels"});        
        
        
        chart3 = new Chart(chart_length, 60); 
        chart3.chart_description = "chart 2";   
        donch = new DonchianChannels(chart3);       
        donch.period = 10;   
        chart3.indicators.add(donch);
        chart3.indicator_properties.add(new Object[]{"donchian channels"});
*/
        

        
        chart2 = new Chart(chart_length, 20);       
        chart2.chart_description = "chart 1";
        DonchianChannels donch = new DonchianChannels(chart2);       
        donch.period = 10;   
        chart2.indicators.add(donch);
        chart2.indicator_properties.add(new Object[]{"donchian channels"});        
        
        
        chart3 = new Chart(chart_length, 60);     
        chart3.chart_description = "chart 2";
        donch = new DonchianChannels(chart3);       
        donch.period = 10;   
        chart3.indicators.add(donch);
        chart3.indicator_properties.add(new Object[]{"donchian channels"});

        
        order_quantity = 100000;
        
        allow_exit = true;
        saved_position = 0;
        

        contract = new Contract();
        contract.symbol("USD");
        contract.secType("CASH");
        contract.currency("CAD");
        contract.exchange("IDEALPRO");

        
        
        trading_sessions = new ArrayList();
        trading_sessions.add(new TradingSession(Calendar.SUNDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.FRIDAY, new Time(0, 0), new Time(17, 0)));
        
        
        
        
        int count = 0;
        for (int i = 0; i < 24; i++)
        {
            for (int m = 0; m < 60; m++)
            {
//                System.out.println(i + "  " + m);
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    
//                    System.out.println("    " + session.start_time.hour + " " + session.start_time.min);
                    
                    if (i == session.start_time.hour &&
                            m == session.start_time.min)
                    {
                        session.start_time.count = count;
                    }
                }
                
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    if (i == session.end_time.hour &&
                            m == session.end_time.min)
                    {
                        session.end_time.count = count;
                    }
                }
                
                count++;
            }
        }
        
        
    }
    
    
    
    
    public void send_buy_order(String subject, String message, double position)
    {            
        
        
        MessageHandler.SendMessage(subject, message);
        
        Order order = new Order();
        order.action("BUY");
        order.orderType("MKT");
        order.totalQuantity(position);
        
        frmMain.f.connection.sent_order_ids.add(frmMain.f.connection.nextOrderId);
                
        frmMain.f.connection.clientSocket.placeOrder(
                frmMain.f.connection.nextOrderId, 
                this.contract, order);
                 
        frmMain.f.connection.clientSocket.reqIds(-1);
        
    }
    
    public void send_sell_order(String subject, String message, double position)
    {            
        MessageHandler.SendMessage(subject, message);
        
        Order order = new Order();
        order.action("SELL");
        order.orderType("MKT");
        order.totalQuantity(position);
                        
        frmMain.f.connection.sent_order_ids.add(frmMain.f.connection.nextOrderId);
        
        frmMain.f.connection.clientSocket.placeOrder(
                frmMain.f.connection.nextOrderId, 
                this.contract, order);
                 
        frmMain.f.connection.clientSocket.reqIds(-1);
                 
        
    }
    
    
    public static boolean HasOpenOrders()
    {
        boolean result = false;
        
        if (frmMain.f.connection.open_orders != null)
        {
            if (frmMain.f.connection.open_orders.size() > 0)
            {
                result = true;
            }
        
//            for (int i = 0; i < frmMain.f.connection.open_orders.size(); i++)
//            {
//                int oid = (int)frmMain.f.connection.open_orders.get(i);
//
//                for (int i2 = 0; i2 < frmMain.f.connection.sent_order_ids.size(); i2++)
//                {
//                    int sid = (int)frmMain.f.connection.sent_order_ids.get(i2);
//                    if (sid == oid)
//                    {
//                        result = true;
//                        break;
//                    }
//                }
//
//                if (result == true)
//                {
//                    break;
//                }
//            }
        
        }
        
        return result;
    }
    
    public static void CancelOpenOrders()
    {
        System.out.println("cancel open orders:");
        
        if (frmMain.f.connection.open_orders != null)
        {
            for (int i = 0; i < frmMain.f.connection.open_orders.size(); i++)
            {
                int oid = (int)frmMain.f.connection.open_orders.get(i);
                
                System.out.println("canceling:  " + oid);
                
                frmMain.f.connection.clientSocket.cancelOrder(oid);
                
//                for (int i2 = 0; i2 < frmMain.f.connection.sent_order_ids.size(); i2++)
//                {
//                    int sid = (int)frmMain.f.connection.sent_order_ids.get(i2);
//                    if (sid == oid)
//                    {
//                        System.out.println("canceling " + oid);
//                        frmMain.f.connection.clientSocket.cancelOrder(oid);
//                        break;
//                    }
//                }
            }
        }
        
        
        
    }
    
    public void EvaluateEntry()
    {
        try
        {
            
            
            
            DonchianChannels dch2 = (DonchianChannels)this.chart2.indicators.get(0);

            if (frmMain.f.connection.price_data.midpoint > dch2.upper_channel[chart2.length - 2])
            {
                dch2.lcount = 0;
            }

            if (frmMain.f.connection.price_data.midpoint < dch2.lower_channel[chart2.length - 2])
            {
                dch2.ucount = 0;
            }            

            DonchianChannels dch3 = (DonchianChannels)this.chart3.indicators.get(0);

            if (frmMain.f.connection.price_data.midpoint > dch3.upper_channel[chart3.length - 2])
            {
                dch3.lcount = 0;
            }

            if (frmMain.f.connection.price_data.midpoint < dch3.lower_channel[chart3.length - 2])
            {
                dch3.ucount = 0;
            }

            
            if (positions != null)
            {
            
                
                double iqty = 0;

                for (int i = 0; i < positions.size(); i++)
                {
                    Object[] ob = (Object[])positions.get(i);

                    if (ob[0].toString().equals(contract.symbol()))
                    {
                        iqty = (double)ob[2];
                        break;
                    }
                }
                
                if (iqty == 0 && HasOpenOrders() == false)
                {

                    if (frmMain.f.connection.signal_count <= 3)
                    {

                        String time = Formatters.df.format(frmMain.f.connection.current_time.getTime());
                        String price = Formatters.pf5.format(frmMain.f.connection.price_data.midpoint);

                        if (dch3.upper_channel[chart2.length - 2] > dch3.upper_channel[chart2.length - 3] &&
                                dch3.upper_channel[chart2.length - 3] > dch3.upper_channel[chart2.length - 4] &&
                                dch3.upper_channel[chart2.length - 4] > dch3.upper_channel[chart2.length - 5])
                        {

                            if (dch2.ucount == 0 && frmMain.f.connection.price_data.prev_price != 0 && 
                                         frmMain.f.connection.price_data.midpoint > dch2.upper_channel[chart2.length - 2] &&
                                         frmMain.f.connection.price_data.prev_price <= dch2.upper_channel[chart2.length - 2] &&
                                         frmMain.f.connection.price_data.prev_price > dch2.lower_channel[chart2.length - 2])
                            {
                                
                                
                                String message = "crossing upper channel " + time + " " + price + " " + String.valueOf(frmMain.f.connection.nextOrderId);
                                System.out.println(message);
                                SendMail.send_message("Trading Alert", message);
                                send_buy_order("tws", "sending buy order - open: " + frmMain.f.connection.price_data.ask, order_quantity);
                                dch2.ucount++;
                                frmMain.f.connection.signal_count++;
                            }

                        }


                        if (dch3.lower_channel[chart2.length - 2] < dch3.lower_channel[chart2.length - 3] &&
                                dch3.lower_channel[chart2.length - 3] < dch3.lower_channel[chart2.length - 4] &&
                                dch3.lower_channel[chart2.length - 4] < dch3.lower_channel[chart2.length - 5])
                        {

                            if (dch2.lcount == 0 && frmMain.f.connection.price_data.prev_price != 0 && 
                                    frmMain.f.connection.price_data.midpoint < dch2.lower_channel[chart2.length - 2] &&
                                    frmMain.f.connection.price_data.prev_price >= dch2.lower_channel[chart2.length - 2]&&
                                    frmMain.f.connection.price_data.prev_price < dch2.upper_channel[chart2.length - 2])
                            {
                                
                                
                                
                                
                                String message = "crossing lower channel " + time + " " + price + " " + String.valueOf(frmMain.f.connection.nextOrderId);
                                System.out.println(message);
                                SendMail.send_message("Trading Alert", message);
                                send_sell_order("tws", "sending sell order - open: " + frmMain.f.connection.price_data.bid, order_quantity);
                                dch2.lcount++;
                                frmMain.f.connection.signal_count++;
                                
                                
                            }

                        }

                    }
                
                }
            
            }
            
            
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
        
    }
    
    public void EvaluateExit()
    {
        try
        {
            

            
            
            if (positions != null)
            {
                
                if (allow_exit == true)
                {
                
                
                
                    double iqty = 0;
                    double avgCst = 0;

                    for (int i = 0; i < positions.size(); i++)
                    {
                        Object[] ob = (Object[])positions.get(i);

                        if (ob[0].toString().equals(contract.symbol()))
                        {
                            iqty = (double)ob[2];
                            avgCst = (double)ob[3];
                            break;
                        }
                    }



                    if (iqty > 0 && frmMain.f.connection.price_data.bid > avgCst + 0.0003)
                    {
                        allow_exit = false;
                        saved_position = iqty;

                        String message = "sending sell order - close: " + 
                                frmMain.f.connection.price_data.bid + " " + 
                                String.valueOf(iqty);

                        send_sell_order("tws", message, Math.abs(iqty));


                    }                             
                    else if (iqty < 0 && frmMain.f.connection.price_data.ask < avgCst - 0.0003)
                    {                
                        allow_exit = false;
                        saved_position = iqty; 


                        String message = "sending buy order - close: " + 
                                frmMain.f.connection.price_data.ask + " " + 
                                String.valueOf(iqty);

                        send_buy_order("tws", message, Math.abs(iqty));

                    }

                    /*

                    for testing...

                    double amt = 0.0001;
                        
                    if (iqty > 0)
                    {

                        if (frmMain.f.connection.price_data.bid > avgCst + amt || frmMain.f.connection.price_data.bid < avgCst - amt)
                        {
                            allow_exit = false;
                            saved_position = iqty;

                            String message = "sending sell order - close: " + 
                                    frmMain.f.connection.price_data.bid + " " + 
                                    String.valueOf(iqty);

                            send_sell_order("tws", message, Math.abs(iqty));
                            

                        }

                    }                             
                    else if (iqty < 0)
                    {                 

                        if (frmMain.f.connection.price_data.ask < avgCst - amt || frmMain.f.connection.price_data.ask > avgCst + amt)
                        {
                            allow_exit = false;
                            saved_position = iqty;

                            String message = "sending buy order - close: " + 
                                    frmMain.f.connection.price_data.ask + " " + 
                                    String.valueOf(iqty);

                            send_buy_order("tws", message, Math.abs(iqty));
                            
                        
                        }

                    }
                    */
                    
                    

                
                }
                
                

                
            }
            
            
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
            
        }
        
        
    }
    
    public void update_chart(Calendar date, Chart chart, boolean perform_trading)
    {
        try
        {
            // charts initialized when system is loaded
        
            if (chart.next_date == null)
            {
                
                
                // note:  timer stops when connection lost, so this shouldn't get executed

                chart.next_date = Calendar.getInstance();
                chart.next_date.clear();
                chart.next_date.set(Calendar.YEAR, chart.price_record.date.get(Calendar.YEAR));
                chart.next_date.set(Calendar.MONTH, chart.price_record.date.get(Calendar.MONTH));
                chart.next_date.set(Calendar.DAY_OF_MONTH, chart.price_record.date.get(Calendar.DAY_OF_MONTH));
                Calendar prev_date = (Calendar)chart.next_date.clone();
                while (true)
                { 
                    prev_date = (Calendar)chart.next_date.clone();                                   
                    chart.period = chart.periods[chart.period_id];                       
                    chart.next_date.add(Calendar.MINUTE, chart.period);
                    chart.period_id++;
                    if (chart.period_id >= chart.periods.length)
                    {
                        chart.period_id = 0;
                    }  
                    if (chart.next_date.compareTo(chart.price_record.date) > 0)
                    {
                        break;
                    }
                }   
                chart.date[chart.length - 1] = prev_date.getTimeInMillis();
                chart.open[chart.length - 1] = chart.price_record.open;
                chart.high[chart.length - 1] = chart.price_record.high;
                chart.low[chart.length - 1] = chart.price_record.low;
                chart.close[chart.length - 1] = chart.price_record.close;
                chart.volume[chart.length - 1] = chart.price_record.volume;

                
                

            }
            else
            {

                
                
                if (date.compareTo(chart.next_date) >= 0)
                {
                    
                    

    //                System.out.println("chart record: " +  
    //                        chart.open[chart.length - 1] + "  " + 
    //                        chart.high[chart.length - 1] + "  " +
    //                        chart.low[chart.length - 1] + "  " +
    //                        chart.close[chart.length - 1]);

                    // calculate indicators
                    if (chart.indicator_properties.size() > 0)
                    {
                        for (int i = 0; i < chart.indicator_properties.size(); i++)
                        {
                            Object[] ind = (Object[])chart.indicator_properties.get(i);
                            if (ind[0].toString().equals("simple moving average"))
                            {
                                SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i); 
                                ob.Calculate(chart);
                            }
                            else if (ind[0].toString().equals("exponential moving average"))
                            {
                                ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i); 
                                ob.Calculate(chart);                            
                            }
                            else if (ind[0].toString().equals("ema macd"))
                            {
                                EMAMacd ob = (EMAMacd)chart.indicators.get(i); 
                                ob.Calculate(chart);
                            }
                            else if (ind[0].toString().equals("vidya"))
                            {
                                VIDYA ob = (VIDYA)chart.indicators.get(i); 
                                ob.Calculate(chart);
                            }
                            else if (ind[0].toString().equals("donchian channels"))
                            {
                                DonchianChannels ob = (DonchianChannels)chart.indicators.get(i); 
                                ob.Calculate(chart);
                            }
                            else if (ind[0].toString().equals("chande kroll stop"))
                            {
                                ChandeKrollStop ob = (ChandeKrollStop)chart.indicators.get(i); 
                                ob.Calculate(chart);

                            }
                        }
                    }


                     // perform trading


                    //if (perform_trading == true && frmMain.f.ts.chart2.chart_loaded == true && frmMain.f.ts.chart3.chart_loaded == true)
                    //{
                       
                        


                    // roll chart

                

                    for (int i = 1; i < chart.length; i++)
                    {
                        chart.date[i - 1] = chart.date[i];
                        chart.open[i - 1] = chart.open[i];
                        chart.high[i - 1] = chart.high[i];
                        chart.low[i - 1] = chart.low[i];
                        chart.close[i - 1] = chart.close[i];
                        chart.volume[i - 1] = chart.volume[i];
                        chart.open_trades[i - 1] = chart.open_trades[i];
                        chart.close_trades[i - 1] = chart.close_trades[i];

                        for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
                        {
                            Object[] ind = (Object[])chart.indicator_properties.get(i2);
                            if (ind[0].toString().equals("simple moving average"))
                            {
                                SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2);                            
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("exponential moving average"))
                            {
                                ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);                            
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("ema macd"))
                            {
                                EMAMacd ob = (EMAMacd)chart.indicators.get(i2);  
                                ob.ema1.values[i - 1] = ob.ema1.values[i];
                                ob.ema2.values[i - 1] = ob.ema2.values[i];
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("vidya"))
                            {
                                VIDYA ob = (VIDYA)chart.indicators.get(i2);                            
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("donchian channels"))
                            {
                                DonchianChannels ob = (DonchianChannels)chart.indicators.get(i2);                            
                                ob.upper_channel[i - 1] = ob.upper_channel[i];                      
                                ob.lower_channel[i - 1] = ob.lower_channel[i];
                            }
                            else if (ind[0].toString().equals("chande kroll stop"))
                            {
                                ChandeKrollStop ob = (ChandeKrollStop)chart.indicators.get(i2);                            
                                ob.atr[i - 1] = ob.atr[i];                         
                                ob.buy_stop[i - 1] = ob.buy_stop[i];                         
                                ob.sell_stop[i - 1] = ob.sell_stop[i];
                            }
                        }



                    }

                    
                

                    Calendar prev_date = (Calendar)chart.next_date.clone();
                    while (true)
                    {
                        prev_date = (Calendar)chart.next_date.clone();
                        chart.period = chart.periods[chart.period_id];                       
                        chart.next_date.add(Calendar.MINUTE, chart.period);
                        chart.period_id++;
                        if (chart.period_id >= chart.periods.length)
                        {
                            chart.period_id = 0;
                        }  
                        if (chart.next_date.compareTo(date) > 0)
                        {
                            break;
                        }
                    }   
                    
                    

                    if (chart.chart_loaded == false)
                    {

                        chart.date[chart.length - 1] = prev_date.getTimeInMillis();
                        chart.open[chart.length - 1] = chart.price_record.open;
                        chart.high[chart.length - 1] = chart.price_record.high;
                        chart.low[chart.length - 1] = chart.price_record.low;
                        chart.close[chart.length - 1] = chart.price_record.close;
                        chart.volume[chart.length - 1] = chart.price_record.volume;

                    }
                    else
                    {
                        
                         chart.date[chart.length - 1] = prev_date.getTimeInMillis();
                        chart.open[chart.length - 1] = chart.close[chart.length - 2];
                        chart.high[chart.length - 1] = chart.close[chart.length - 2];
                        chart.low[chart.length - 1] = chart.close[chart.length - 2];
                        chart.close[chart.length - 1] = chart.close[chart.length - 2];
                        chart.volume[chart.length - 1] = chart.close[chart.length - 2];

                    }
                    
                    chart.open_trades[chart.length - 1] = 0;
                    chart.close_trades[chart.length - 1] = 0;


                    for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
                    {
                        Object[] ind = (Object[])chart.indicator_properties.get(i2);
                        if (ind[0].toString().equals("simple moving average"))
                        {
                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2);                            
                            ob.values[chart.length - 1] = 0;
                        }
                        else if (ind[0].toString().equals("exponential moving average"))
                        {
                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);                            
                            ob.values[chart.length - 1] = 0;
                        }
                        else if (ind[0].toString().equals("ema macd"))
                        {
                            EMAMacd ob = (EMAMacd)chart.indicators.get(i2);  
                            ob.ema1.values[chart.length - 1] = 0;
                            ob.ema2.values[chart.length - 1] = 0;
                            ob.values[chart.length - 1] = 0;
                        }
                        else if (ind[0].toString().equals("vidya"))
                        {
                            VIDYA ob = (VIDYA)chart.indicators.get(i2);                            
                            ob.values[chart.length - 1] = 0;
                        }
                        else if (ind[0].toString().equals("donchian channels"))
                        {
                            DonchianChannels ob = (DonchianChannels)chart.indicators.get(i2);                            
                            ob.upper_channel[chart.length - 1] = 0;                      
                            ob.lower_channel[chart.length - 1] = 0;
                            ob.ucount = 0;
                            ob.lcount = 0;
                        }
                        else if (ind[0].toString().equals("chande kroll stop"))
                        {
                            ChandeKrollStop ob = (ChandeKrollStop)chart.indicators.get(i2);                            
                            ob.atr[chart.length - 1] = 0;                         
                            ob.buy_stop[chart.length - 1] = 0;                         
                            ob.sell_stop[chart.length - 1] = 0;
                        }
                    }

                    
                    
                    if (chart.chart_loaded == false)
                    {
                        chart.bars_loaded++;
                    }                    
                    

                }
                else
                {

                    if (chart.chart_loaded == false)
                    {                    
                        if (chart.price_record.high > chart.high[chart.length - 1]) chart.high[chart.length - 1] = chart.price_record.high;
                        if (chart.price_record.low < chart.low[chart.length - 1]) chart.low[chart.length - 1] = chart.price_record.low;
                        chart.close[chart.length - 1] = chart.price_record.close;
                    }
                    else if (frmMain.f.connection.price_data.midpoint > 0)
                    {
                        if (frmMain.f.connection.price_data.midpoint > chart.high[chart.length - 1]) chart.high[chart.length - 1] = frmMain.f.connection.price_data.midpoint;
                        if (frmMain.f.connection.price_data.midpoint < chart.low[chart.length - 1]) chart.low[chart.length - 1] = frmMain.f.connection.price_data.midpoint;
                        chart.close[chart.length - 1] = frmMain.f.connection.price_data.midpoint;
                    }
                    

                    if (perform_trading == true && frmMain.f.ts.chart2.chart_loaded == true && frmMain.f.ts.chart3.chart_loaded == true)
                    {
                        
                        EvaluateEntry();

                        EvaluateExit();



                    }

                }
            }
        
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
            
    
    }
    
    
    public void update(Calendar date)
    {
        try
        {
        
            boolean session_open = false;

            boolean complete = false;
            int count = 0;
            for (int h = 0; h < 24; h++)
            {
                for (int m = 0; m < 60; m++)
                {
                    if (h == date.get(Calendar.HOUR_OF_DAY) &&
                            m == date.get(Calendar.MINUTE))
                    {
                        complete = true;
                        break;
                    }

                    count++;
                }

                if (complete) break;
            }

            for (int si = 0; si < trading_sessions.size(); si++)
            {
                TradingSession session = (TradingSession)trading_sessions.get(si);

                if (date.get(Calendar.DAY_OF_WEEK) == session.day &&
                        count >= session.start_time.count &&
                        count < session.end_time.count)
                {
                    session_open = true;
                    break;
                }
            }

            if (session_open == true)
            {

                if (date.get(Calendar.HOUR_OF_DAY) == 0) frmMain.f.connection.signal_count = 0;
                
                
                update_chart(date, chart3, false);

                update_chart(date, chart2, true);

                
                
                
                
//                if (this.send_price_data == true)
//                {
//                    send_price_data();
//                }

            }

        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
    }
    
    
    
    
    
    
}
