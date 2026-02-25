package patrick.test.bs.invalid;


import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.util.Play;
import junit.framework.TestCase;
import patrick.client.BasicStrategy;

/**
 * This tests that a charlie is invalid for a play
 * @author Patrick Muller
 */
public class TestCharlie extends TestCase {

  /**
   * Runs the test
   */
  public void test() {
    Hand hand = new Hand(new Hid(Seat.YOU));

    // add 5 cards for the charlie
    hand.hit(new Card(2, Card.Suit.CLUBS));
    hand.hit(new Card(3, Card.Suit.DIAMONDS));
    hand.hit(new Card(4, Card.Suit.SPADES));
    hand.hit(new Card(5, Card.Suit.SPADES));
    hand.hit(new Card(6, Card.Suit.SPADES));

    Card upCard = new Card(10, Card.Suit.HEARTS);

    BasicStrategy bs = new BasicStrategy();

    Play play = bs.getPlay(hand, upCard);

    assert play == Play.NONE;
  }
}
