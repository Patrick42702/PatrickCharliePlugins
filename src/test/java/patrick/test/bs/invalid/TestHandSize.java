package patrick.test.bs.invalid;


import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * Test hand size less than 2 invalid
 * @author Patrick Muller
 */
public class TestHandSize extends TestCase {

  /**
   * Runs the test
   */
  public void test() {
    Hand hand = new Hand(new Hid(Seat.YOU));

    // Test 1 card in hand
    hand.hit(new Card(4, Card.Suit.HEARTS));

    Card upCard = new Card(7, Card.Suit.CLUBS);

    BasicStrategy bs = new BasicStrategy();

    Play play = bs.getPlay(hand, upCard);

    assert play == Play.NONE;
  }
}
