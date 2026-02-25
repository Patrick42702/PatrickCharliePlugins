package patrick.test.bs.section1;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * Tests my 12 vs dealer 2 which should be HIT.
 * @author Patrick Muller
 */
public class Test_12_2 extends TestCase {
  /**
   * Runs the test.
   */
  public void test() {
    // Hand needs a hid which we can generate with a seat.
    Hand myHand = new Hand(new Hid(Seat.YOU));

    // Put two cards in the hand, only rank matters, not suit.
    myHand.hit(new Card(4, Card.Suit.CLUBS));
    myHand.hit(new Card(8, Card.Suit.DIAMONDS));

    // Again, only up-card rank matters, not suit.
    Card upCard = new Card(2, Card.Suit.HEARTS);

    BasicStrategy strategy = new BasicStrategy();

    // Play should match the basic strategy.
    Play play = strategy.getPlay(myHand, upCard);

    // This throws an exception if play is not the expected Play.
    assert play == Play.HIT;
  }
}
