package charlie.server.bot;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Dealer;
import charlie.dealer.Seat;
import charlie.plugin.IBot;
import charlie.util.Play;
import patrick.client.BasicStrategy;
import patrick.client.BotBasicStrategy;

import java.util.*;

public class Huey implements IBot, Runnable {
  // On average, we want 2.5-second delay, so set random to 5 seconds
  private final static int MIN_DELAY = 2000;
  private final static int MAX_DELAY = 3000;
  private final static Random random = new Random();

  Seat mySeat;
  Hid myHid;
  Hand myHand;
  Hid dealerHid;
  Dealer dealer;
  Card upCard;
  boolean myTurn = false;
  BasicStrategy bs = new BotBasicStrategy();

  /**
   * Set the calling thread to sleep for a random amount of time between 2-3 seconds,
   * so that the average sleep time should be about 2.5 seconds
   * @author Patrick Muller
   */
  public void randDelay() {
    try {
      Thread.sleep(Huey.random.nextInt(Huey.MIN_DELAY, Huey.MAX_DELAY + 1));
    } catch (InterruptedException e) {
      System.out.println("Interrupted during randDelay");
    }
  }

  /**
   * dealer calls this method to get bot's hand, initialize member variable
   * @author Patrick Muller
   */
  public Hand getHand() {
    myHid = new Hid(mySeat);
    myHand = new Hand(myHid);
    return myHand;
  }

  /**
   * set the dealer reference to member var
   * @param dealer dealer reference
   * @author Patrick Muller
   */
  public void setDealer(Dealer dealer) {
    this.dealer = dealer;
  }

  /**
   * accept our seat
   * @param seat Our seat given by the dealer
   * @author Patrick Muller
   */
  @Override
  public void sit(Seat seat) {
    mySeat = seat;
  }

  /**
   * Method called when starting game.
   * Add reference of dealers hand to member var.
   * @param list list of hids in the game
   * @param i count of hands
   * @author Patrick Muller
   */
  @Override
  public void startGame(List<Hid> list, int i) {
    for (Hid hid : list) {
      if (hid.getSeat() == Seat.DEALER) {
        dealerHid = hid;
      }
    }
  }

  @Override
  public void endGame(int i) {

  }

  /**
   * Called when dealer deals a card to any player. Check if its our
   * turn before calling the play
   * @param hid Card getting dealt to this hid
   * @param card The card getting dealt
   * @param ints
   * @author Patrick Muller
   */
  @Override
  public void deal(Hid hid, Card card, int[] ints) {
    // check if card is for us and our turn
    if (hid.getSeat() == mySeat && myTurn) {
      play(myHid);
    }
    // set the dealer's upCard
    else if (hid.getSeat() == Seat.DEALER && card != null) {
      upCard = card;
    }
  }

  @Override
  public void insure() {

  }

  @Override
  public void bust(Hid hid) {

  }

  @Override
  public void win(Hid hid) {

  }

  @Override
  public void blackjack(Hid hid) {

  }

  @Override
  public void charlie(Hid hid) {

  }

  @Override
  public void lose(Hid hid) {

  }

  @Override
  public void push(Hid hid) {

  }

  @Override
  public void shuffling() {

  }

  /**
   * Method invoked during turn change. Call our play if its our turn.
   * @param hid hid of player's turn
   * @author Patrick Muller
   */
  @Override
  public void play(Hid hid) {
    // not our hand, skip
    if (hid.getSeat() != mySeat) {
      myTurn = false;
      return;
    }

    // now our turn
    else if (hid.getSeat() == mySeat)
      myTurn = true;

    // get the bot's play
    Play myPlay = bs.getPlay(myHand, upCard);

    // Start a worker thread based on the bot strategy using dealer reference
    if (myPlay == Play.STAY) {
      new Thread(() -> {
        randDelay();
        dealer.stay(this, myHid);
      }).start();
    }

    else if (myPlay == Play.HIT) {
      new Thread(() -> {
        randDelay();
        dealer.hit(this, hid);
      }).start();
    }

    else if (myPlay == Play.DOUBLE_DOWN) {
      // if play is a double down, our turn is now over regardless
      myTurn = false;

      new Thread(() -> {
        randDelay();
        dealer.doubleDown(this, hid);
      }).start();
    }

    else {
      System.out.println("Play is None or split, something very wrong");
    }
  }

  @Override
  public void split(Hid hid, Hid hid1) {

  }

  @Override
  public void run() {

  }

}
