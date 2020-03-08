/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;
import java.util.Calendar;
import java.util.*;

class PriceData
{    
    public double bid;
    public double ask;
    public double midpoint;
    public double prev_price;
    
    public PriceData()
    {
        bid = 0;
        ask = 0;
        midpoint = 0;
        prev_price = 0;
    }
}

class BaseConnection implements EWrapper
{
    public boolean connection_acknowledged;
    public boolean connection_error;
    public boolean connection_lost;
    public boolean has_market_data;   
    public PriceData price_data;
    public Calendar current_time;
    public int nextOrderId;
    public ArrayList open_orders;
    public ArrayList sent_order_ids;    
    public int signal_count;
    
    public void updateMktDepthL2(int a, int b, String c, int d, int e, double f, int g, boolean h)
    {
        
    }
    
    public void tickByTickAllLast(int a, int b, long c, double d, int e, TickAttribLast f, String s1, String s2)
    {
        
    }
    
    public void completedOrdersEnd()
    {
        
    }
    
    public void completedOrder(Contract contract, Order order, OrderState state)
    {
        
    }
    
    public void orderBound(long l, int a, int b)
    {
        
    }
    
    public void tickByTickBidAsk(int i, long l, double d, double d1, int i1, int i2, TickAttribBidAsk taba)
    {
        
    }
    
    public void tickPrice(int tickerId, int field, double price, TickAttrib attribs)
    {
        try
        {
            if (field == 1) // bid
            {
                price_data.bid = price;
            }
            else if (field == 2) // ask
            {
                price_data.ask = price;
            }

            if (price_data.bid > 0 && price_data.ask > 0)
            {
                price_data.midpoint = (price_data.bid + price_data.ask) / 2;
                
                if (frmMain.f.ts.chart2.chart_loaded == true && frmMain.f.ts.chart3.chart_loaded == true)
                {
                
                    frmMain.f.ts.update(current_time);

                    frmMain.f.chart_window.repaint();
            
                }
                
                
                price_data.prev_price = price_data.midpoint;
                
            }
            
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
    }
    
    public void tickSize(int tickerId, int field, int size)
    {
        
    }
    
    public void tickOptionComputation(int tickerId, int field,
            double impliedVol, double delta, double optPrice,
            double pvDividend, double gamma, double vega, double theta,
            double undPrice)
    {
        
    }

    public void tickGeneric(int tickerId, int tickType, double value)
    {
        
    }

    public void tickString(int tickerId, int tickType, String value)
    {
        
    }

    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate)
    {
        
    }
    
    public void orderStatus(int orderId, String status, double filled,
            double remaining, double avgFillPrice, int permId, int parentId,
            double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) 
    {
                
        if (open_orders != null) open_orders.add(orderId);        
        
//        System.out.println("orderStatus: " + orderId + "  " + status);
        
    }
    public void openOrder(int orderId, Contract contract, Order order,
            OrderState orderState)
    {
//        System.out.println("openOrder: " + orderId + " " + contract.symbol() + " " + contract.currency() + " " + orderState.getStatus());
    }
    public void openOrderEnd()
    {
//        System.out.println(" open order end");
        
        
    }
    public void updateAccountValue(String key, String value, String currency,
            String accountName) 
    {
        
    }

    public void updatePortfolio(Contract contract, double position,
            double marketPrice, double marketValue, double averageCost,
            double unrealizedPNL, double realizedPNL, String accountName)
    {
        
    }

    public void updateAccountTime(String timeStamp)
    {
        
    }

    public void accountDownloadEnd(String accountName)
    {
        
    }

    public void nextValidId(int orderId)
    {
        System.out.println("next order id:" + orderId);
        nextOrderId = orderId;
    }

    public void contractDetails(int reqId, ContractDetails contractDetails)
    {
        
    }

    public void bondContractDetails(int i, ContractDetails cd)
    {
        
    }

    public void contractDetailsEnd(int reqId) 
    {
        
    }

