package cc.setting;

import cc.core.ChineseCharacterTzu;
import cc.core.ChineseCharacterWen;
import cc.moveable_type.ChineseCharacterMovableType;
import cc.moveable_type.ChineseCharacterMovableTypeTzu;

/**
 * 活字設定工具。將部件結構（<code>ChineseCharacter</code>）轉換成活字結構（
 * <code>ChineseCharacterMovableType</code>）。
 * <p>
 * SimplePiece是兩兩配對後定框，再調部件大小，但無法物件距離難貼近或拉開。
 * 
 * @author Ihc
 */
public interface ChineseCharacterTypeSetter
{
	/**
	 * 產生並初使化獨體活字
	 * 
	 * @param parent
	 *            此活字結構的上層活字
	 * @param chineseCharacterWen
	 *            要轉化的文（獨體）部件
	 * @return 獨體活字
	 */
	public ChineseCharacterMovableType setWen(
			ChineseCharacterMovableTypeTzu parent,
			ChineseCharacterWen chineseCharacterWen);

	/**
	 * 產生並初使化合體活字
	 * 
	 * @param parent
	 *            此活字結構的上層活字
	 * @param chineseCharacterTzu
	 *            要轉化的字（合體）部件
	 * @return 合體活字
	 */
	public ChineseCharacterMovableType setTzu(
			ChineseCharacterMovableTypeTzu parent,
			ChineseCharacterTzu chineseCharacterTzu);
}
