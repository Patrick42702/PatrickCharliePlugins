package patrick.test.bs.invalid;


import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * This tests that a null card is invalid for a play
 * @author Patrick Muller
 */
public class TestNullHand extends TestCase {

  /**
   * Runs the test
   */
  public void test() {
    Hand hand = null;

    Card upCard = new Card(2, Card.Suit.CLUBS);

    BasicStrategy bs = new BasicStrategy();

    Play play = bs.getPlay(hand, upCard);

    assert play == Play.NONE;
  }
}

