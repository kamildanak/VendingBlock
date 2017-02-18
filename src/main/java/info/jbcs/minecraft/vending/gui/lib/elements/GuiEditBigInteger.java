package info.jbcs.minecraft.vending.gui.lib.elements;

import info.jbcs.minecraft.vending.gui.lib.input.InputKeyboardEvent;

import java.awt.event.KeyEvent;
import java.math.BigInteger;

public class GuiEditBigInteger extends GuiEdit {
    private BigInteger value = BigInteger.valueOf(0);
    private BigInteger min;
    private BigInteger max;

    public GuiEditBigInteger(int x, int y, int w, int h, BigInteger min, BigInteger max) {
        super(x, y, w, h);
        this.min = min;
        this.max = max;
    }

    @Override
    public void onAdded() {
        super.onAdded();
        setText(this.value.toString());
    }

    public BigInteger getValue() {
        return this.value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
        super.setText(value.toString());
    }

    @Override
    public void keyPressed(InputKeyboardEvent ev) {
        if (!field.isFocused()) return;
        if (Character.isDigit(ev.character)) {
            value = value.multiply(BigInteger.valueOf(10))
                    .add(BigInteger.valueOf(Character.getNumericValue(ev.character)));
        } else if (ev.character == '-') {
            value = value.negate();
        } else if (ev.character == KeyEvent.VK_BACK_SPACE) {
            value = value.divide(BigInteger.valueOf(10));
        }
        if (value.compareTo(min) < 0) value = min;
        if (value.compareTo(max) > 0) value = max;
        super.setText(value.toString());


        ev.handled = true;

    }

    @Override
    public int getHeight() {
        if (hidden) return 0;
        return h;
    }

    @Override
    public int getWidth() {
        if (hidden) return 0;
        return w;
    }
}
