/*
 * Copyright (c) 2026 Hexant, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package patrick.client;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.util.Play;

/**
 * This file is an implementation of the basic strategy
 * in blackjack.
 *
 * @author Patrick Muller
 */
public class BasicStrategy {
  // These help make table formatting compact to look like the pocket card.
  public final static Play P = Play.SPLIT;
  public final static Play H = Play.HIT;
  public final static Play S = Play.STAY;
  public final static Play D = Play.DOUBLE_DOWN;

  // Face cards have a value of 10.
  public final static int faceCardValue = 10;

  /**
   * Rules for section 1; see Instructional Services (2000) pocket card
   */
  Play[][] section1Rules = {
    /*        2  3  4  5  6  7  8  9  T  A  */
    /* 21 */ {S, S, S, S, S, S, S, S, S, S},
    /* 20 */ {S, S, S, S, S, S, S, S, S, S},
    /* 19 */ {S, S, S, S, S, S, S, S, S, S},
    /* 18 */ {S, S, S, S, S, S, S, S, S, S},
    /* 17 */ {S, S, S, S, S, S, S, S, S, S},
    /* 16 */ {S, S, S, S, S, H, H, H, H, H},
    /* 15 */ {S, S, S, S, S, H, H, H, H, H},
    /* 14 */ {S, S, S, S, S, H, H, H, H, H},
    /* 13 */ {S, S, S, S, S, H, H, H, H, H},
    /* 12 */ {H, H, S, S, S, H, H, H, H, H}
  };

  /**
   * Rules for section 2; see Instructional Services (2000) pocket card
   */
  Play[][] section2Rules = {
    /*         2  3  4  5  6  7  8  9  T  A  */
    /*  11 */ {D, D, D, D, D, D, D, D, D, H},
    /*  10 */ {D, D, D, D, D, D, D, D, H, H},
    /*   9 */ {H, D, D, D, D, H, H, H, H, H},
    /*   8 */ {H, H, H, H, H, H, H, H, H, H},
    /*   7 */ {H, H, H, H, H, H, H, H, H, H},
    /*   6 */ {H, H, H, H, H, H, H, H, H, H},
    /*   5 */ {H, H, H, H, H, H, H, H, H, H}
  };

  /**
   * Rules for section 3; see Instructional Services (2000) pocket card
   */
  Play[][] section3Rules = {
    /*           2  3  4  5  6  7  8  9  T  A  */
    /*  A,10 */ {S, S, S, S, S, S, S, S, S, S},
    /*  A,9  */ {S, S, S, S, S, S, S, S, S, S},
    /*  A,8  */ {S, S, S, S, S, S, S, S, S, S},
    /*  A,7  */ {S, D, D, D, D, S, S, H, H, H},
    /*  A,6  */ {H, D, D, D, D, H, H, H, H, H},
    /*  A,5  */ {H, H, D, D, D, H, H, H, H, H},
    /*  A,4  */ {H, H, D, D, D, H, H, H, H, H},
    /*  A,3  */ {H, H, H, D, D, H, H, H, H, H},
    /*  A,2  */ {H, H, H, D, D, H, H, H, H, H},
  };

  /**
   * Rules for section 4; see Instructional Services (2000) pocket card
   */
  Play[][] section4Rules = {
    /*              2  3  4  5  6  7  8  9  T  A  */
    /*  A,A-8,8 */ {P, P, P, P, P, P, P, P, P, P},
    /*    10,10 */ {S, S, S, S, S, S, S, S, S, S},
    /*      9,9 */ {P, P, P, P, P, S, P, P, S, S},
    /*      7,7 */ {P, P, P, P, P, P, H, H, H, H},
    /*      6,6 */ {P, P, P, P, P, H, H, H, H, H},
    /*      5,5 */ {D, D, D, D, D, D, D, D, H, H},
    /*      4,4 */ {H, H, H, P, P, H, H, H, H, H},
    /*      3,3 */ {P, P, P, P, P, P, H, H, H, H},
    /*      2,2 */ {P, P, P, P, P, P, H, H, H, H}
  };

  /**
   * Created helper method to calculate the column index
   * in the doSection methods. The logic in all the methods
   * is the same, so this helps the code adhere to
   * DRY principles.
   *
   * @param upCard The dealer's upcard
   */
  public int getColumn(Card upCard) {
    // Subtract 2 since the dealer's up-card starts at 2
    int colIndex = upCard.getRank() - 2;

    if (upCard.isFace())
      colIndex = faceCardValue - 2;

      // Ace is the 10th card (index 9)
    else if (upCard.isAce())
      colIndex = 9;

    return colIndex;
  }


