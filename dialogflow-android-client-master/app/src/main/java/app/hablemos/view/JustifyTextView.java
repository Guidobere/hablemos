package app.hablemos.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import app.hablemos.R;


public class JustifyTextView extends AppCompatTextView {

    private int mLineY;
    private int mViewWidth;
    private int justificarTextoDesde;
    private int escalarLineaDesde;
    private int completarLineaHasta;

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.justificarTextoDesde = Integer.parseInt(getContext().getString(R.string.justificarTextoDesde));
        this.escalarLineaDesde =  Integer.parseInt(getContext().getString(R.string.escalarLineaDesde));
        this.completarLineaHasta =  Integer.parseInt(getContext().getString(R.string.completarLineaHasta));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        String text = (String) getText();
        if(text.length()>justificarTextoDesde) {
            TextPaint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.drawableState = getDrawableState();
            mViewWidth = getMeasuredWidth();
            mLineY = 0;
            mLineY += getTextSize();
            Layout layout = getLayout();
            for (int i = 0; i < layout.getLineCount(); i++) {
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String line = text.substring(lineStart, lineEnd);

                float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                if (needScale(line)) {
                    if(line.length() < completarLineaHasta)
                        line = rightPad(line, completarLineaHasta, ' ');
                    drawScaledText(canvas, line, width);
                } else {
                    canvas.drawText(line, 0, mLineY, paint);
                }

                mLineY += getLineHeight();
            }
        } else super.onDraw(canvas);
    }

    private void drawScaledText(Canvas canvas, String line, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(3);
        }

        float d = (mViewWidth - lineWidth) / line.length() - 1;
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {
        if (line.length() < escalarLineaDesde) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    public static String repeat(char val, int count){
        StringBuilder buf = new StringBuilder(count);
        while (count-- > 0) {
            buf.append(val);
        }
        return buf.toString();
    }

    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (TextUtils.isEmpty(padStr)) {
            padStr = " ";
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }
}

