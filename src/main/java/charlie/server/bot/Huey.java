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
    private final static int MAX_DELAY = 3000;
    private final static Random random = new Random();

    Seat mySeat;
    Hid myHid = new Hid();
    Map<Hid, Hand> hands = new HashMap<>();
    Dealer dealer;
    Card upCard;
    boolean myTurn = false;
    BasicStrategy bs = new BotBasicStrategy();

    public void randDelay() {
        try{
            Thread.sleep(Huey.random.nextInt(Huey.MAX_DELAY));
        }
        catch (InterruptedException e){
            System.out.println("Interrupted during randDelay");
        }
    }

    public Hand getHand() {
        return hands.get(myHid);
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public void sit(Seat seat) {
        mySeat = Seat.RIGHT;
    }

    @Override
    public void startGame(List<Hid> list, int i) {
        for (Hid hid : list) {
            if (hid.getSeat() == mySeat)
                myHid = hid;
            hands.put(hid, new Hand());
        }
    }

    @Override
    public void endGame(int i) {

    }

    @Override
    public void deal(Hid hid, Card card, int[] ints) {
        if (hid.getSeat() == mySeat) {
            Hand myHand = hands.get(hid);
            myHand.hit(card);

            if (myTurn)
                play(myHid);
        }
        if (hid.getSeat() == Seat.DEALER && card != null) {
            Hand dealerHand = hands.get(hid);
            dealerHand.hit(card);
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

    @Override
    public void play(Hid hid) {
        // not our hand, skip
        if (hid != myHid)
            return;

        // now our turn
        if (hid.getSeat() == mySeat){
            myTurn = true;
        }

        Play myPlay = bs.getPlay(hands.get(hid), upCard);

        if (myPlay == Play.STAY) {
            new Thread(() -> {
                randDelay();
                dealer.stay(this, myHid);
            }).start();
        }
        else if(myPlay == Play.HIT){
            new Thread(() -> {
                randDelay();
                dealer.hit(this, hid);
            });
        }
        else {
            new Thread(() -> {
                randDelay();
                dealer.doubleDown(this, hid);
            });
        }
    }

    @Override
    public void split(Hid hid, Hid hid1) {

    }

    @Override
    public void run() {

    }

}
