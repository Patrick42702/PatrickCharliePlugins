package patrick.test.bs.invalid;


import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * This tests that a card with an invalid rank
 * in the hand is invalid
 * @author Patrick Muller
 */
public class TestHandCardRank extends TestCase {

  /**
   * Runs the test
   */
  public void test() {
    Hand hand = new Hand(new Hid(Seat.YOU));

    // Test if it picks up the invalid ranked card, 12
    hand.hit(new Card(2, Card.Suit.CLUBS));
    hand.hit(new Card(5, Card.Suit.CLUBS));
    hand.hit(new Card(12, Card.Suit.CLUBS));

    Card upCard = new Card(7, Card.Suit.CLUBS);

    BasicStrategy bs = new BasicStrategy();

    Play play = bs.getPlay(hand, upCard);

    assert play == Play.NONE;
  }
}
