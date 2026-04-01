package patrick.plugin;

import charlie.shoe.Shoe;
import charlie.card.Card;
import charlie.card.Card.Suit;

/**
 * This shoe is to test split aces with no blackjacks functionality
 * @author Patrick Muller
 */
public class SplitAcesNoBJ extends Shoe {

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
        cards.add(new Card(4, Suit.SPADES));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(4, Suit.SPADES));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(5, Suit.CLUBS));
        cards.add(new Card(5, Suit.CLUBS));
    }
}