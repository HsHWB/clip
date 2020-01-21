package evan.wang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import evan.wang.R;

/**
 * 头像上传裁剪框
 */
public class ClipView extends View {
    private Paint paint = new Paint();
    //画裁剪区域边框的画笔
    private Paint borderPaint = new Paint();
    //裁剪框水平方向间距
    private float mHorizontalPadding;
    //裁剪框边框宽度
    private int clipBorderWidth;
    //裁剪圆框的半径
    private int clipRadiusWidth;
    private InnerView innerView;

    private Rect clipRect = new Rect();

    //裁剪框矩形宽度
    private int clipWidth;
    //裁剪框类别，（圆形、矩形），默认为圆形
    private ClipType clipType = ClipType.CIRCLE;
    private Xfermode xfermode;


    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //去锯齿
        paint.setAntiAlias(true);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(clipBorderWidth);
        borderPaint.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if (innerView == null){
                    innerView = new InnerView.InnerViewBuilder(getContext())
                            .setCanMove(false)
                            .setOutSideView(ClipView.this)
                            .setWidth(getClipRect().height())
                            .setHeight(getClipRect().height())
                            .setBorderWidth(5)
                            .setBorderColor(R.color.common_red_color)
                            .create();
                    Log.d("ClipView", "onGlobalLayout innerView");
                }
                ClipView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        //通过Xfermode的DST_OUT来产生中间的透明裁剪区域，一定要另起一个Layer（层）
        canvas.saveLayer(0, 0, this.getWidth(), this.getHeight(), null, LAYER_FLAGS);
        //设置背景
        canvas.drawColor(Color.parseColor("#a8000000"));
//        canvas.drawColor(getResources().getColor(R.color.colorAccent));
        paint.setXfermode(xfermode);
        //绘制圆形裁剪框
        if (clipType == ClipType.CIRCLE) {
            //中间的透明的圆
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, clipRadiusWidth, paint);
            //白色的圆边框
            canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, clipRadiusWidth, borderPaint);
        } else if (clipType == ClipType.RECTANGLE) { //绘制矩形裁剪框
            //绘制中间的矩形
            int beginX = 0;
            int endX = this.getWidth();
            int rectangleWidth = this.getWidth();
            int rectangleHeight = 9 * rectangleWidth / 16;
            int beginY = (this.getHeight() - rectangleHeight) / 2;
            Log.d("ClipView", "ClipView onDraw beginX : " + beginX + "     endX : " + endX +
                    "      rectangleWidth : " + rectangleWidth + "      rectangleHeight : " + rectangleHeight +
                    "      beginY : " + beginY + "      this.getHeight() : " + this.getHeight() + "      clipWidth : " + clipWidth);
            canvas.drawRect(mHorizontalPadding, beginY,
                    rectangleWidth - mHorizontalPadding, beginY + rectangleHeight , paint);
            //绘制白色的矩形边框
            clipRect.set((int)mHorizontalPadding, beginY,
                    (int)(rectangleWidth - mHorizontalPadding), beginY + rectangleHeight);
            canvas.drawRect(clipRect, borderPaint);
        }
        //出栈，恢复到之前的图层，意味着新建的图层会被删除，新建图层上的内容会被绘制到canvas (or the previous layer)
        canvas.restore();
    }

    /**
     * 获取裁剪区域的Rect
     *
     * @return
     */
    public Rect getClipRect() {
        Rect rect = new Rect();
//        //宽度的一半 - 圆的半径
//        rect.left = (this.getWidth() / 2 - clipRadiusWidth);
//        //宽度的一半 + 圆的半径
//        rect.right = (this.getWidth() / 2 + clipRadiusWidth);
//        //高度的一半 - 圆的半径
//        rect.top = (this.getHeight() / 2 - clipRadiusWidth);
//        //高度的一半 + 圆的半径
//        rect.bottom = (this.getHeight() / 2 + clipRadiusWidth);
        rect.left = 0;
        rect.right = this.getWidth();
        int rectangleHeight = 9 * this.getWidth() / 16;
        rect.top = (this.getHeight() - rectangleHeight) / 2;
        rect.bottom = this.getHeight() - rect.top;
        return rect;
    }

    /**
     * 设置裁剪框边框宽度
     *
     * @param clipBorderWidth
     */
    public void setClipBorderWidth(int clipBorderWidth) {
        this.clipBorderWidth = clipBorderWidth;
        borderPaint.setStrokeWidth(clipBorderWidth);
        invalidate();
    }

    /**
     * 设置裁剪框水平间距
     *
     * @param mHorizontalPadding
     */
    public void setmHorizontalPadding(float mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
        this.clipRadiusWidth = (int) (getScreenWidth(getContext()) - 2 * mHorizontalPadding) / 2;
        this.clipWidth = clipRadiusWidth * 2;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 设置裁剪框类别
     *
     * @param clipType
     */
    public void setClipType(ClipType clipType) {
        this.clipType = clipType;
    }

    /**
     * 裁剪框类别，圆形、矩形
     */
    public enum ClipType {
        CIRCLE, RECTANGLE
    }
}