    public void execDetails(int reqId, Contract contract, Execution execution)
    {
        System.out.println(reqId + "  " + contract.symbol() + " " + contract.currency() + " " + execution.orderId() + "  ");
    }
    public void execDetailsEnd(int reqId)
    {
        System.out.println("exec detaild end:");
    }
    
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size)
    {
       
        
    }

    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size)
    {
        
    }

    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange)
    {
        
    }

    public void managedAccounts(String accountsList)
    {
        
    }

    public void receiveFA(int faDataType, String faXmlData)
    {
        
    }
    
    
    public void historicalData(int reqId, Bar bar)
    {
            System.out.println(bar.time() + " " + bar.open() + " " + bar.high() + " " + bar.low() + " " + bar.close());

            try
            {
                frmMain.f.ts.chart2.chart_loaded = false;
                frmMain.f.ts.chart3.chart_loaded = false;

                

                Calendar date = Calendar.getInstance();
                date.clear();

                date.setTime(frmMain.f.format.parse(bar.time()));

                
               frmMain.f.ts.chart2.price_record = new PriceRecord(
                        date, 
                        bar.open(),
                        bar.high(),
                        bar.low(),
                        bar.close(),
                        bar.volume()
                    );
                
                frmMain.f.ts.chart3.price_record = new PriceRecord(
                        date, 
                        bar.open(),
                        bar.high(),
                        bar.low(),
                        bar.close(),
                        bar.volume()
                    );
                
                
                
                frmMain.f.ts.update(date);


            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        
    }
    
    public void scannerParameters(String xml)
    {
        
    }

    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr)
    {
        
    }

    public void scannerDataEnd(int reqId)
    {
        
    }

    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double WAP, int count)
    {
        
    }
    
    
    public void currentTime(long time)
    {
        try
        {
        
            current_time.setTimeInMillis(time * 1000);
            

            frmMain.f.connection.open_orders = new ArrayList();
            frmMain.f.connection.clientSocket.reqAllOpenOrders();
                    
                    
            if (TradingSystem.HasOpenOrders() == true) 
            {
                TradingSystem.CancelOpenOrders();
            }
            
            frmMain.f.ts.update(current_time);
            frmMain.f.chart_window.repaint();
            
            
        
        }
        catch (Exception ex)
        {
            ExceptionHandler.HandleException(ex);
        }
        
    }
    
    public void fundamentalData(int reqId, String data)
    {
        
    }

    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract)
    {
        
    }

    public void tickSnapshotEnd(int tickerId)
    {
        
    }

    public void marketDataType(int reqId, int marketDataType)
    {
        
    }

    public void commissionReport(CommissionReport commissionReport)
    {
        
    }
    
    public void position(String account, Contract contract, double pos, double avgCost)
    {
//        System.out.println("position:  " + current_time.getTime() + " " + contract.symbol() + " " + contract.currency() + " " + pos + " " + avgCost);
        
        frmMain.f.ts.positions.add(new Object[]{contract.symbol(), contract.currency(), pos, avgCost});
        
        
       // System.out.println(contract.symbol() + " " + contract.currency() + " " + pos + " " + avgCost);
    }
    public void positionEnd()
    {

        double iqty = 0;

        for (int i = 0; i < frmMain.f.ts.positions.size(); i++)
        {
            Object[] ob = (Object[])frmMain.f.ts.positions.get(i);

            if (ob[0].toString().equals(frmMain.f.ts.contract.symbol()))
            {
                iqty = (double)ob[2];
                break;
            }
        }
        
        if (frmMain.f.ts.saved_position != 0 && iqty != frmMain.f.ts.saved_position)
        {
            frmMain.f.ts.allow_exit = true;
            frmMain.f.ts.saved_position = 0;
        }
                
//        System.out.println("positions end");
    }
    
    public void accountSummary(int reqId, String account, String tag, String value, String currency)
    {
        
    }

    public void accountSummaryEnd(int reqId)
    {
        
    }

    public void verifyMessageAPI(String string)
    {
        
    }

    public void verifyCompleted(boolean bln, String string)
    {
        
    }

    public void verifyAndAuthMessageAPI(String string, String string1)
    {
        
    }

    public void verifyAndAuthCompleted(boolean bln, String string)
    {
        
    }

    public void displayGroupList(int reqId, String groups)
    {
        
    }

    public void displayGroupUpdated(int reqId, String contractInfo)
    {
        
    }
    
    public void error (Exception e)
    {
        System.out.println(e.getMessage());
    }
    
    public void error (String str)
    {
        System.out.println(str);
    }
    
    public void error (int id, int errorCode, String errorMsg)
    {
        System.out.println(id + " " + errorCode + " " + errorMsg);
        
        if (errorCode == 507) // Bad Message Length null
        {
            frmMain.f.connection.connection_error = true;
        }        
        else if (errorCode == 1100) // connection lost
        {
            frmMain.f.connection.connection_lost = true;
        }
        else if (errorCode == 1102) // connection restored
        {
            frmMain.f.connection.connection_lost = false;
            frmMain.f.LoadTradingSystem();
            frmMain.f.start_server();
            System.out.println("system restored");
        }
        
    }
    
    public void connectionClosed()
    {
        
    }

    public void connectAck()
    {   
        this.connection_acknowledged = true;
        
    }
    public void positionMulti(int requestId, String account, String modelCode, Contract contract, double pos, double avgCost)
    {
        
    }

    public void positionMultiEnd(int requestId)
    {
        
    }

    public void accountUpdateMulti(int reqId, String account, String modelCode,
            String key, String value, String currency)
    {
        
    }

    public void accountUpdateMultiEnd(int reqId)
    {
        
    }

    public void securityDefinitionOptionalParameter(int i, String string, int i1, String string1, String string2, Set<String> set, Set<Double> set1)
    {
        
    }

    public void securityDefinitionOptionalParameterEnd(int i)
    {
        
    }

    public void softDollarTiers(int i, SoftDollarTier[] sdts)
    {
        
    }

    public void familyCodes(FamilyCode[] fcs)
    {
        
    }

    public void symbolSamples(int i, ContractDescription[] cds)
    {
        
    }
    
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr)
    { 
        
        frmMain.f.ts.chart2.chart_loaded = true;
        frmMain.f.ts.chart3.chart_loaded = true;
            
        frmMain.f.chart_window.chart = frmMain.f.ts.chart2;
        
//        frmMain.f.chart_window.chart = frmMain.f.ts.chart3;
        
        frmMain.f.SetScrollBar();
        frmMain.f.SetSystemStatus("System Loaded");
        
        javax.swing.JOptionPane.showMessageDialog(null, "Make sure you start the server");
        

        // error: duplicate ticker id when connection lost
        
        if (frmMain.f.connection.has_market_data == false)
        {
            frmMain.f.connection.clientSocket.reqMktData(0, frmMain.f.ts.contract, "", false, false, null);
            frmMain.f.connection.has_market_data = true;
            
            
        }
        
        frmMain.f.connection.current_time = Calendar.getInstance();
        frmMain.f.connection.current_time.clear();
        

        new Thread(() -> {

              
            while (true)
            {
                
                if (frmMain.f.connection.clientSocket.isConnected() == false)
                {
                    break;
                }
                
                if (frmMain.f.connection.connection_lost == true)
                {
                    System.out.println("connection lost - exiting timer");
                    break;
                }

                try
                {
                    frmMain.f.connection.clientSocket.reqCurrentTime();
                    
                    frmMain.f.ts.positions = new ArrayList();
                    
                    frmMain.f.connection.clientSocket.reqPositions();
                    frmMain.f.connection.clientSocket.cancelPositions();
//                    
//                    frmMain.f.connection.open_orders = new ArrayList();
//                    frmMain.f.connection.clientSocket.reqAllOpenOrders();
                    
                    Thread.sleep(500);
                }
                catch (Exception ex)
                {
                    
                    ex.printStackTrace();
                }
                    
            }


        }).start();
        
        
        
//        new Thread(() -> {
//
//            
//              
//            while (true)
//            {
//                try
//                {
//                    if (WebServer.server != null)
//                    {
//                        
//                    }
//                    
//                    Thread.sleep(1000);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//            }
//                 
//
//        }).start();


    }
    
    public void mktDepthExchanges(DepthMktDataDescription[] dmdds)
    {
        
    }

    public void tickNews(int i, long l, String string, String string1, String string2, String string3)
    {
        
    }

    public void smartComponents(int i, Map<Integer, Map.Entry<String, Character>> map)
    {
        
    }

    public void tickReqParams(int i, double d, String string, int i1)
    {
        
    }

    public void newsProviders(NewsProvider[] nps)
    {
        
    }

    public void newsArticle(int i, int i1, String string)
    {
        
    }

    public void historicalNews(int i, String string, String string1, String string2, String string3)
    {
        
    }

    public void historicalNewsEnd(int i, boolean bln)
    {
        
    }

    public void headTimestamp(int i, String string)
    {
        
    }
    
    
    public void histogramData(int reqId, List<HistogramEntry> items)
    {        
        
    }


    public void historicalDataUpdate(int i, Bar bar)
    {
        
    }

    public void rerouteMktDataReq(int i, int i1, String string)
    {
        
    }

    public void rerouteMktDepthReq(int i, int i1, String string)
    {
        
    }

    public void marketRule(int i, PriceIncrement[] pis)
    {
        
    }
    
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL)
    {
        
    }
    
    public void pnlSingle(int i, int i1, double d, double d1, double d2, double d3)
    {
        
    }

    public void historicalTicks(int i, List<HistoricalTick> list, boolean bln)
    {
        
    }

    public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean bln)
    {
        
    }

    public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean bln)
    {
        
    }

    public void tickByTickAllLast(int i, int i1, long l, double d, int i2, TickAttrib ta, String string, String string1)
    {
        
    }

    public void tickByTickBidAsk(int i, long l, double d, double d1, int i1, int i2, TickAttrib ta)
    {
        
    }

    public void tickByTickMidPoint(int i, long l, double d)
    {
        
    }
    
}


    

class ConnectionParams
{
    public String host;
    public int port;
    public int clientId;
}

public class Connection extends BaseConnection
{
    
    public EReaderSignal readerSignal;
    public EClientSocket clientSocket;
    public static ConnectionParams connectionParams;
    
    public Connection()
    {
        
        readerSignal = new EJavaSignal();
	clientSocket = new EClientSocket(this, readerSignal);
        price_data = new PriceData();
        
    }
    
    
    
    public void SetConnectParams(String host, int port, int clientID)
    {
        connectionParams = new ConnectionParams();
        connectionParams.host = host;
        connectionParams.port = port;
        connectionParams.clientId = clientID;
        
    }
    
    
    public void ProcessMessages()
    {
        
        final EReader reader = new EReader(clientSocket, readerSignal);   

            reader.start();


            new Thread(() -> {

               while (true) {
                   
                   if (clientSocket.isConnected() == false) break;

                   readerSignal.waitForSignal();
                   

                   try {
                       reader.processMsgs();

                   } catch (Exception e) {
                       System.out.println("Exception: "+e.getMessage());
                   }
                   
               }
               
            }).start();
            
            clientSocket.reqIds(0);

    }
    
    
    
}