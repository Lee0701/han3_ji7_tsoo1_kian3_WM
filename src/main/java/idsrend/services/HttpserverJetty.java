package idsrend.services;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * 利用Jetty來做主機端，提供服務予儂組字。
 *
 * @author Ihc
 */
/**
 * I use Jetty as the mainframe end, to provide the service for ideograph sequencing.
 *
 * @author Ihc
 */
public class HttpserverJetty
{
	/** 網路監聽埠口 */
	/** internet monitor port */	
	static final int 連接埠 = 8060;//連接埠 means connection port

	/**
	 * 主要執行的函式。
	 *
	 * @param 參數
	 *            無使用著。
	 */
	/**
	 * Functions that execute the most
	 *
	 * @param parameter
	 *            Not used.
	 */
	public static void main(String[] 參數)//參數 means parameter
	{
		Server server = new Server(8060);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(IDSrendServlet.class, "/*");
		boolean 遏袂啟動 = true;//遏袂啟動 means Not activated yet
		while (遏袂啟動)//遏袂啟動 means Not activated yet
		{
			try
			{
				server.start();
				遏袂啟動 = false;//遏袂啟動 means Not activated yet
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e1)
				{
				}
			}
		}
		System.out.println("服務啟動～～");//服務啟動～～ means Service activate
		try
		{
			server.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
