/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.Gender;
import destiny.core.chinese.*;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/** 起大限 */
public interface IBigRange extends Descriptive {

  Logger logger = LoggerFactory.getLogger(IBigRange.class);

  Tuple2<Double , Double> getRange(House house, int set, YinYangIF yinYang, Gender gender, FortuneOutput fortuneOutput, IHouseSeq houseSeqImpl);

  /** 計算每個地支 的 大限 起訖 時刻 */
  default Map<Branch , Tuple2<Double , Double>> getFlowBigMap(Map<Branch , House> branchHouseMap , int set , StemBranch birthYear , Gender gender , FortuneOutput fortuneOutput , IHouseSeq houseSeq) {
    return Arrays.stream(Branch.values())
      .map(branch -> {
        Tuple2<Double , Double> t2 = getRange(branchHouseMap.get(branch), set, birthYear.getStem(), gender, fortuneOutput , houseSeq);
        return Tuple.tuple(branch , t2);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
  }

  /** 承上 , 計算每個地支的 大限 起訖 時刻，並且按照先後順序排列 (年齡 小 -> 大) */
  default Map<StemBranch , Tuple2<Double , Double>> getSortedFlowBigMap(Map<Branch , House> branchHouseMap , int set , StemBranch birthYear , Gender gender , FortuneOutput fortuneOutput , IHouseSeq houseSeq) {
    Map<Branch , Tuple2<Double , Double>> map = getFlowBigMap(branchHouseMap , set , birthYear, gender , fortuneOutput , houseSeq);
    logger.debug("[unsorted] map = {}" , map);

    Stem stemOf寅 = IZiwei.getStemOf寅(birthYear.getStem());

    return map.entrySet().stream()
      .map(e -> {
        StemBranch sb = IZiwei.getStemBranchOf(e.getKey() , stemOf寅);
        return new AbstractMap.SimpleEntry<>(sb, e.getValue());
      })
      .sorted(Map.Entry.comparingByValue())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  @Override
  default String getTitle(Locale locale) {
    try {
      return ResourceBundle.getBundle(IBigRange.class.getName(), locale).getString(getClass().getSimpleName());
    } catch (MissingResourceException e) {
      return getClass().getSimpleName();
    }
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }
}