package idsrend.services;

import java.awt.Font;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import cc.ccomponent_adjuster.ExpSequenceLookup;
import cc.ccomponent_adjuster.ExpSequenceLookup_byDB;
import cc.char_indexingtool.ChineseCharacterTypeSetter;
import cc.char_indexingtool.FontRefSettingTool;
import cc.layouttools.MergePieceAdjuster;
import cc.char_indexingtool.FontCorrespondTable;
import cc.char_indexingtool.CommonFontNoSearch;
import cc.char_indexingtool.CommonFontNoSearchbyDB;
import cc.stroke.FunctinoalBasicBolder;
import cc.stroketool.MkeSeparateMovableType_Bolder;
import cc.tool.database.PgsqlConnection;
import idsrend.CharComponentStructureAdjuster.IDSnormalizer;

import java.lang.System;

/**
 * 佮愛規的服務佮提供的字體攏總整合起來。
 *
 * @author Ihc
 */
public class IDSrendServlet extends HttpServlet
{
	/** 序列化編號 */
	private static final long serialVersionUID = 1224634082415129183L;
	/** 組宋體用的工具 / The Tool for compositing Song font .	*/
	protected IDSrendService 宋體組字工具;
	/** 組宋體粗體用的工具 / The Tool for compositing bold Song font . */
	protected IDSrendService 粗宋組字工具;
	/** 組楷體用的工具 / The Tool for compositing Kai font .*/
	protected IDSrendService 楷體組字工具;
	/** 組楷體粗體用的工具 / The Tool for compositing Kai bold font .*/
	protected IDSrendService 粗楷組字工具;

	/** 產生圖形傳予組字介面畫。毋過無X11、圖形介面就袂使用 */
	// GraphicsConfiguration 系統圖畫設定;
	/** 佮資料庫的連線 */
	protected PgsqlConnection 連線;//連線 means connetcion


	/** 組字出來的字型解析度 */
	int 字型大細; // 字型大細 means font resolution

	/** 建立一个組字的服務。 */
	public IDSrendServlet()
	{
		
		// 系統圖畫設定 = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getDefaultScreenDevice().getDefaultConfiguration();

		連線 = new PgsqlConnection();//連線 means connection
		// TODO 換專門查的使用者，換讀取權限
		
		ExpSequenceLookup 查詢方式 = new ExpSequenceLookup_byDB(連線);//查詢方式 means loopup method , 連線 means connection
		// TODO ExpSequenceLookup_byDB(連線) ExpSequenceNoLookup()
		IDSnormalizer 正規化工具 = new IDSnormalizer();//正規化工具= IDS normalizer
		MergePieceAdjuster 調整工具 = new MergePieceAdjuster( //調整工具 means tunning tool.
		// new FunctinoalBasicBolder(new Stroke[] {}, 01),
				1e-1, 5);
		CommonFontNoSearch 展開式查通用字型編號工具 = new CommonFontNoSearchbyDB(連線);/*展開式查通用字型編號工具 menas using IDS  to reversely lookup Han characters which already own codepoint in Unicode. */
		// TODO CommonFontNoSearchbyDB(連線) NonLookingupCommonFontNo()
		
		int 粗字型屬性 = Font.BOLD;//粗字型屬性 = bold font type property
		int 普通字型屬性 = 0;//普通字型屬性 = normal font type property
		字型大細 = 200;//字型大細 = font resolution
		MkeSeparateMovableType_Bolder 活字加粗 = new MkeSeparateMovableType_Bolder(//活字加粗 = Make a sparate movable type bolder.
				new FunctinoalBasicBolder(new Stroke[] {}, 01), 1e-1);
		ChineseCharacterTypeSetter 宋體設定工具 = new FontRefSettingTool(展開式查通用字型編號工具, FontCorrespondTable
				.提著吳守禮注音摻宋體字體().調整字體參數(普通字型屬性, 字型大細), new FontRenderContext(
				new AffineTransform(),
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
				java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT), 活字加粗);
		/*
		  "ChineseCharacterTypeSetter 宋體設定工具 =...."
		  宋體設定工具  means  Song font settingtool 
		  展開式查通用字型編號工具 means  IDSSquence_reverseLookupUnicodeNo_Tool
		  提著吳守禮注音摻宋體字體() means  getWuShou-li_BoPoMoFofont_withSongFont()
		  調整字體參數() menas tunefontparameter()
		 */

		宋體組字工具 = new IDSrendService(查詢方式, 正規化工具, 宋體設定工具, 調整工具, 活字加粗, 普通字型屬性, 字型大細);
		/* "宋體組字工具 = new......"
		   means:		   
		   SongFontCompositeTool = new IDSrendService(lookup_method, normalize_tool, SongFontsetting_tool, tunetool, movable_type_bolder, normal_font_parameter, font_resolution);
		*/

		ChineseCharacterTypeSetter 粗宋設定工具 = new FontRefSettingTool(展開式查通用字型編號工具, FontCorrespondTable
				.提著吳守禮注音摻宋體字體().調整字體參數(粗字型屬性, 字型大細), new FontRenderContext(
				new AffineTransform(),
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
				java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT), 活字加粗);

		/*
		  粗宋設定工具 means bold Song font setting tool.
		  粗字型屬性 means bold font parameter
		  others are similar to SongFontSettingTool
		*/


		粗宋組字工具 = new IDSrendService(查詢方式, 正規化工具, 粗宋設定工具, 調整工具, 活字加粗, 粗字型屬性, 字型大細);
		//粗宋組字工具 means bold Song font CompositeTool, others similar to  SongFontCompositeTool.
		

		ChineseCharacterTypeSetter 楷體設定工具 = new FontRefSettingTool(展開式查通用字型編號工具, FontCorrespondTable
				.提著吳守禮注音摻楷體字體().調整字體參數(普通字型屬性, 字型大細), new FontRenderContext(
				new AffineTransform(),
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
				java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT), 活字加粗);
		/* 楷體設定工具 means Kai font setting tool.
		   others are similar to SongFontSettingTool
		 */

		楷體組字工具 = new IDSrendService(查詢方式, 正規化工具, 楷體設定工具, 調整工具, 活字加粗, 普通字型屬性, 字型大細);
		//楷體組字工具 means Kai font compositetool, others similar to  SongFontCompositeTool.

		ChineseCharacterTypeSetter 粗楷設定工具 = new FontRefSettingTool(展開式查通用字型編號工具, FontCorrespondTable
				.提著吳守禮注音摻楷體字體().調整字體參數(粗字型屬性, 字型大細), new FontRenderContext(
				new AffineTransform(),
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
				java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT), 活字加粗);
		//粗楷設定工具 means bold Kai font  setting tool. others are similar to bold Song font setting tool.

		粗楷組字工具 = new IDSrendService(查詢方式, 正規化工具, 粗楷設定工具, 調整工具, 活字加粗, 粗字型屬性, 字型大細);
		//粗楷組字工具 means bold Kai font compositetool, others similar to  SongFontCompositeTool.
	}

