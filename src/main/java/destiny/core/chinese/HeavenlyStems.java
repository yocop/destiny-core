package destiny.core.chinese;

import java.util.Arrays;
import java.util.List;

/** 天干系統 */
public enum HeavenlyStems implements Comparable<HeavenlyStems> , FiveElementIF , YinYangIF
{
  甲('甲'),
  乙('乙'),
  丙('丙'),
  丁('丁'),
  戊('戊'),
  己('己'),
  庚('庚'),
  辛('辛'),
  壬('壬'),
  癸('癸');
  
  private char Name;
  
  protected final static HeavenlyStems[] HeavenlyStemsArray =
    { 甲 , 乙 , 丙 , 丁 , 戊 ,
      己 , 庚 , 辛 , 壬 , 癸 };
  
  private final static List<HeavenlyStems> HeavenlyStemsList = Arrays.asList(HeavenlyStemsArray);

  private HeavenlyStems(char c)
  {
    this.Name = c;
  }
  
  /** 從五行 以及 陰陽 建立天干 */
  public final static HeavenlyStems getHeavenlyStems(FiveElement fiveElement , YinYang yinYang)
  {
    if (fiveElement == FiveElement.木)
    {
      if (yinYang == YinYang.陽)
        return 甲;
      else
        return 乙;
    }
    else if (fiveElement == FiveElement.火)
    {
      if (yinYang == YinYang.陽)
        return 丙;
      else
        return 丁;
    }
    else if (fiveElement == FiveElement.土)
    {
      if (yinYang == YinYang.陽)
        return 戊;
      else
        return 己;
    }
    else if (fiveElement == FiveElement.金)
    {
      if (yinYang == YinYang.陽)
        return 庚;
      else
        return 辛;
    }
    else 
    {
      if (yinYang == YinYang.陽)
        return 壬;
      else
        return 癸;
    }
  }
  
  /**
   * 抓取天干的 index , 為 0-based <BR>
   * 0 為 甲 <BR>
   * 1 為 乙 <BR>
   * ...     <BR>
   * 9 為 癸 <BR> 
   * @param index
   * @return
   */
  public static HeavenlyStems getHeavenlyStems(int index)
  {
    /**
     * 如果 index < 0  , 則 加 10 , recursive 再傳一次<BR>
     * 如果 index >=10 , 則 減 10 , recursive 再傳一次<BR> 
     */
    if (index < 0)
      return getHeavenlyStems(index+10);
    else if (index >=10 )
      return (getHeavenlyStems(index-10));
    return HeavenlyStemsArray[index];
  }
  
  public static HeavenlyStems getHeavenlyStems(char c)
  {
    HeavenlyStems result = null;
    for (int i=0 ; i < HeavenlyStemsArray.length ; i++)
    {
      if ( HeavenlyStemsArray[i].Name == c)
      {
        result = HeavenlyStemsArray[i];
        break;
      }
    }
    if (result != null)
      return result;
    else
      throw new RuntimeException("No such HeavenlyStems '"+c+"'");
  }
  
  /** 甲[0] ... 癸[9] */
  public static int getIndex(HeavenlyStems hs)
  {
    int result = HeavenlyStemsList.indexOf(hs);
    if (result != -1)
      return result;
    else
      throw new RuntimeException("Cannot find HeavenlyStems : " + hs + " in HeavenlyStems .");
  }
  
  /** 甲[0] ... 癸[9] */
  public int getIndex()
  {
    return getIndex(this);
  }
  
  /**
   * 實作 Comparable
   * */
  /*
  public int compareTo(Object o)
  {
    HeavenlyStems h = (HeavenlyStems) o;
    if ( getIndex(this) < getIndex(h) )
      return -1;
    else if ( getIndex(this) == getIndex(h) )
      return 0;
    else
      return 1;
  }
  */
  
  @Override 
  public String toString()
  {
    return String.valueOf(Name);
  }
  
  /**
   * 實作 FiveElementsIF 的 getFiveElements()
   */
  public FiveElement getFiveElement()
  {
    switch(getIndex(this))
    {
      case 0:      case 1:
        return FiveElement.木;
      case 2:      case 3:
        return FiveElement.火;
      case 4:      case 5:
        return FiveElement.土;
      case 6:      case 7:
        return FiveElement.金;
      case 8:      case 9:
        return FiveElement.水;
      default:
        throw new RuntimeException("HeavenlyStems Error : cannot getFiveElements() : " + toString());
    }
  }//getFiveElements()
  
  /**
  * 實作 YinYangIF
  */
  public YinYang getYinYang()
  {
    if (getIndex(this) % 2 == 0)
      return YinYang.陽;
    else
      return YinYang.陰;
  }
}
