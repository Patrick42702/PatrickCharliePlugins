package patrick.server;

import charlie.actor.House;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.plugin.IPlayer;
import charlie.util.Play;
import org.apache.log4j.Logger;

public class MyDealer extends charlie.dealer.Dealer {
  Logger LOG = Logger.getLogger(MyDealer.class);

  public MyDealer(House house) {
    super(house);
  }

  @Override
  public void hit(IPlayer iplayer, Hid hid) {
    // Validate the request
    Hand hand = validate(hid);
    if (hand == null) {
      LOG.error("got invalid HIT player = " + iplayer);
      return;
    }

    // Deal a card
    Card card = deal();
    hand.hit(card);

    hid.request(Play.HIT);

    LOG.info("hit hid = " + hid + " with " + card);

    // All players MUST test for charlie. Otherwise they will
    // not know they have this hand and may try to hit if hand<21.
    for (IPlayer player : playerSequence) {
      player.deal(hid, card, hand.getValues());
    }


    // If the hand isBroke, we're done with this hand
    if (hand.isBroke()) {
      updateBankroll(hid, LOSS);

      // Tell everyone what happened
      for (IPlayer _player : playerSequence)
        _player.bust(hid);

      LOG.info("going to next hand");
      goNextHand();
    }
    // If hand got a Charlie or Blackjack, we're done with this hand
    else if (hand.isCharlie()) {
//            hid.multiplyAmt(CHARLIE_PAYS);
      hid.request(Play.STAY);

      updateBankroll(hid, CHARLIE_PAYS);

      // Tell everyone what happened
      for (IPlayer _player : playerSequence)
        _player.charlie(hid);

      goNextHand();
    } else if (hand.isBlackjack()) {
      hid.request(Play.STAY);

      updateBankroll(hid, BLACKJACK_PAYS);

      // Tell everyone what happened
      for (IPlayer _player : playerSequence)
        _player.blackjack(hid);
      goNextHand();
    }
    // Player has 21: don't force player to break!
    else if (hand.getValue() == 21) {
      goNextHand();
    }
  }


  @Override
  protected void goNextHand() {
    LOG.info("hand sequence index = " + nextHandIndex + " hand sequence size = " + handSequence.size());

    // Get next hand and inform player
    if (nextHandIndex < handSequence.size()) {
      // Did we "hit" a split hand this time
      boolean firstSplitHit = false;

      Hid hid = handSequence.get(nextHandIndex++);

      active = players.get(hid);
      LOG.info("active player = " + active);

      // Check for isBlackjack before moving on
      Hand hand = this.hands.get(hid);

      // If hand has Blackjack, it's not automatic hand wins
      // since the dealer may also have isBlackjack
      if (hand.isBlackjack()) {
        goNextHand();
        return;
      }

      // Is this hand created from a "split" AND about to be new turn?
      // If so, we need to "HIT" the hand with its first card.
      if (hid.isSplit() && hand.size() == 1) {
        // Need to request a delay or it comes out too fast.
        try {
          Thread.sleep(DEAL_DELAY);

          Card card = deal();

          hand.hit(card);

          firstSplitHit = true;
        } catch (InterruptedException ex) {
          LOG.error(ex.getMessage());
        }
      }

      // Loop through players to announce plays
      for (IPlayer player : playerSequence) {

        // if this is the first hit of a split, announce deal to players
        if (firstSplitHit) {

          player.deal(hid, hand.getCard(1), hand.getValues());

          // if this hit results in a blackjack, simulate blackjack outcome
          // and announce to players
          if (hand.isBlackjack()) {
            updateBankroll(hid, BLACKJACK_PAYS);

            for (IPlayer _player : playerSequence) {
              _player.blackjack(hid);
            }

            // Hand is over with a blackjack
            goNextHand();
          }
        }
        LOG.info("sending turn " + hid + " to " + player);
        player.play(hid);
      }
    } else {
      // If there are no more hands, close out game with dealer
      // making last play.
      closeGame();
    }
  }
}
