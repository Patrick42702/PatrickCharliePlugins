package patrick.client;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.util.Play;

public class BotBasicStrategy extends BasicStrategy {

    private static final int PAIR_2S = 4;
    private static final int PAIR_4S = 8;

    /**
     * This method overrides the basic strategy that a player would use to fit the bots ruleset.
     * Bots cannot split, thus need to make their best possible decision in suggested split scenarios,
     * like hit low values, stand on high, etc.
     *
     * @param hand   Hand player hand
     * @param upCard Dealer up-card
     * @return The play the bot will make
     * @author Patrick Muller
     */
    @Override
    public Play getPlay(Hand hand, Card upCard) {
        // Get basic strategy play, if it's not a split, return it
        Play play = super.getPlay(hand, upCard);

        if (play != Play.SPLIT)
            return play;

        // If hand value is 4, we must return hit as it does not follow under doSection2rules
        else if (hand.getValue() == PAIR_2S)
            return Play.HIT;

        // All hand values 5-11 that would call for a split now follow under section 2 rules
        // 9,10,11 aren't hand value's that would reach this scenario, so use a value of 8
        // for the check (pair of 4s)
        else if (hand.getValue() <= PAIR_4S)
            return doSection2(hand, upCard);

        // The rest of the hands values fall within section 1 rules
        else
            return doSection1(hand, upCard);
    }
}
