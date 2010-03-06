package destiny.core.calendar.eightwords;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;


/**
 * <PRE>
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006/06/12 將此 class 繼承 EightWordsNullable
 * </PRE>
 * @author smallufo
 * @date 2002/8/25
 * @time 下午 11:22:48
 */
public class EightWords extends EightWordsNullable 
{

  /** Constructor , 任何一柱都不可以為 null */
  public EightWords( StemBranch year , StemBranch month , StemBranch day , StemBranch hour)
  {
    if (year == null || month == null || day == null || hour == null)
      throw new RuntimeException("year / month / day / hour cannot be null !");
    this.year  = year;
    this.month = month;
    this.day   = day;
    this.hour  = hour;
  }
  
  /**
   * 以 "甲子","甲子","甲子","甲子" 方式 construct 此物件 , 任何一柱都不可以為 null
   */
  public EightWords( String year , String month , String day , String hour)
  {
    if (year == null || month == null || day == null || hour == null)
      throw new RuntimeException("year / month / day / hour cannot be null !");
    char y1 = year.toCharArray()[0];
    char y2 = year.toCharArray()[1];
    char m1 = month.toCharArray()[0];
    char m2 = month.toCharArray()[1];
    char d1 = day.toCharArray()[0];
    char d2 = day.toCharArray()[1];
    char h1 = hour.toCharArray()[0];
    char h2 = hour.toCharArray()[1];
    
    this.year  = StemBranch.get(y1 , y2);
    this.month = StemBranch.get(m1 , m2);
    this.day   = StemBranch.get(d1 , d2);
    this.hour  = StemBranch.get(h1 , h2);
  }
  
  /** 從 EightWordsNullabel 建立 EightWords , 其中 EightWordsNullabel 任何一柱都不可以為 null , 否則會出現 RuntimeException */
  public EightWords(EightWordsNullable nullable)
  {
    if (nullable.getYear()==null || nullable.getMonth()==null || nullable.getDay()==null || nullable.getHour()==null)
      throw new RuntimeException("year / month / day / hour cannot be null !");
    this.year  = nullable.getYear();
    this.month = nullable.getMonth();
    this.day   = nullable.getDay();
    this.hour  = nullable.getHour();
  }
  
  /**
   * null Constructor , 避免誤用而建構出有 null 的四柱
   */
  @SuppressWarnings("unused")
  private EightWords()
  {}
  
  @Override public HeavenlyStems getYearStem()  { return year.getStem();  }
  @Override public HeavenlyStems getMonthStem() { return month.getStem(); }
  @Override public HeavenlyStems getDayStem()   { return day.getStem();   }
  @Override public HeavenlyStems getHourStem()  { return hour.getStem();  }
  
  @Override public EarthlyBranches getYearBranch()  { return year.getBranch();  }
  @Override public EarthlyBranches getMonthBranch() { return month.getBranch(); }
  @Override public EarthlyBranches getDayBranch()   { return day.getBranch();   }
  @Override public EarthlyBranches getHourBranch()  { return hour.getBranch();  }
  
  @Override
  public boolean equals(Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
      EightWords ew = (EightWords) o;
      return (year.equals(ew.year) && month.equals(ew.month) && day.equals(ew.day) && hour.equals(ew.hour));
    }
    else return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 13 ;
    hash = hash * 17 + year.hashCode();
    hash = hash * 17 + month.hashCode();
    hash = hash * 17 + day.hashCode();
    hash = hash * 17 + hour.hashCode();
    return hash;
  }
  
  @Override
  public String toString()
  {
    return 
      "\n"+
      hour.getStem()   + day.getStem()   + month.getStem()   + year.getStem() + "\n" +
      hour.getBranch() + day.getBranch() + month.getBranch() + year.getBranch() ;
  }
  
  @Override
  /** 設定年干支 , 不可為 null */
  public void setYear (StemBranch branch) 
  { 
    if (branch == null)
      throw new RuntimeException("StemBranch of year cannot be null !");
    year  = branch; 
  }
  
  @Override
  /** 設定月干支 , 不可為 null */
  public void setMonth(StemBranch branch) 
  { 
    if (branch == null)
      throw new RuntimeException("StemBranch of month cannot be null !");
    month = branch; 
  }
  
  @Override
  /** 設定日干支 , 不可為 null */
  public void setDay  (StemBranch branch) 
  { 
    if (branch == null)
      throw new RuntimeException("StemBranch of day cannot be null !");
    day   = branch; 
  }
  
  @Override
  /** 設定時干支 , 不可為 null */
  public void setHour (StemBranch branch) 
  { 
    if (branch == null)
      throw new RuntimeException("StemBranch of hour cannot be null !");
    hour  = branch; 
  }
  
  /** 以字串的 ("甲子") 來設定年干支 */
  public void setYear (String value) { year  = StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定月干支 */
  public void setMonth(String value) { month = StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定日干支 */
  public void setDay  (String value) { day   = StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定時干支 */
  public void setHour (String value) { hour  = StemBranch.get(value); }

}
