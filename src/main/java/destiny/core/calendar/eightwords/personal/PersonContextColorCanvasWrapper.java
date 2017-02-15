/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.Time;
import destiny.core.calendar.TimeDecoratorChinese;
import destiny.core.calendar.eightwords.ContextColorCanvasWrapper;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.StemBranch;
import destiny.tools.ColorCanvas.AlignUtil;
import destiny.tools.ColorCanvas.ColorCanvas;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PersonContextColorCanvasWrapper extends ContextColorCanvasWrapper {

  /** 預先儲存已經計算好的結果 */
  private final PersonContextModel model;

  /** 地支藏干的實作，內定採用標準設定 */
  private HiddenStemsIF hiddenStemsImpl;

  private ColorCanvas cc;

  public enum OutputMode {HTML , TEXT}

  private OutputMode outputMode = OutputMode.HTML;

  private PersonContextModel.FortuneOutputFormat fortuneOutputFormat = PersonContextModel.FortuneOutputFormat.虛歲;

  private final Decorator<Time> timeDecorator = new TimeDecoratorChinese();

  private final Direction direction;

  public PersonContextColorCanvasWrapper(@NotNull PersonContextModel model,
                                         String locationName, HiddenStemsIF hiddenStemsImpl, String linkUrl , Direction direction) {
    super(model.getPersonContext(), model.getPersonContext().getLmt().toLocalDateTime() ,
      model.getPersonContext().getLocation() , locationName , hiddenStemsImpl , linkUrl, direction);
    this.model = model;
    this.hiddenStemsImpl = hiddenStemsImpl;
    this.direction = direction;
  }

  public void setOutputMode(OutputMode mode)
  {
    this.outputMode = mode;
  }

  /** 取得八字命盤 */
  @NotNull
  @Override
  public String toString()
  {
    PersonContext personContext = model.getPersonContext();

    cc = new ColorCanvas(32,70,"　");

    ColorCanvas metaDataColorCanvas = getMetaDataColorCanvas();

    cc.add(metaDataColorCanvas , 1 , 1); // 國曆 農曆 經度 緯度 短網址 等 MetaData

    cc.setText("性別：", 1, 59);
    cc.setText(personContext.getGender().toString() , 1, 65); // '男' or '女'
    cc.setText("性" , 1 , 67);

    cc.setText("八字：" , 10 , 1);

    EightWords eightWords = personContext.getEightWords();

    ReactionsUtil reactionsUtil = new ReactionsUtil(this.hiddenStemsImpl);

    cc.add(getEightWordsColorCanvas() , 11 , 9); // 純粹八字盤


    ColorCanvas 大運直 = new ColorCanvas(9,24,"　" );
    ColorCanvas 大運橫 = new ColorCanvas(8,70,"　" , Optional.empty() , Optional.empty());

    List<FortuneData> dataList = new ArrayList<>(model.getFortuneDatas());

    for (int i=1 ; i <= dataList.size() ; i++) {
      FortuneData fortuneData = dataList.get(i-1);
      int startFortune = fortuneData.getStartFortune();
      int   endFortune = fortuneData.getEndFortune();
      StemBranch stemBranch = fortuneData.getStemBranch();
      Time startFortuneLmt = fortuneData.getStartFortuneLmt();
      Time   endFortuneLmt = fortuneData.getEndFortuneLmt();

      大運直.setText(AlignUtil.alignRight(startFortune, 6) , i , 1 , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      大運直.setText("→" , i , 9 , "green" );
      大運直.setText(AlignUtil.alignRight(endFortune , 6) , i , 13 , "green" , null , "終運時刻：" + timeDecorator.getOutputString(endFortuneLmt));
      大運直.setText(stemBranch.toString() , i , 21 , "green");
    }


    if (direction == Direction.R2L) {
      Collections.reverse(dataList);
    }

    for (int i=1 ; i <= dataList.size() ; i++) {
      FortuneData fortuneData = dataList.get(i-1);
      int startFortune = fortuneData.getStartFortune();
      StemBranch stemBranch = fortuneData.getStemBranch();
      Time startFortuneLmt = fortuneData.getStartFortuneLmt();

      大運橫.setText(AlignUtil.alignCenter(startFortune , 6) , 1 , (i-1)*8+1 , "green" , null , "起運時刻：" + timeDecorator.getOutputString(startFortuneLmt));
      Reactions reaction = reactionsUtil.getReaction(stemBranch.getStem() , eightWords.getDay().getStem());
      大運橫.setText(reaction.toString().substring(0, 1), 2, (i-1)*8+3, "gray");
      大運橫.setText(reaction.toString().substring(1,2) , 3 , (i-1)*8+3 , "gray");
      大運橫.setText(stemBranch.getStem()  .toString() , 4 , (i-1)*8+3 , "red");
      大運橫.setText(stemBranch.getBranch().toString(), 5, (i-1)*8+3, "red");
      大運橫.add(地支藏干(stemBranch.getBranch() , eightWords.getDay().getStem()) , 6 , (i-1)*8+1);
    }

    cc.setText("大運（"+fortuneOutputFormat +"）", 10, 55);
    cc.add(大運直, 11, 47);
    cc.add(大運橫, 22, 1);

    ColorCanvas 節氣 = new ColorCanvas(2 , cc.getWidth() ,  "　");
    SolarTerms prevMajorSolarTerms = model.getPrevMajorSolarTerms();
    SolarTerms nextMajorSolarTerms = model.getNextMajorSolarTerms();


    Time prevMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(-1) );
    節氣.setText(prevMajorSolarTerms.toString() , 1 , 1);
    節氣.setText("：" , 1, 5);
    節氣.setText(this.timeDecorator.getOutputString(prevMajorSolarTermsTime) , 1,7);

    Time nextMajorSolarTermsTime = new Time(personContext.getLmt() , personContext.getTargetMajorSolarTermsSeconds(1) );
    節氣.setText(nextMajorSolarTerms.toString() , 2 , 1);
    節氣.setText("：" , 2, 5);
    節氣.setText(this.timeDecorator.getOutputString(nextMajorSolarTermsTime) , 2,7);

    cc.add(節氣 , 31 , 1);

    switch(this.outputMode)
    {
      case TEXT:
        return cc.toString();
      case HTML:
        return cc.getHtmlOutput();
      default:
        return cc.getHtmlOutput();
    }
  } // toString()

  /**
   * 設定大運輸出的格式
   */
  public void setFortuneOutputFormat(PersonContextModel.FortuneOutputFormat fortuneOutputFormat)
  {
    this.fortuneOutputFormat = fortuneOutputFormat;
  }
}
