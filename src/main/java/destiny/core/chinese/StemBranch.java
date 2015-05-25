package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
public class StemBranch extends StemBranchNullable implements Comparable<StemBranch> , Serializable
{
//  @Nullable
//  private final HeavenlyStems   stem;   //天干
//
//  @Nullable
//  private final EarthlyBranches branch; //地支
  
  // 0[甲子] ~ 59[癸亥]
  @NotNull
  private transient static StemBranch[] stemBranchArray = new StemBranch[60];
  static
  {
    int n=0;
    do
    {
      stemBranchArray[n]= new StemBranch (Stem.getHeavenlyStems(n % 10) ,
                                          Branch.getEarthlyBranches(n % 12) );
      n++;
    }
    while(n<60);
  }
  
  private StemBranch(@NotNull Stem 天干 , @NotNull Branch 地支)
  {
    super(天干 , 地支);
  }
  
  /*
  public StemBranch(char 天干 , char 地支) 
  {
    this.stem = HeavenlyStems.getHeavenlyStems(天干);
    this.branch = EarthlyBranches.getEarthlyBranches(地支);

    if ( (HeavenlyStems.getIndex(this.stem) % 2 )  != (EarthlyBranches.getIndex(this.branch) %2 ) )
          throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch );    
  }
  */
  
  /**
   * @param index 0[甲子] ~ 59[癸亥]
   */
  public static StemBranch get(int index)
  {
    return stemBranchArray[normalize(index)];
  }

  /**
   * 取得下 n 組干支組合
   * n = 0 : 傳回自己
   */
  public StemBranch next(int n) {
    return get(getIndex(this) + n);
  }

  /**
   * 取得前 n 組干支組合
   * n = 0 : 傳回自己
   */
  public StemBranch prev(int n) {
    return next(0-n);
  }

  /** 取得下一組干支 , 甲子 傳回 乙丑 */
  public StemBranch getNext() {
    return next(1);
  }

  /** 取得上一組干支 , 甲子 傳回 癸亥 */
  public StemBranch getPrevious() {
    return prev(1);
  }

  @NotNull
  public static StemBranch get(@NotNull Stem 天干 , @NotNull Branch 地支)
  {
    if ( (Stem.getIndex(天干) % 2 )  != (Branch.getIndex(地支) %2 ) )
        throw new RuntimeException("Stem/Branch combination illegal ! " + 天干 + " cannot be combined with " + 地支 );

    int hIndex = Stem.getIndex(天干);
    int eIndex = Branch.getIndex(地支);
    switch (hIndex - eIndex) {
      case 0:
      case -10:
        return get(eIndex);
      case 2:
      case -8:
        return get(eIndex + 12);
      case 4:
      case -6:
        return get(eIndex + 24);
      case 6:
      case -4:
        return get(eIndex + 36);
      case 8:
      case -2:
        return get(eIndex + 48);
      default:
        throw new RuntimeException("Invalid 天干/地支 Combination!");
    }
  }
  
  @Nullable
  public static StemBranch get(char heavenlyStems , char earthlyBranches)
  {
    return get(Stem.getHeavenlyStems(heavenlyStems).get() , Branch.getEarthlyBranches(earthlyBranches).get());
  }
  
  public static StemBranch get(@NotNull String stemBranch)
  {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0) , stemBranch.charAt(1));
  }
  
  private static int normalize(int index)
  {
    if (index >= 60)
      return (normalize(index-60));
    else if (index < 0)
      return (normalize(index+60));
    else
      return index;
  }
  
  /** 取得干支的差距，例如 "乙丑" 距離 "甲子" 的差距為 "1" , 通常是用於計算「需歲」 (尚需加一) */
  public int differs(@NotNull StemBranch sb)
  {
    return getIndex(this) - sb.getIndex();
  }
  
  public boolean equals(@Nullable Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
      StemBranch sb = (StemBranch) o;
      return (this.stem == sb.stem && this.branch == sb.branch );
    }
    else return false;
  }//equals()

  public int hashCode()
  {
    int stemCode   = (stem == null ? 0 : stem.hashCode() );
    int branchCode = (branch == null ? 0 : branch.hashCode() );
    int hash = 7;
    hash = hash * 11 + stemCode;
    hash = hash * 11 + branchCode;
    return hash;
  }
  
  /**
   * 0[甲子] ~ 59[癸亥]
   * @param sb 取得某組干支的順序
   * @return 0[甲子] ~ 59[癸亥]
   */
  private static int getIndex(@NotNull StemBranch sb)
  {
    int index=-1;
    for (int i = 0 ; i < stemBranchArray.length ; i ++)
    {
      if (sb.equals(stemBranchArray[i]) )
        index = i;
    }
    return index;
  }//getIndex()




  /**
   * @return 0[甲子] ~ 59[癸亥]
   */
  public int getIndex()
  {
    return getIndex(this);
  }

  public static Iterator<StemBranch> iterator() {
    return Arrays.stream(stemBranchArray).iterator();
  }
  
  @NotNull
  public String toString()
  {
    return stem.toString()+branch.toString();
  }
   /**
   * 實作 Comparable 的 compareTo()
   */
  public int compareTo(StemBranch o)
  {
    return (getIndex(this) - getIndex(o));
    
    /**
    StemBranch sb = (StemBranch)o;
    if ( getIndex(this) < StemBranch.getIndex(sb) )
      return -1;
    else if ( getIndex(this) == StemBranch.getIndex(sb))
      return 0;
    else
      return 1;
    */
  }//compareTo()
 
  /**
   * @return 天干
   */
  @NotNull
  public Stem getStem()
  {
    return stem;
  }
  
  /**
   * @return 地支
   */
  @NotNull
  public Branch getBranch()
  {
    return branch;
  }



  public static Iterable<StemBranch> iterable() {
    return Arrays.asList(stemBranchArray);
  }
}
