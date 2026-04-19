package patrick.test.client;

import charlie.actor.Courier;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.plugin.IUi;
import charlie.test.framework.Perfect;
import charlie.util.Play;
import patrick.client.BasicStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfectSideBet extends Perfect implements IUi {

  final static int BET_AMT = 25;
  final static int SIDE_BET_AMT = 10;
  final static int STARTING_BANKROLL = 1000;

  Hid you;
  Hid dealer;
  Boolean myTurn = false;
  Map<Hid, Hand> hands = new HashMap<>();
  BasicStrategy bs = new BasicStrategy();
  double bankroll = STARTING_BANKROLL;
  int gameno = 1;

  /**
   * Runs the test.
   */
  public void test() throws Exception {
    System.setProperty("charlie.shoe", "charlie.sidebet.test.Shoe");

    System.setProperty("charlie.sidebet.rule", "charlie.sidebet.rule.SideBetRule");

    System.setProperty("charlie.sidebet.view", "charlie.sidebet.view.SideBetView");

    // Starts the server and logs in using only defaults
    go(this);

    for (; gameno <= 10; gameno++) {
      resetAwait();
      info("about to call bet #: " + gameno);
      if (gameno == 1) {
        bet(BET_AMT, 0);
      }
      else {
        bet(BET_AMT, SIDE_BET_AMT);
      }
      assert await(20000);
      info("just waited for bet #: " + gameno);
      info("current bankroll: " + bankroll);
    }

    // End of scope closes sockets which shuts down client and server.
    info("DONE !");
    info("FINAL BANKROLL" + bankroll);
  }

  @Override
  public void setCourier(Courier courier) {

  }

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

  @Override
  public void endGame(int shoeSize) {
    myTurn = false;
    hands.clear();
    signal();

    info("ENDING game shoe size: " + shoeSize);
  }

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

  @Override
  public void bust(Hid hid) {
    assert(false);
  }

  @Override
  public void win(Hid hid) {
    bankroll += (hid.getAmt() + hid.getSideAmt());

    switch(gameno) {
      case 2 -> {
        // super 7, win 3:1
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == 3 * SIDE_BET_AMT);
        assert(bankroll == 1055);
      }
      case 3 -> {
        // lose sidebet
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == -SIDE_BET_AMT);
        assert(bankroll == 1070);
      }
      case 6 -> {
        // royal match, pay 25:1
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == 25 * SIDE_BET_AMT);
        assert(bankroll == 1315);
      }
      case 7 -> {
        // lose sidebet
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == -SIDE_BET_AMT);
        assert(bankroll == 1330);
      }
      case 8 -> {
        // exactly 13, pay 1:1
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == SIDE_BET_AMT);
      }
      case 9 -> {
        // super 7, win 3:1
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == 3 * SIDE_BET_AMT);
        assert(bankroll == 1420);
      }
      case 10 -> {
        // lose sidebet
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == BET_AMT);
        assert(hid.getSideAmt() == -SIDE_BET_AMT);
        assert(bankroll == 1435);
      }
      default -> {
        assert(false);
      }
    }
  }

  @Override
  public void blackjack(Hid hid) {
    assert(false);
  }

  @Override
  public void charlie(Hid hid) {
    assert(false);
  }

  @Override
  public void lose(Hid hid) {
    info("P+L lost: " + (hid.getAmt() + hid.getSideAmt()));
    bankroll += (hid.getAmt() + hid.getSideAmt());
    switch(gameno) {
      case 4 -> {
        // win super 7 side bet
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == -BET_AMT);
        assert(hid.getSideAmt() == 3 * SIDE_BET_AMT);
        assert(bankroll == 1075);
      }
      case 5 -> {
        // lose side bet
        assert(hid.getSeat() == Seat.YOU);
        assert(hid.getAmt() == -BET_AMT);
        assert(hid.getSideAmt() == -SIDE_BET_AMT);
        assert(bankroll == 1040);
      }
      default -> {
        assert(false);
      }
    }
  }

  @Override
  public void push(Hid hid) {
    info("P+L push: " + (hid.getAmt() + hid.getSideAmt()));
    bankroll += (hid.getAmt() +  hid.getSideAmt());
    if (gameno == 1) {
      assert(hid.getSeat() == Seat.YOU);
      assert(hid.getAmt() == 0);
      assert(hid.getSideAmt() == 0);
      assert(bankroll == 1000);
    }
    else {
      assert(false);
    }
  }

  @Override
  public void shuffling() {
    assert(false);
  }

  @Override
  public void play(Hid hid) {
    // Skip if it's not our turn
    if (hid.getSeat() != Seat.YOU) {
      myTurn = false;
      return;
    }

    // play basic strategy
    myTurn = true;
    Card upCard = hands.get(dealer).getCard(1);

    Play play = bs.getPlay(hands.get(you), upCard);
    info("This is the dealer's upCard: "+upCard);
    info("This is my hand value: " + hands.get(you).getValue() + " this is my play that im sending: " + play.toString());

    // call our play
    switch (play) {
      case HIT ->
        hit(you);
      case STAY ->
        stay(you);
      case DOUBLE_DOWN ->
        doubleDown(you);
      case SPLIT ->
        split(you);
    }
  }

  @Override
  public void split(Hid hid, Hid hid1) {
    assert(false);
  }
}
