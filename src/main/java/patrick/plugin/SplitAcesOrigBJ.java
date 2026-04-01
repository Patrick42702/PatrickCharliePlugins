package patrick.plugin;

import charlie.shoe.Shoe;
import charlie.card.Card;
import charlie.card.Card.Suit;

/**
 * This class extends the basic Shoe implementation
 * and overrides the initialization method. It places
 * 10 cards into the shoe given from the lab.
 * @author Patrick Muller
 */
public class SplitAcesOrigBJ extends Shoe {

    /**
     * Override Shoe Initialization behavior
     */
    @Override
    public void init() {
        cards.clear();
        cards.add(new Card(Card.ACE, Suit.SPADES));
        cards.add(new Card(Card.QUEEN, Suit.HEARTS));
        cards.add(new Card(Card.ACE, Suit.SPADES));
        cards.add(new Card(3, Suit.HEARTS));
        cards.add(new Card(Card.KING, Suit.SPADES));
        cards.add(new Card(3, Suit.CLUBS));
        cards.add(new Card(4, Suit.SPADES));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(3, Suit.CLUBS));
        cards.add(new Card(4, Suit.SPADES));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(3, Suit.CLUBS));
        cards.add(new Card(4, Suit.SPADES));
        cards.add(new Card(5, Suit.CLUBS));
    }
}