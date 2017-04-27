import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.l33tindustries.tools.file.SystemFile;
import com.l33tindustries.tools.network.wget;

public class Stockz
{
	static String OutputType;
	
	//VERSION GOES HERE
	static final String version = "Version 3";
	
	static final String usage = " USAGE: <STOCK LIST | Stock Symbol> <default || String of stock options to Yahoo API> <JSON | default>";
	static final String output = " symbol  	price	  p/e	    peg    Day_range      52-weekrang    Day_high       Day_low        52-week_High   52-week_Low    EPS_Estimate_Current_Year       EPS_Estimate_Next_Year Price/Sales     EPS_Estimate_Next_Quarter       Market_Capitalization   Volume  Average_Daily_Volume    Previous_Close  Short_Ratio";
	static  String EndString = ";";
	
	static String defaultURLOption = "sl1rr5mwhgkje7e8p5e9j1va2ps7";
	
	static ExecutorService ThreadexecSvc;
	
	private static Logger logger = Logger.getLogger(Stockz.class.getName());
	
	private static String getCurrentMethodName() 
 	{ 
 		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace(); 
 		
 		return 	stackTraceElements[1].toString().replaceFirst(stackTraceElements[1].toString().split("\\.")[0]+"\\.", "");
 	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) 
	{	
		OutputType = args[1];
		
		//PropertyConfigurator.configure("DEBUG");
		
		ThreadexecSvc = Executors.newCachedThreadPool();
		
		if(args.length == 0)
	    {
			System.out.println(usage);
	        System.exit(0);
	    }
		if (args[0].equals("version"))
		{
			System.out.println(version);
			System.exit(0);
		}
		
		else if (args[0].equals("output"))
		{
		      System.out.println(output);
		      System.exit(0);
		}
		else
		{			
			logger.trace(getCurrentMethodName() + " Entering application.");
			
			logger.debug(getCurrentMethodName() + " Creating file to read in stocks list (if it was passed in).");
			SystemFile f = new SystemFile(args[0]);
			
			logger.debug(getCurrentMethodName() + " Creating String for URL Options...");
			String URLOptions = null;
			logger.debug(getCurrentMethodName() + " String Created. Current Options: " + URLOptions);
			
			//SET THE ARGUMENTS FOR THE HTTP GET TO THE YAHOO API
			logger.debug(getCurrentMethodName() + " Setting the URL Options...");
			if(args[1].equals("default"))
			{				
				logger.debug(getCurrentMethodName() + " URL Options are default. Setting to: " + defaultURLOption );
				URLOptions = defaultURLOption;
				
			}
			else
			{
				logger.debug(getCurrentMethodName() + " URL Options are not default. Setting to : " + args[1] );
				URLOptions = args[1];
			}
			
			//A FILE WAS PASSED IN
			if (f.fileExists())
			{
				logger.debug(getCurrentMethodName() + " A list of Stocks via File was passed in. Creating Array to process a stock per thread." );
				@SuppressWarnings("unchecked")
				ArrayList A = new ArrayList(f.GetLines());
				for (int i = 0; i < A.size(); i++)
				{
					logger.debug(getCurrentMethodName() + " Starting Thread to processing stock : " + A.get(i) + " with URL options " + URLOptions);
					ThreadexecSvc.execute((Runnable) new AnalyzeStock((String) A.get(i), URLOptions));		
				}
				logger.debug(getCurrentMethodName() + " Shutting down threads..");

				ThreadexecSvc.shutdownNow();
				logger.debug(getCurrentMethodName() + " Threads shutdown.");
			}
			//A STOCK SYMBOL WAS PASSED IN
			else
			{
				logger.debug(getCurrentMethodName() + " A stock symbol " + args[0] + "  was passed in - Executing Thread to get information with URL Option " + URLOptions );
				ThreadexecSvc.execute((Runnable) new AnalyzeStock(args[0], URLOptions));
			}
			
			logger.trace(getCurrentMethodName() + " Exiting application.");
		}
	}
	
	public static boolean isNull(String str2) {
		logger.trace(getCurrentMethodName() + " Entering.");
		logger.trace(getCurrentMethodName() + " Exiting.");
        return str2 == null ? true : false;
    }
}

