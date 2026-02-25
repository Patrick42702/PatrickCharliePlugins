package patrick.test.shoe;

import charlie.card.Card;
import charlie.plugin.IShoe;
import junit.framework.TestCase;
import patrick.plugin.MyShoe02;

/**
 * This class unit tests MyShoe02 ensuring
 * there are 10 cards and the first 2 cards
 * match correctly after calling the init method
 * @author Patrick Muller
 */
public class MyShoe02Test extends TestCase {

  /**
   * Run test
   */
  public void test(){
    IShoe shoe = new MyShoe02();

    // must init the shoe before testing
    shoe.init();

    // test 10 cards loaded in shoe
    assert shoe.size() == 10;

    // test first card is a 2
    Card card1 = shoe.next();
    assert card1.getRank() == 2;

    // test second card is a queen
    Card card2 = shoe.next();
    assert card2.getRank() == Card.QUEEN;
  }
}
