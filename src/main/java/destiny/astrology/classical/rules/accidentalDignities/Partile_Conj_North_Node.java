/**
 * @author smallufo 
 * Created on 2007/12/29 at 下午 11:45:23
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** Partile conjunction with Dragon's Head (Moon's North Node). */
public final class Partile_Conj_North_Node extends Rule
{
  /** 內定採用 NodeType.MEAN */
  private NodeType nodeType = NodeType.MEAN;

  public Partile_Conj_North_Node()
  {
  }

  public NodeType getNodeType()
  {
    return nodeType;
  }

  public void setNodeType(NodeType nodeType)
  {
    this.nodeType = nodeType;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLng();
    double northDeg;
    if (nodeType == NodeType.TRUE)
      northDeg = horoscopeContext.getPosition(LunarNode.NORTH_TRUE).getLng();
    else
      northDeg = horoscopeContext.getPosition(LunarNode.NORTH_MEAN).getLng();
    if ( Horoscope2.getAngle(planetDegree , northDeg) <=1 )
    {
      if (nodeType == NodeType.TRUE)
      {
        //addComment(Locale.TAIWAN , planet + " 與 " + LunarNode.NORTH_TRUE + " 形成 " + Aspect.CONJUNCTION);
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, LunarNode.NORTH_TRUE, Aspect.CONJUNCTION}));
      }
      else
      {
        //addComment(Locale.TAIWAN , planet + " 與 " + LunarNode.NORTH_MEAN + " 形成 " + Aspect.CONJUNCTION);
        return Optional.of(Tuple.tuple("comment" , new Object[] {planet , LunarNode.NORTH_MEAN , Aspect.CONJUNCTION}));
      }
    }
    return Optional.empty();
  }

}
