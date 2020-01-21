package evan.wang.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import evan.wang.R;

public class InnerView extends View {

    private boolean canMove = false;
    private View outSideView = null;
    private float width;
    private float height;
    private float borderWidth;
    private int borderColor;

    private Paint paint;
    private Path path;
    private RectF rectF = new RectF();
    private Point pointLeftTop = null;
    private Point pointRightTop = null;
    private Point pointLeftBottom = null;
    private Point pointRightBottm = null;
    private boolean hasInit = false;
    private InnerViewBuilder innerViewBuilder;
    public static final int DEFAULT_COLOR = R.color.common_red_color;
    public static final int DEFAULT_BORDER = 10;

    public InnerView(Context context, InnerViewBuilder innerViewBuilder) {
        super(context);
        this.innerViewBuilder = innerViewBuilder;
        initView(null, innerViewBuilder);
    }

    public InnerView(Context context) {
        super(context);
    }

    public InnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, null);
    }

    public InnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (outSideView != null && hasInit){
            path.reset();
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = width;
            rectF.bottom = height;
            path.addRect(rectF, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth((int) width), measureHeight((int) height));

    }

    private int measureWidth(int measureSpec){
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //设置一个默认值，就是这个View的默认宽度为500，这个看我们自定义View的要求
        int result = (int) width;
        if (specMode == MeasureSpec.AT_MOST) {//相当于我们设置为wrap_content
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {//相当于我们设置为match_parent或者为一个具体的值
            result = specSize;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = (int) height;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * java代码，构建者模式进来
     * @param innerViewBuilder
     */
    private void initBuilderView(InnerViewBuilder innerViewBuilder){
        if (innerViewBuilder != null){
            canMove = innerViewBuilder.canMove;
            outSideView = innerViewBuilder.outSideView;
            width = innerViewBuilder.width;
            height = innerViewBuilder.height;
            borderWidth = innerViewBuilder.borderWidth;
            borderColor = innerViewBuilder.borderColor;
        }
    }

    /**
     *
     * @param attrs
     */
    private void initView(AttributeSet attrs, InnerViewBuilder innerViewBuilder){
        //从xml进来
        if (attrs != null && getContext() != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.InnerView);
            if (array == null){
                return;
            }
            canMove = array.getBoolean(R.styleable.InnerView_can_move, false);
            int outSideId = array.getResourceId(R.styleable.InnerView_out_side_view, 0);
            if (outSideId > 0){
                outSideView = (ViewGroup) View.inflate(getContext(), outSideId, null);
            }
            width = array.getDimension(R.styleable.InnerView_inner_width, 100);
            height = array.getDimension(R.styleable.InnerView_inner_height, 100);
            borderWidth = array.getDimension(R.styleable.InnerView_inner_border_width, 2);
            borderColor = array.getColor(R.styleable.InnerView_inner_border_width, (DEFAULT_COLOR));
            array.recycle();
            initPaint();
            initSize();
        }else if (innerViewBuilder != null){
            initBuilderView(innerViewBuilder);
            initPaint();
            initSize();
        }

    }

    private void initPaint(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(borderColor));
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    private void initSize(){
        if (outSideView != null){
            int parentWidth = outSideView.getWidth();
            int parentHeight = outSideView.getHeight();
            pointLeftTop = new Point((int) ((parentWidth - width) / 2), 0);
            pointRightTop = new Point((int) ((parentWidth + width) / 2), 0);
            pointLeftBottom = new Point(0, (int) height);
            pointRightBottm = new Point((int) ((parentWidth - width) / 2), (int) height);
            hasInit = true;
        }
    }

    public static class InnerViewBuilder{
        private Context context;
        private boolean canMove = false;
        private View outSideView = null;
        private float width;
        private float height;
        private float borderWidth;
        private int borderColor;

        public InnerViewBuilder(Context context) {
            this.context = context;
        }

        public InnerViewBuilder setCanMove(boolean canMove) {
            this.canMove = canMove;
            return this;
        }

        public InnerViewBuilder setOutSideView(View outSideView) {
            this.outSideView = outSideView;
            return this;
        }

        public InnerViewBuilder setWidth(float width) {
            this.width = width;
            return this;
        }

        public InnerViewBuilder setHeight(float height) {
            this.height = height;
            return this;
        }

        public InnerViewBuilder setBorderWidth(float borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public InnerViewBuilder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public InnerView create(){
            if (this.width == 0){
                this.width = 100;
            }
            if (this.height == 0){
                this.height = 100;
            }
            if (this.borderColor <= 0){
                this.borderColor = DEFAULT_COLOR;
            }
            if (this.borderWidth <= 0){
                this.borderWidth = DEFAULT_BORDER;
            }
            InnerView innerView = new InnerView(this.context, this);
            ((RelativeLayout)outSideView.getParent()).addView(innerView);
            return innerView;
        }

    }

}
