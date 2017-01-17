package idsrend.services;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.slf4j.Logger;
import org.slf4j.profiler.Profiler;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import cc.movabletype.PieceMovableType;
import cc.movabletype.SeprateMovabletype;
import cc.ccomponent_adjuster.ExpSequenceNoLookup;
import cc.ccomponent_adjuster.ExpSequenceLookup;
import cc.char_indexingtool.ChineseCharacterTypeSetter;
import cc.char_indexingtool.FontRefSettingTool;
import cc.char_indexingtool.FontCorrespondTable;
import cc.char_indexingtool.CommonFontNoSearch;
import cc.char_indexingtool.NonLookingupCommonFontNo;
import cc.layouttools.MergePieceAdjuster;
import cc.log.IDSGenLogToolpack;
import cc.movabletype.ChineseCharCompositeMoveabletype;
import cc.printtools.AwtForSinglePiecePrinter;
import cc.stroke.FunctinoalBasicBolder;
import cc.stroke.NullStroke;
import cc.stroketool.MkeSeparateMovableType_Bolder;
import idsrend.CharComponentStructureAdjuster.IDSnormalizer;
import idsrend.CharComponentStructureAdjuster.TriElementsReplacer;
import idsrend.charcomponent.CharComponent;
import idsrend.parser.IDSExecption;
import idsrend.parser.IDSParser;

/**
 * 共組字規的系統整理起來，紲落來若欲用，就會當自由選擇欲用的工具。
 * 
 * @author Ihc
 */
public class IDSrendService
{
	/** 記錄程式狀況 */
	protected Logger 記錄工具;  //e 記錄工具 means 'recording tool'
	/** 看使用者提供的部件，是毋是愛先換做展開式抑是按怎 */
	protected ExpSequenceLookup 查詢方式; //e 查詢方式 means 'lookup method'
	/** 決定有需要正規化無佮按怎正規化的物件 */
	protected IDSnormalizer 正規化工具; //e 正規化工具 means IDS normalizer

	protected TriElementsReplacer 代換工具 = new TriElementsReplacer();
	/*e 代換工具 means 'substitution tool' */
	/** 依據部件佮字體的性質，共部件提來產生活字 */
	protected ChineseCharacterTypeSetter 設定工具;
	/*e 設定工具 means 'setting tool' */
	/** 佮頭拄仔產生的活字，組合閣共調整 */
	protected MergePieceAdjuster 調整工具; //e 調整工具 means 'tuning tool'
	protected MkeSeparateMovableType_Bolder 活字加粗;
	/*e 活字加粗 means 'turn movable type to bold' */
	/** 主要是控制字體是毋是粗體 */
	protected final int 字型屬性; //e 字型屬性 means 'font property'
	/** 字體愛偌大 */
	protected final int 字型大細; //e 字型大細 means 'font resolution'
	/** 限制組字式長度，予儂無法度惡意攻擊 */
	protected final int 組字式上大長度;
	/*e 組字式上大長度 means 'maximum length of IDS' */

	/**
	 * 建立一个組字介面，並決定所有屬性。
	 * 
	 * @param 查詢方式
	 *            看使用者提供的部件，是毋是愛先換做展開式抑是按怎
	 * @param 正規化工具
	 *            決定有需要正規化無佮按怎正規化的物件
	 * @param 異寫式查詢
	 *            異寫式查詢的方法
	 * @param 設定工具
	 *            依據部件佮字體的性質，共部件提來產生活字
	 * @param 調整工具
	 *            佮頭拄仔產生的活字，組合閣共調整
	 * @param 字型屬性
	 *            主要是控制字體是毋是粗體
	 * @param 字型大細
	 *            字體愛偌大
	 */
	public IDSrendService(ExpSequenceLookup 查詢方式, IDSnormalizer 正規化工具,
			ChineseCharacterTypeSetter 設定工具, MergePieceAdjuster 調整工具,
			MkeSeparateMovableType_Bolder 活字加粗, int 字型屬性, int 字型大細)
	/*e meaning:
         *  public IDSrendService(ExpSequenceLookup lookup_method, IDSnormalizer IDS_normalizer,
         *          ChineseCharacterTypeSetter setting_tool, MergePieceAdjuster tuning_tool,
         *          MkeSeparateMovableType_Bolder turn_movable_type_to_bold,
         *                                               int font_property, int font_resolution)
	 */
	{
		this(查詢方式, 正規化工具, 設定工具, 調整工具, 活字加粗, 字型屬性, 字型大細, 50);
	}

