package xyz.mackan.wifilamp.effects;

public class BlinkEffect {
    public int[] blinkTo = {0, 0, 0};
    public int[] blinkFrom = {0, 0, 0};
    public int offTime, onTime, blinks;

    public final int EFFECT_TYPE = 1;

    public BlinkEffect(){
        this.offTime = 1000;
        this.onTime = 1000;
        this.blinks = 1;
    }

    public BlinkEffect setOffTime(int offTime) {
        this.offTime = offTime;
        return this;
    }

    public BlinkEffect setOnTime(int onTime) {
        this.onTime = onTime;
        return this;
    }

    public BlinkEffect setBlinks(int blinks) {
        this.blinks = blinks;
        return this;
    }

    public BlinkEffect setBlinkTo(int[] blinkTo) {
        this.blinkTo = blinkTo;
        return this;
    }

    public BlinkEffect setBlinkFrom(int[] blinkFrom) {
        this.blinkFrom = blinkFrom;
        return this;
    }

}
