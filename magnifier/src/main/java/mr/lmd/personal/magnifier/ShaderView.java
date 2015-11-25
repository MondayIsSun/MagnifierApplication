package mr.lmd.personal.magnifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.MotionEvent;
import android.view.View;

/**
 * 放大镜实现步骤：
 * <p/>
 * 1、获取到要放大的背景图片，生成改图片放大的着色器
 * <p/>
 * 2、生成放大镜，设置放大镜的形状、放大的区域大小和着色器
 * <p/>
 * 3、实现放大镜触摸移动监听，并重新绘制放大镜位置
 *
 * @author LinMingDao
 */

public class ShaderView extends View {

    private final Bitmap mBitmap;//待放大的背景
    private BitmapShader mShader;//着色器
    private final ShapeDrawable mDrawable;

    private static final int RADIUS = 100;// 放大镜的大小控制
    private static final int FACTOR = 2;// 放大倍数控制

    private final Matrix mMatrix = new Matrix();

    public ShaderView(Context context) {
        super(context);

        //1.1、初始化待放大的背景图片
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.code_bg);
        mBitmap = bmp;

        //1.2、初始化shader：着色器 ——> 处理放大镜放大的效果 ——> Bitmap.createScaledBitmap()
        mShader = new BitmapShader(
                Bitmap.createScaledBitmap(bmp, bmp.getWidth() * FACTOR, bmp.getHeight() * FACTOR, true),
                TileMode.CLAMP, TileMode.CLAMP);

        //2、ShapeDrawable：绘制放大镜——>实际上就是一个绘制的区域啦——>我们要对绘制的区域进行放大处理
        mDrawable = new ShapeDrawable(new RectShape());//mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setShader(mShader);//设置绘制区域图片的效果
        mDrawable.setBounds(0, 0, RADIUS * 1, RADIUS * 1);//设置一开始时绘制区域的大小（bound：边界）
    }

    //3、实现放大镜触摸移动监听，并重新绘制放大镜位置
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        //画shader的起始位置
        mMatrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR);
        mDrawable.getPaint().getShader().setLocalMatrix(mMatrix);

        //手指点击屏幕的时候重新绘制放大镜的区域
        mDrawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);

        invalidate();

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //设置画布的背景
        canvas.drawBitmap(mBitmap, 0, 0, null);

        //画放大镜
        mDrawable.draw(canvas);
    }
}
