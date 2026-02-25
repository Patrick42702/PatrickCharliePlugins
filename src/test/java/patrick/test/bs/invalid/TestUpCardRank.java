package patrick.test.bs.invalid;


import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * This tests that a card's rank must be between
 * 1-13 to be valid
 * @author Patrick Muller
 */
public class TestUpCardRank extends TestCase {

  /**
   * Runs the test
   */
  public void test() {
    Hand hand = new Hand(new Hid(Seat.YOU));
    // set normal hand
    hand.hit(new Card(2, Card.Suit.HEARTS));
    hand.hit(new Card(11, Card.Suit.HEARTS));

    // 14 is not a valid rank
    Card upCard = new Card(14, Card.Suit.DIAMONDS);

    BasicStrategy bs = new BasicStrategy();

    Play play = bs.getPlay(hand, upCard);

    assert play == Play.NONE;
  }
}