	@Override
	/**
	 * 會依照使用的目錄，決定用的字體佮檔案類型。
	 * 用法：<code>/字體/組字式.檔案類型</code>
	 * 這馬干焦提供：<code>宋體</code>、<code>宋體粗體</code>、<code>楷體</code>、<code>楷體粗體</code>
	 * 檔案類型：<code>png</code>
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		response.setHeader("Cache-Control", "public, max-age=31536000");
		response.setHeader("Server", "han3_ji7_tsoo1_kian3");
		
		//System.out.println("PathInfo="+request.getPathInfo());
		String 網址字串 = URLDecoder.decode(request.getPathInfo(), "UTF-8")//在通用的應用程式用request.getRequestURI()會取到servlet path本身
		    .substring(1);//網址字串 means url string,
		if (是舊網址(網址字串))// TODO ==字體 , "是舊網址" means isOldURL. 
		{
			String[] 目錄 = 網址字串.split("/", 2);
			String 新網址 = String.format("/%s?%s=%s",
						      URLEncoder.encode(目錄[1], "UTF-8"),//新網址 means new URL,目錄 means catalog.
						      URLEncoder.encode("字體", "UTF-8"),//字體 means font.
						      URLEncoder.encode(目錄[0], "UTF-8"));//字體= font

			System.out.println(網址字串);
			System.out.println(目錄[0]);
			System.out.println(目錄[1]);
			System.out.println(新網址);
			System.out.println("XXXDDDDD---------------------");
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			// response.setHeader("Location", response.encodeRedirectURL(新網址));
			response.setHeader("Location", 新網址);
			// response.sendRedirect(String.format("/%s?字體=%s", 目錄[2], 目錄[1]));
			return;
		}
		IDSrendService 組字工具 = 宋體組字工具;// 組字工具 means composite tool, 宋體組字工具 means Song font composite tool.
		//System.out.println("網址字串="+網址字串);
		//System.out.println("輸入的uri="+request.getRequestURI());	
		//System.out.println("字體是"+request.getParameter("字體"));
		String 字體 = request.getParameter("字體");//字體 means font
		switch (字體 != null ? 字體 : "")
		{
		case "宋體"://Song Font
			組字工具 = 宋體組字工具;
			break;
		case "宋體粗體"://Bold Song Font
			組字工具 = 粗宋組字工具;
			break;
		case "楷體"://Kai font
			組字工具 = 楷體組字工具;
			break;
		case "楷體粗體"://Bold Kai font
			組字工具 = 粗楷組字工具;
			break;
		default:
			組字工具 = 宋體組字工具;
			break;
		}

		int 位置 = 網址字串.lastIndexOf('.');//位置 means position
		if (位置 == -1)
			位置 = 網址字串.length();
		
		String 組字式 = 網址字串.substring(0, 位置); //組字式 means IDS ,網址字串 = url string , 位置= position
		System.out.println("組字式="+組字式);
		
		String 附檔名 = 網址字串.substring(位置 + 1);//附檔名 means Filename extension  
		if (附檔名.equals("svg"))
		{
			response.setHeader("Content-Type", "image/svg+xml;charset=utf-8");
			組字工具.字組成svg(組字式, response.getOutputStream()); //組字工具 means  composite font tool, 字組成svg means glyph composited svg,組字式=IDS
		}
		else // TODO 只支援png、svg，其他先用png / Only support png and svg now,so 'else' means png.
		{
			response.setHeader("Content-Type", "image/png");
			組字工具.字組成png(組字式, response.getOutputStream());
		}
	}

    private boolean 是舊網址(String 網址字串) //是舊網址 = isOldUrl , 網址字串=url string.
	{
	    String[] 目錄 = 網址字串.split("/");// 目錄= folder
		if (目錄.length == 2)// TODO ==字體
		{
			switch (目錄[0])
			{
			case "宋體"://Song font
			case "宋體粗體"://Bold Song font
			case "楷體"://Kai font
			case "楷體粗體"://Bold Kai font
				return true;
			default:
				return false;
			}
		}
		return false;
	}
}
