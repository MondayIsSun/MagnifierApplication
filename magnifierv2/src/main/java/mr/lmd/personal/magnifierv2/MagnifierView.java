package mr.lmd.personal.magnifierv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * 放大镜
 * Created by LinMingDao on 2015/11/24.
 */
public class MagnifierView extends View {

    public enum Style {
        RECT, CIRCLE
    }

    private Style mStyle = Style.CIRCLE;

    public void setMagnifierStyle(Style style) {
        this.mStyle = style;
    }

    private Path mPath = new Path();
    private Matrix matrix = new Matrix();
    private Bitmap bitmap;
    private static final int RADIUS = 200;
    private static final float FACTOR = 1.7f;
    private int mCurrentX, mCurrentY;

    private Rect mRect = new Rect();
    private Rect mScaleOriginalRect = new Rect();
    private Rect mScaleRect = new Rect();

    private boolean mHadOnSizeChange;

    public MagnifierView(Context context, Style style) {
        super(context);
        if (Style.CIRCLE == style) {
            mPath.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
        } else if (Style.RECT == style) {
            mPath.addRect(mCurrentX - RADIUS, mCurrentY - RADIUS, RADIUS + mCurrentX, RADIUS + mCurrentY, Path.Direction.CW);
        }
        matrix.setScale(FACTOR, FACTOR);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_wait);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurrentX = (int) event.getX();
        mCurrentY = (int) event.getY();

        mPath.rewind();
        if (mStyle == Style.CIRCLE) {
            mPath.addCircle(mCurrentX, mCurrentY, RADIUS, Path.Direction.CW);
        } else if (mStyle == Style.RECT) {
            mPath.addRect(mCurrentX - RADIUS, mCurrentY - RADIUS, RADIUS + mCurrentX, RADIUS + mCurrentY, Path.Direction.CW);
        }

        mScaleRect.left = (int) (mScaleOriginalRect.left - mCurrentX * (FACTOR - 1));
        mScaleRect.top = (int) (mScaleOriginalRect.top - mCurrentY * (FACTOR - 1));
        mScaleRect.right = (int) (mScaleOriginalRect.right - mCurrentX * (FACTOR - 1));
        mScaleRect.bottom = (int) (mScaleOriginalRect.bottom - mCurrentY * (FACTOR - 1));

        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (!mHadOnSizeChange && w > 0 && h > 0) {
            mHadOnSizeChange = true;

            mRect.left = 0;
            mRect.top = 0;
            mRect.right = w;
            mRect.bottom = h;

            mScaleRect.left = 0;
            mScaleRect.top = 0;
            mScaleRect.right = (int) (w * FACTOR);
            mScaleRect.bottom = (int) (h * FACTOR);

            mScaleOriginalRect = new Rect(mScaleRect);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, null, mRect, null);
        canvas.clipPath(mPath);
        canvas.drawBitmap(bitmap, null, mScaleRect, null);
    }
}