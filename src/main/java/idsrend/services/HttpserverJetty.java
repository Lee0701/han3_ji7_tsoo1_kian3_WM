package idsrend.services;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * 利用Jetty來做主機端，提供服務予儂組字。
 *
 * @author Ihc
 */
/*e
 * I use Jetty as the server side, to provide the service for rendering Unicode Ideographic Description Sequence.
 *
 * @author Ihc
 */
public class HttpserverJetty
{
	/**
	 * 網路監聽埠口
	 */
	/*e
	 * internet monitor port
	 */
	static final int 連接埠 = 8060; //e 連接埠 means connection port

	/**
	 * 主要執行的函式。
	 *
	 * @param 參數
	 *            無使用著。
	 */
	/*e
	 * Functions that execute the most
	 *
	 * @param parameter
	 *            Not used.
	 */
	public static void main(String[] 參數) //e 參數 means parameter
	{
		Server server = new Server(8060);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(IDSrendServlet.class, "/*");
		boolean 遏袂啟動 = true; //e 遏袂啟動 means Not activated yet
		while (遏袂啟動) //e 遏袂啟動 means Not activated yet
		{
			try
			{
				server.start();
				遏袂啟動 = false; //e 遏袂啟動 means Not activated yet
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
		System.out.println("服務啟動～～"); //e 服務啟動～～ means Service activate~~
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