  /**
   * Gets the play for player's hand vs. dealer up-card.
   *
   * @param hand   Hand player hand
   * @param upCard Dealer up-card
   * @return Play based on basic strategy
   */
  public Play getPlay(Hand hand, Card upCard) {
    // the hand/card we received is malformed
    if (!isValid(hand, upCard))
      return Play.NONE;

    Card card1 = hand.getCard(0);
    Card card2 = hand.getCard(1);

    // All pairs are evaluated in section 4 of the strategy card
    if (hand.isPair())
      return doSection4(hand, upCard);

      // All cards in section 3 have 1 Ace card + any other card
    else if (hand.size() == 2 && (card1.getRank() == Card.ACE || card2.getRank() == Card.ACE))
      return doSection3(hand, upCard);

      // All cards in section 2 are between and include values of 5 and 11
    else if (hand.getValue() >= 5 && hand.getValue() < 12)
      return doSection2(hand, upCard);

      // Any other hand with a value greater or equal to 12 lands in section 1
    else if (hand.getValue() >= 12)
      return doSection1(hand, upCard);

    return Play.NONE;
  }

  /**
   * Does section 1 processing of the basic strategy, 12-21 (player) vs. 2-A (dealer)
   *
   * @param hand   Player's hand
   * @param upCard Dealer's up-card
   */
  protected Play doSection1(Hand hand, Card upCard) {
    int value = hand.getValue();

    // Subtract 21 since the player's hand starts at 21, and we're working
    // our way down through section 1 from index 0.
    int rowIndex = 21 - value;

    Play[] row = section1Rules[rowIndex];

    int colIndex = getColumn(upCard);

    return row[colIndex];
  }

  /**
   * Does section 2 processing of the basic strategy, 5-11 (player) vs. 2-A (dealer)
   *
   * @param hand   Player's hand
   * @param upCard Dealer's up-card
   */
  protected Play doSection2(Hand hand, Card upCard) {
    int value = hand.getValue();

    // Subtract value from 11 because the player's hand starts at 11
    // in section 2, and we're working our way down from index zero.
    int rowIndex = 11 - value;

    Play[] row = section2Rules[rowIndex];

    int colIndex = getColumn(upCard);

    return row[colIndex];
  }

  /**
   * Does section 3 processing of the basic strategy, A+_ (player) vs. 2-A (dealer)
   *
   * @param hand   Player's hand
   * @param upCard Dealer's up-card
   */
  protected Play doSection3(Hand hand, Card upCard) {
    int value = hand.getValue();

    // Subtract value from 21 because the player's hand starts at 21
    // in section 3, and we're working our way down from index zero.
    int rowIndex = 21 - value;

    Play[] row = section3Rules[rowIndex];

    int colIndex = getColumn(upCard);

    return row[colIndex];
  }

  /**
   * Does section 4 processing of the basic strategy, A+_ (player) vs. 2-A (dealer)
   *
   * @param hand   Player's hand
   * @param upCard Dealer's up-card
   */
  protected Play doSection4(Hand hand, Card upCard) {
    int value = hand.getValue();
    Card card1 = hand.getCard(0);

    // Reduce the pair by dividing its value by 2, and then
    // subtract the result from 10. Add 1 at the end because
    // we are starting from index 1.
    int rowIndex = 10 - (value / 2) + 1;

    // If A,A or 8,8 we set index to 0.
    if (card1.getRank() == Card.ACE || card1.getRank() == 8)
      rowIndex = 0;

      // 8,8 is included with the ace, leaving a gap in the table.
      // Fix this offset by subtracting 1 to the row index if we are at
      // 7,7 or lower.
    else if (value <= 14)
      rowIndex--;

    Play[] row = section4Rules[rowIndex];

    int colIndex = getColumn(upCard);

    return row[colIndex];
  }

  /**
   * Validates a hand and up-card.
   *
   * @param hand   Hand
   * @param upCard Up-card
   * @return True if both are valid, false otherwise
   */
  boolean isValid(Hand hand, Card upCard) {
    return isValid(hand) && isValid(upCard);
  }

  /**
   * Validates a hand.
   *
   * @param hand Hand
   * @return True if valid, false otherwise
   */
  boolean isValid(Hand hand) {
    if (hand == null)
      return false;

    // hand is only valid if it has 2 or more cards
    if (hand.size() < 2)
      return false;

    // I'm going to comment this out for now since our table has
    // hand values that add to 21 inside of it, for example
    // 17+ includes 21, A,10 is 21.
    // can't call a play on a blackjack
    //  if (hand.isBlackjack())
    //  return false;

    // can't call a play on a charlie
    if (hand.isCharlie())
      return false;

    // can't call a play on a bust and a hand can't be less than a 4
    if (hand.getValue() > 21 || hand.getValue() < 4)
      return false;

    // Every card in the hand must have a rank between 1 and 13.
    // Soft Ace - King
    for (int i = 0; i < hand.size(); i++) {
      Card card = hand.getCard(i);
      if (card.getRank() < 1 || card.getRank() > 13)
        return false;
    }

    // hand is valid
    return true;
  }

  /**
   * Validates a card
   *
   * @param card Card
   * @return True if valid, false otherwise
   */
  boolean isValid(Card card) {
    if (card == null)
      return false;

    // rank must be between 1 and 13
    if (card.getRank() < 1 || card.getRank() > 13)
      return false;

    // card is valid
    return true;
  }
}