	/**
	 * 建立一个組字介面，並決定所有屬性。
	 * 
	 * @param 查詢方式
	 *            看使用者提供的部件，是毋是愛先換做展開式抑是按怎
	 * @param 正規化工具
	 *            決定有需要正規化無佮按怎正規化的物件
	 * @param 異寫式查詢
	 *            異寫式查詢的方法
	 * @param 設定工具
	 *            依據部件佮字體的性質，共部件提來產生活字
	 * @param 調整工具
	 *            佮頭拄仔產生的活字，組合閣共調整
	 * @param 字型屬性
	 *            主要是控制字體是毋是粗體
	 * @param 字型大細
	 *            字體愛偌大
	 * @param 組字式上大長度
	 *            限制組字式長度，予儂無法度惡意攻擊
         *
	 */
	public IDSrendService(ExpSequenceLookup 查詢方式, IDSnormalizer 正規化工具,
			ChineseCharacterTypeSetter 設定工具, MergePieceAdjuster 調整工具,
			MkeSeparateMovableType_Bolder 活字加粗, int 字型屬性, int 字型大細, int 組字式上大長度)
	/*e meaning:
         *  public IDSrendService(ExpSequenceLookup lookup_method, IDSnormalizer IDS_normalizer,
         *          ChineseCharacterTypeSetter setting_tool, MergePieceAdjuster tuning_tool,
         *          MkeSeparateMovableType_Bolder turn_movable_type_to_bold,
         *              int font_property, int font_resolution, int maximum_length_of_IDS)
	 */
	{
		this.查詢方式 = 查詢方式;
		this.正規化工具 = 正規化工具;
		this.設定工具 = 設定工具;
		this.調整工具 = 調整工具;
		this.活字加粗 = 活字加粗;
		this.字型屬性 = 字型屬性;
		this.字型大細 = 字型大細;
		this.組字式上大長度 = 組字式上大長度;

		記錄工具 = new IDSGenLogToolpack().記錄工具(getClass());
                     /*e 記錄工具 means 'recording tool' */
	}

	/**
	 * 組字而且畫出來。
	 * 
	 * @param 組字式
	 *            使用者要求的組字式
	 * @param 欲畫的所在
	 *            畫字體的所在
	 * @return 實際畫的組字式
         *
	 */
	public String 組字(String 組字式, Graphics 欲畫的所在)
	/*e meaning:
         *    public String compose_glyph(String IDS, Graphics on_where_to_render)
	 */
	{
		if (組字式.length() >= 組字式上大長度) //e 組字式上大長度 means 'maximum length of IDS'
			組字式 = 組字式.substring(0, 組字式上大長度);
		Profiler 看時工具 = new Profiler("組字 " + 組字式);
		/*e 看時工具 means profiler */
		看時工具.setLogger(記錄工具); //e 記錄工具 means 'recording tool'

		看時工具.start("初使化"); //e 初使化 means 'initialization'
		// 記錄工具.debug(MarkerFactory.getMarker("@@"),
		// "初使化～～ 時間：" + System.currentTimeMillis());

		看時工具.start("分析中"); //e 分析中 means 'analyzing'
		// 記錄工具.debug("分析中～～ 時間：" + System.currentTimeMillis());

		/*e
		 * 序列分析工具 means 'sequence analyzing tool'
		 * 組字式 means IDS, 查詢方式 means 'lookup method'
		 */
		IDSParser 序列分析工具 = new IDSParser(組字式, 查詢方式);
		CharComponent CharComponent;
		try
		{
			CharComponent = 序列分析工具.解析一個組字式();
		} //e 解析一個組字式 means 'parse an IDS'
		catch (IDSExecption e)
		{
			// TODO 看欲按怎處理，硬顯示，抑是傳連結毋著？
			e.printStackTrace();
			return "";
		}

		CharComponent 組字部件 = (CharComponent) CharComponent;
		//e 組字部件 means 'glyph component'
		// 組字部件.建立組字式(組字式建立工具);
		// 記錄工具.debug(組字部件.提到組字式());
		CharComponent = (CharComponent) 正規化工具.正規化(代換工具.三元素組合代換成二元素(CharComponent));
		/*e
		 * 正規化工具 means IDS normalizer, 正規化 means normalize,
		 * 代換工具 means 'substitution tool',
                 * 三元素組合代換成二元素 means 'substutute 3 element combination into 2 elements'
		 */
		組字部件.樹狀結構組字式(); //e 樹狀結構組字式 means 'tree structure IDS'
		// 記錄工具.debug(組字部件.提到組字式());

		看時工具.start("設定中"); //e 設定中 means 'setting up, 看時工具 means profiler'
		// 記錄工具.debug("設定中～～ 時間：" + System.currentTimeMillis());

		ChineseCharCompositeMoveabletype 活字 = CharComponent.typeset(設定工具, null);
		/*e 活字 means 'movable type', 設定工具 means 'setting tool' */

		看時工具.start("調整中"); //e 調整中 means 'tuning'
		// 記錄工具.debug("調整中～～ 時間：" + System.currentTimeMillis());

		活字.adjust(調整工具); //e 調整工具 means 'tuning tool'

		看時工具.start("四角中"); //e 四角中 means 'tuning movable type'
		SeprateMovabletype 上尾欲畫的圖 = 調整工具.format((PieceMovableType) 活字);
		/*e 上尾欲畫的圖 means 'picture to be drawn at last' */

		看時工具.start("加粗中"); //e 加粗中 means 'bolding'
		活字加粗.加粗(上尾欲畫的圖); //e 活字加粗 means 'turn movable type to bold'

		看時工具.start("列印中"); //e 加粗中 means 'printing'
		// 記錄工具.debug("列印中～～ 時間：" + System.currentTimeMillis());
		Graphics2D 字型圖版 = (Graphics2D) 欲畫的所在;
		/*e 字型圖版 means 'font plate', 欲畫的所在 means 'on where to render' */
		字型圖版.setColor(Color.black);
		字型圖版.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		字型圖版.translate(0, 字型大細 * 0.83);// TODO 閣愛研究按怎調整
		/*e 字型大細 means 'font resolution' */
		字型圖版.setStroke(new NullStroke());

		AwtForSinglePiecePrinter 列印工具 = new AwtForSinglePiecePrinter(字型圖版);
		/*e 列印工具 means 'printing tool' */

		列印工具.printPiece(上尾欲畫的圖);

		看時工具.stop().log();
		return 組字部件.樹狀結構組字式();
		/*e 組字部件 means 'glyph component', 樹狀結構組字式 means 'tree structure IDS' */
	}

