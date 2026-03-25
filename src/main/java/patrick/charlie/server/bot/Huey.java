package patrick.charlie.server.bot;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Dealer;
import charlie.dealer.Seat;
import charlie.plugin.IBot;

import java.util.List;

public class Huey implements IBot, Runnable {
    Hid myHid = new Hid();

    public Hand getHand() {
        return null;
    }

    public void setDealer(Dealer dealer) {

    }

    @Override
    public void sit(Seat seat) {

    }

    @Override
    public void startGame(List<Hid> list, int i) {

    }

    @Override
    public void endGame(int i) {

    }

    @Override
    public void deal(Hid hid, Card card, int[] ints) {

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

    }

    @Override
    public void split(Hid hid, Hid hid1) {

    }

    @Override
    public void run() {

    }
}
