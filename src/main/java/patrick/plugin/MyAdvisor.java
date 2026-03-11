package patrick.plugin;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.plugin.IAdvisor;
import charlie.util.Play;
import patrick.client.BasicStrategy;

public class MyAdvisor implements IAdvisor {


    @Override
    public Play advise(Hand hand, Card card) {
        BasicStrategy bs = new BasicStrategy();
        return bs.getPlay(hand, card);
    }
}