	public static IDSrendService 預設組字服務()
	{
		return 預設組字服務(200);
	}

	public static IDSrendService 預設組字服務(int 字型大細)
	/*e meaning:
	 *  public static IDSrendService default_glyph_composing_service(int font_resolution)
	 */
	{
		int 普通字型屬性 = 0; //e 普通字型屬性 means 'normal font property'

		ExpSequenceLookup 查詢方式 = new ExpSequenceNoLookup();
		/*e 查詢方式 means lookup method */
		// ExpSequenceLookup_byDB(連線) ExpSequenceNoLookup()
		IDSnormalizer 正規化工具 = new IDSnormalizer();
		/*e 正規化工具 means IDS normalizer */
		// 異寫式查詢工具 異寫式查詢 = new 資料庫連線異寫式查詢(連線);
		MergePieceAdjuster 調整工具 = new MergePieceAdjuster(
		/*e 調整工具 means 'tuning tool' */
		// new FunctinoalBasicBolder(new Stroke[] {}, 01),
				1e-1, 5);
		CommonFontNoSearch 展開式查通用字型編號工具 = new NonLookingupCommonFontNo();
		// CommonFontNoSearchbyDB(連線) NonLookingupCommonFontNo()
		/*e
		 * 展開式查通用字型編號工具 means  IDSSquence_reverseLookupUnicodeNo_Tool
		 * 活字加粗 means 'turn movable type to bold'
		 * 宋體設定工具  means  Song font settingtool
		 * 提著吳守禮注音摻宋體字體() means  getWuShou-li_BoPoMoFofont_withSongFont()
		 * 調整字體參數() menas tunefontparameter()
		 */
		MkeSeparateMovableType_Bolder 活字加粗 = new MkeSeparateMovableType_Bolder(
				new FunctinoalBasicBolder(new Stroke[] {}, 01), 1e-1);
		ChineseCharacterTypeSetter 宋體設定工具 = new FontRefSettingTool(展開式查通用字型編號工具, FontCorrespondTable
				.提著吳守禮注音摻宋體字體().調整字體參數(普通字型屬性, 字型大細), new FontRenderContext(
				new AffineTransform(),
				java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
				java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT), 活字加粗);

		return new IDSrendService(查詢方式, 正規化工具, 宋體設定工具, 調整工具, 活字加粗, 普通字型屬性, 字型大細);
		/*e 查詢方式 means lookup method */
	}

	public void 字組成png(String 組字式, OutputStream 輸出檔案) throws IOException
	/*e meaning:  compose_glyph_into_png(String IDS, OutputStream output_file)... */
	{
		BufferedImage 字型圖片 = //e 字型圖片 means 'glyph image'
		// 系統圖畫設定.createCompatibleImage(字型大細,
		// 字型大細, Transparency.TRANSLUCENT);
		new BufferedImage(字型大細, 字型大細, BufferedImage.TYPE_INT_ARGB);
		組字(組字式, 字型圖片.getGraphics());
		ImageIO.write(字型圖片, "png", 輸出檔案);
		return;
	}

	public void 字組成svg(String 組字式, OutputStream 輸出檔案)
	/*e meaning:  compose_glyph_into_svg(String IDS, OutputStream output_file) */
			throws UnsupportedEncodingException, SVGGraphics2DIOException
	{
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		boolean useCSS = true; // we want to use CSS style
								// attributes
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.setSVGCanvasSize(new Dimension(字型大細, 字型大細));
		組字(組字式, svgGenerator);
		/*e meaning:  compose_glyph(IDS,svgGenerator); */
		OutputStreamWriter svgOutput = new java.io.OutputStreamWriter(輸出檔案,
				"UTF-8");
		svgGenerator.stream(svgOutput, useCSS);
		return;
	}
}