class AnalyzeStock implements Runnable
{
	/**
	 * @return the stockSymbol
	 */
	public String getStockSymbol() {
		logger.trace(getCurrentMethodName() + " Entering.");
		logger.trace(getCurrentMethodName() + " Exiting.");
		return StockSymbol;
	}

	/**
	 * @param stockSymbol the stockSymbol to set
	 */
	public void setStockSymbol(String stockSymbol) {
		logger.trace(getCurrentMethodName() + " Entering.");
		logger.trace(getCurrentMethodName() + " Exiting.");
		StockSymbol = stockSymbol;
	}

	/**
	 * @return the strURL
	 */
	public String getStrURL() {
		logger.trace(getCurrentMethodName() + " Entering.");
		logger.trace(getCurrentMethodName() + " Exiting.");
		return strURL;
	}

	/**
	 * @param strURL the strURL to set
	 */
	public void setStrURL(String strURL) {
		logger.trace(getCurrentMethodName() + " Entering.");
		this.strURL = strURL;
		logger.trace(getCurrentMethodName() + " Exiting.");
	}

	private String StockSymbol;
	private String strURL;
	
	static wget httpGetter = new wget();
	
	private static Logger logger = Logger.getLogger(Stockz.class.getName());
	
	AnalyzeStock(String Symbol, String URLStringOption)
	{
		logger.trace(getCurrentMethodName() + " Entering Constructor : AnalyzeStock");
		setStockSymbol(Symbol.toUpperCase());
		logger.debug(getCurrentMethodName() + " A stock symbol " + Symbol + "  set to upper case:  " + StockSymbol );
		setStrURL(URLStringOption);
		logger.debug(getCurrentMethodName() + " URL Options set  " + URLStringOption );
		logger.trace(getCurrentMethodName() + " Exiting Constructor : AnalyzeStock");
	}
	
	private static String getCurrentMethodName() 
 	{ 
 		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace(); 
 		
 		return 	stackTraceElements[1].toString().replaceFirst(stackTraceElements[1].toString().split("\\.")[0]+"\\.", "");
 	}
	
	public void run()
	{		
				
		logger.trace(getCurrentMethodName() + " Entering");
		try 
		{			
			UUID uuid = UUID.randomUUID();
			logger.debug(getCurrentMethodName() + " Generating Random UUID :" + uuid);

			String TemporaryStockFileName = getStockSymbol() + "-" + uuid + ".stk";
            
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Setting Temporary Stock Filname to : " + TemporaryStockFileName);
			String httpURL = "http://finance.yahoo.com/d/quotes.csv?s=" + getStockSymbol() + "&f=" + strURL;
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Final URL: " + httpURL);

			httpGetter.get(httpURL, TemporaryStockFileName);	
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Stock information recieved: " + getStockSymbol() + ":" +  uuid);

			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Creating and manipulating Stock Data to get values: " +  getStockSymbol() + ":" +  uuid);
			SystemFile file = new SystemFile(TemporaryStockFileName);
				
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Getting the first line of " + file.getFilename());
			String STOCK = file.ReturnFirstLine();
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Line of first file: " + STOCK);
	        			
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Deleting file: " + file.getFilename());
			file.DeleteFile();
			
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Removing all \" from line : " + STOCK);
			STOCK = STOCK.replaceAll("\"", "");
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Removing all spaces from line : " + STOCK);
			STOCK = STOCK.replaceAll(" ", "");
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Removing all N\\/A from line : " + STOCK);
			STOCK = STOCK.replaceAll("N\\/A", "0");
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Final Line : " + STOCK);

			
			logger.debug(getCurrentMethodName() + " Thread:" + uuid + " Finished. About to output the answer. " +  getStockSymbol() + ":" +  uuid);
			System.out.println(STOCK);
			logger.trace(getCurrentMethodName() + " Thread:" + uuid + " Exiting");
		} 
	
		catch (IOException e) 
		{
			System.out.println(" Could not reach the internet to get the stock prices for " + StockSymbol);
			logger.trace(getCurrentMethodName() + " Exiting");
		}
		
	}
}