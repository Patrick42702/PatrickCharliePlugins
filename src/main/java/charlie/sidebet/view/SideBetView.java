/*
 Copyright (c) 2014 Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package charlie.sidebet.view;

import charlie.audio.Effect;
import charlie.audio.SoundFactory;
import charlie.card.Hid;
import charlie.plugin.ISideBetView;
import charlie.view.AMoneyManager;

import charlie.view.sprite.Chip;
import charlie.view.sprite.ChipButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * This class implements the side bet view
 *
 * @author Patrick Muller
 */
public class SideBetView implements ISideBetView {
    private final Logger LOG = Logger.getLogger(SideBetView.class);

    public final static int X = 400;
    public final static int Y = 200;
    public final static int DIAMETER = 50;

    protected Font font = new Font("Arial", Font.BOLD, 18);
    protected BasicStroke stroke = new BasicStroke(3);

    // See http://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html
    protected float dash1[] = {10.0f};
    protected BasicStroke dashed
            = new BasicStroke(3.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

    protected List<ChipButton> buttons;
    protected int amt = 0;
    protected AMoneyManager moneyManager;

    private Random random = new Random();

    // these will be used to add visible chips to the side bet
    private List<Chip> chips = new ArrayList<>();

    // offset the chip's position to the right of the betting circle
    int CHIP_BASE_X = X + DIAMETER / 2 + 10;  // to the right of circle
    int CHIP_BASE_Y = Y - DIAMETER / 2 + 5;

    /**
     * Add a new chip to the screen, with a bit of randomness to make it
     * appear as if we were at a real casino.
     * @param image The image of the chip button that was pressed
     * @param chipWidth The width of the chip button that was pressed
     */
    public void addChip(Image image, int chipWidth) {
        // place chips to the right of the betting circle with randomness
        // and use # of chips to avoid stacking them on top of each other.
        int placeX = CHIP_BASE_X + this.chips.size() * chipWidth/3 + random.nextInt(10) - 5;
        int placeY = CHIP_BASE_Y + random.nextInt(6) - 3;

        Chip chip = new Chip(image, placeX, placeY, amt);

        this.chips.add(chip);
    }

    /**
     * Return true if the user clicked within the radius of the at-stake
     * side bet circle.
     * @param x x of where user clicked
     * @param y y of where user clicked
     * @return True if user clicked within the circle
     */
    private boolean isInsideCircle(int x, int y) {
        int radius = DIAMETER / 2;

        int dx = x - X;
        int dy = y - Y;

        // Distance formula. We will square both sides to reduce
        // compute as we don't need the exact distance.
        // If the distance between where the user clicked and the
        // center of the circle is greater than the radius ^ 2,
        // return false
        // Math.sqrt(dx * dx + dy * dy) = radius  OR
        // (dx * dx + dy * dy) = Math.pow(radius, 2)
        return (dx * dx + dy * dy) <= Math.pow(radius, 2);
    }

    public SideBetView() {
        LOG.info("side bet view constructed");
    }

    /**
     * Sets the money manager.
     *
     * @param moneyManager
     */
    @Override
    public void setMoneyManager(AMoneyManager moneyManager) {
        this.moneyManager = moneyManager;
        this.buttons = moneyManager.getButtons();
    }

    /**
     * Registers a click for the side bet.
     * This method gets invoked on right mouse click.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void click(int x, int y) {
        int oldAmt = amt;

        // Test if any chip button has been pressed.
        for (ChipButton button : buttons) {
            if (button.isPressed(x, y)) {
                amt += button.getAmt();

                // spawn worker thread to play chips in sounds
                SoundFactory.play(Effect.CHIPS_IN);

                // img of button that was pressed so we can render it
                // next to the betting circle
                Image img = button.getImage();

                int chipWidth = img.getWidth(null);

                // helper function to render chip to screen
                addChip(img, chipWidth);

                LOG.info("A. side bet amount " + button.getAmt() + " updated new amt = " + amt);
            }
        }

        // if the click was inside the side bet circle
        // and the amount didn't change, clear.
        if (oldAmt == amt && isInsideCircle(x, y)) {
            amt = 0;

            // spawn worker thread to play chips out sound
            SoundFactory.play(Effect.CHIPS_OUT);

            // side bet cleared, un-render the chips
            this.chips.clear();

            LOG.info("B. side bet amount cleared");
        }
    }

    /**
     * Informs view the game is over and it's time to update the bankroll for the hand.
     *
     * @param hid Hand id
     */
    @Override
    public void ending(Hid hid) {
        double bet = hid.getSideAmt();

        if (bet == 0)
            return;

        LOG.info("side bet outcome = " + bet);

        // Update the bankroll
        moneyManager.update(bet);

        LOG.info("new bankroll = " + moneyManager.getBankroll());
    }

    /**
     * Informs view the game is starting.
     */
    @Override
    public void starting() {
    }

    /**
     * Gets the side bet amount.
     *
     * @return Bet amount
     */
    @Override
    public Integer getAmt() {
        return amt;
    }

    /**
     * Updates the view.
     */
    @Override
    public void update() {
    }

    /**
     * Renders the view.
     *
     * @param g Graphics context
     */
    @Override
    public void render(Graphics2D g) {
        // Draw the at-stake place on the table
        g.setColor(Color.RED);
        g.setStroke(dashed);
        g.drawOval(X - DIAMETER / 2, Y - DIAMETER / 2, DIAMETER, DIAMETER);

        // Draw the at-stake amount
        g.setFont(font);
        g.setColor(Color.WHITE);

        // calculate the centered X and Y coordinate and use it to draw
        // the side bet amount. Code taken from AtSteakSprite
        String text = amt + "";
        FontMetrics fm = g.getFontMetrics(font);

        // because we drew the circle at the x and y coordinates / 2, we
        // don't need to account for it when calculating the position of the text.
        int x = X - fm.charsWidth(text.toCharArray(), 0, text.length()) / 2;
        int y = Y + fm.getHeight() / 4;

        g.drawString(text, x, y);

        for (Chip chip : this.chips) {
            chip.render(g);
        }
    }
}
