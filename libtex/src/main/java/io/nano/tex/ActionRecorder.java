package io.nano.tex;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by nano on 18-11-9
 * <p>
 * To record the operations on {@link Graphics2D}.
 */
final class ActionRecorder {

    private static final String TAG = "ActionRecorder";

    private static final byte ACT_setFont = 0;
    private static final byte ACT_setColor = 1;
    private static final byte ACT_setStrokeWidth = 2;
    private static final byte ACT_setStroke = 3;
    private static final byte ACT_translate = 4;
    private static final byte ACT_scale = 5;
    private static final byte ACT_rotate = 6;
    private static final byte ACT_reset = 7;
    private static final byte ACT_drawChar = 8;
    private static final byte ACT_drawText = 9;
    private static final byte ACT_drawLine = 10;
    private static final byte ACT_drawRect = 11;
    private static final byte ACT_fillRect = 12;
    private static final byte ACT_drawRoundRect = 13;
    private static final byte ACT_fillRoundRect = 14;

    private static final class Action {
        byte action;
        Object arg;
        float[] args;

        Action(byte action, Object arg, float[] args) {
            this.action = action;
            this.arg = arg;
            this.args = args;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder
                .append("{ action: ")
                .append(action)
                .append(", arg: ")
                .append(arg)
                .append(", args: [");
            if (args == null) builder.append("null");
            else for (float x : args) builder.append(x).append(", ");
            builder.append("] }");
            return builder.toString();
        }
    }

    private List<Action> actions = new LinkedList<>();
    private Action origin = new Action(ACT_translate, null, new float[]{0, 0});

    ActionRecorder() {
        actions.add(origin);
    }

    void setPosition(float x, float y) {
        origin.args[0] = x;
        origin.args[1] = y;
    }

    void record(int action, Object arg, float[] args) {
        Action act = new Action((byte) action, arg, args);
        actions.add(act);
    }

    void play(Graphics2D g2) {
        for (Action act : actions) {
            if (BuildConfig.DEBUG) Log.d(TAG, act.toString());
            play(g2, act);
        }
    }

    private void play(Graphics2D g2, Action act) {
        if (act == null) return;
        switch (act.action) {
            case ACT_setFont:
                g2.setFont((Font) act.arg);
                break;
            case ACT_setColor:
                g2.setColor((int) act.args[0]);
                break;
            case ACT_setStrokeWidth:
                g2.setStrokeWidth(act.args[0]);
                break;
            case ACT_setStroke:
                g2.setStroke(act.args[0], act.args[1], (int) act.args[2], (int) act.args[3]);
                break;
            case ACT_translate:
                g2.translate(act.args[0], act.args[1]);
                break;
            case ACT_scale:
                g2.scale(act.args[0], act.args[1]);
                break;
            case ACT_rotate:
                g2.rotate(act.args[0], act.args[1], act.args[2]);
                break;
            case ACT_reset:
                g2.reset();
                break;
            case ACT_drawChar:
                g2.drawChar((char) act.args[0], act.args[1], act.args[2]);
                break;
            case ACT_drawText:
                g2.drawText((String) act.arg, act.args[0], act.args[1]);
                break;
            case ACT_drawLine:
                g2.drawLine(act.args[0], act.args[1], act.args[2], act.args[3]);
                break;
            case ACT_drawRect:
                g2.drawRect(act.args[0], act.args[1], act.args[2], act.args[3]);
                break;
            case ACT_fillRect:
                g2.fillRect(act.args[0], act.args[1], act.args[2], act.args[3]);
                break;
            case ACT_drawRoundRect:
                g2.drawRoundRect(
                    act.args[0], act.args[1], act.args[2], act.args[3], act.args[4], act.args[5]);
                break;
            case ACT_fillRoundRect:
                g2.fillRoundRect(
                    act.args[0], act.args[1], act.args[2], act.args[3], act.args[4], act.args[5]);
                break;
            default:
                break;
        }
    }
}
