package patrick.test.shoe;

/*
 * Copyright (c) Ron Coleman
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import charlie.actor.Courier;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.plugin.IUi;
import charlie.test.framework.Perfect;

import javax.swing.text.StyledEditorKit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class tests MyShoe02 and expects an outcome of winning
 * with a 5 card charlie.
 * @author Patrick Muller
 */
public class MyPerfectShoeTest extends Perfect implements IUi {
  Hid you;
  Hid dealer;
  Boolean myTurn = false;
  Map<Hid, Hand> hands = new HashMap<>();

  // Moved these values to be final static so they are in scope
  // of the method Charlie(), to test in best practice.
  final static int BET_AMT = 5;
  final static int SIDE_BET_AMT = 0;

  /**
   * Runs the test.
   */
  public void test() throws Exception {
    // Set shoe property
    System.setProperty("charlie.shoe", "patrick.plugin.MyShoe02");

    // Starts the server and logs in using only defaults
    go(this);

    // Now that the game server is ready, to start a game, we just need to
    // send in a bet which in the GUI is like pressing DEAL.
    bet(BET_AMT,SIDE_BET_AMT);
    info("bet amt: "+BET_AMT+", side bet: "+SIDE_BET_AMT);

    ////////// All test logic at this point done by IUi implementation.

    // Wait for dealer to call end of game.
    assert await(20000);

    // End of scope closes sockets which shuts down client and server.
    info("DONE !");
  }

  /**
   * This method gets invoked whenever a card is dealt.
   * @param hid Target hand
   * @param card Card
   * @param handValues Hand value and soft value
   */
  @Override
  public void deal(Hid hid, Card card, int[] handValues) {
    // If it's our card add it to our hand
    if (hid.getSeat() == Seat.YOU) {
      Hand myHand = hands.get(you);
      myHand.hit(card);

      // if it is our turn, call our play
      if (myTurn)
        play(hid);
    }
    else {
      // dealer's turn
      if (hid.getSeat() == Seat.DEALER && card != null) {
        Hand dealerHand = hands.get(hid);
        dealerHand.hit(card);
      }
    }

    info("DEAL: "+hid+" card: "+card+" hand values: "+handValues[0]+", "+handValues[1]);
  }

  /**
   * This method gets invoked only once whenever the turn changes.
   * @param hid New hand's turn
   */
  @Override
  public void play(Hid hid) {
    // Skip if it's not our turn
    if (hid.getSeat() != Seat.YOU) {
      myTurn = false;
      return;
    }

    // our turn, hit
    myTurn = true;
    hit(you);
  }

  /**
   * This method gets invoked if a hand breaks.
   * @param hid Target hand
   */
  @Override
  public void bust(Hid hid) {
    // Not possible
    assert false;
  }

  /**
   * This method gets invoked for a winning hand.
   * @param hid Target hand
   */
  @Override
  public void win(Hid hid) {
    // Not possible
    assert false;
  }

  /**
   * This method gets invoked for a losing hand.
   * @param hid Target hand
   */
  @Override
  public void lose(Hid hid) {
    // Not possible
    assert false;
  }

  /**
   * This method gets invoke for a hand that pushes, ie, has same value as dealer's hand.
   * @param hid Target hand
   */
  @Override
  public void push(Hid hid) {
    // Not possible
    assert false;
  }

  /**
   * This method gets invoked for a (natural) Blackjack hand, Ace+K, Ace+Q, etc.
   * @param hid Target hand
   */
  @Override
  public void blackjack(Hid hid) {
    // Not possible
    assert false;
  }

  /**
   * This method gets invoked for a 5-card Charlie hand.
   * @param hid Target hand
   */
  @Override
  public void charlie(Hid hid) {
    // Assert there are two players at the start of the game
    // by checking the size of hands
    assert hands.size() == 2;

    // We're the player that got charlie
    assert hid.getSeat() == Seat.YOU;

    // Retrieve our hand
    Hand myHand = hands.get(you);

    // Only 5 cards in our hand
    assert myHand.size() == 5;

    // Hand value is <= 21
    assert myHand.getValue() <= 21;

    // Hand is a charlie
    assert myHand.isCharlie();

    // P&L == 2 * BET_AMT
    assert hid.getAmt() == 2*BET_AMT;

    // Side P&L == 0
    assert hid.getSideAmt() == SIDE_BET_AMT;

    // Dealer has 2 cards
    Hand dealerHand = hands.get(dealer);
    assert dealerHand.size() == 2;
  }

  /**
   * This method get invoked at the start of a game before any cards are dealt.
   * @param hids Hands in the game
   * @param shoeSize Current shoe size, ie, original shoe less cards dealt
   */
  @Override
  public void startGame(List<Hid> hids, int shoeSize) {
    StringBuilder buffer = new StringBuilder();

    buffer.append("game STARTING: ");

    for(Hid hid: hids) {
      buffer.append(hid).append(", ");

      if(hid.getSeat() == Seat.YOU)
        this.you = hid;

      // Set dealer's hid to a member
      else if(hid.getSeat() == Seat.DEALER)
        this.dealer = hid;

      // Add all hids and hands to the hands member for full scope
      hands.put(hid, new Hand(hid));
    }
    buffer.append(" shoe size: ").append(shoeSize);
    info(buffer.toString());
  }

  /**
   * This method gets invoked after a game ends and before the start of a new game.
   * @param shoeSize Endind shoe size
   */
  @Override
  public void endGame(int shoeSize) {
    signal();

    info("ENDING game shoe size: "+shoeSize);
  }

  /**
   * This method gets invoked when the burn card appears, it indicates a
   * re-shuffle is coming after the current game ends.
   */
  @Override
  public void shuffling() {
    info("SHUFFLING");
  }

  /**
   * This method sets the courier.
   * It's not used here because the base test case instantiates a courier for us.
   * @param courier Courier
   */
  @Override
  public void setCourier(Courier courier) {
  }

  /**
   * This method gets invoked when a player requests a split.
   * For instance, a 4+4 split results in two hands, each with two cards,
   * 4+x and 4+y where "x" and "y" are hits to each hand which the dealer
   * automatically performs, respectively.
   * @param newHid New hand split from the original.
   * @param origHid Original hand.
   */
  @Override
  public void split(Hid newHid, Hid origHid) {
    // Not possible
    assert false;
  }

  /**
   * Handles insurance requests.
   */
  @Override
  public void insure() {
    // Insurance not supported.
    assert false;
  }
}
