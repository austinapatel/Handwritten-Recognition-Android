/**Created by Austin Patel*/

/**
 * Code from http://stackoverflow.com/questions/16650419/draw-in-canvas-by-finger-android
 * for use in the "simple" drawing mode.
 * Modified by Austin Patel
 */

package austinpatel.handwrittenletterrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.view.MotionEvent;
import android.view.View;

import austinpatel.handwrittenletterrecognition.data.Constants;

/**View for drawing in both simple and advanced modes with functionality for converting
 * the drawn image to a certain grid size that can be used as input into the neural
 * network.*/
public class DrawingView extends View {

    private static final int LINE_WIDTH = 2;
    private static final float TOUCH_TOLERANCE = 4;

    private float touchX, touchY;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint, mPaint, gridPaint;
    private boolean validTouch;
    private boolean advancedMode;
    private int[][] fill;
    private int bitmapW, bitmapH;
    private Rect gridRect;
    private int sideMargin, topMargin;
    private double lineSpacing;
    private MainActivity mainActivity;
    private Rect squareFillRect;

    public DrawingView(Context c, Paint mPaint, MainActivity mainActivity) {
        super(c);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        advancedMode = false;

        this.mainActivity = mainActivity;

        gridRect = new Rect();
        squareFillRect = new Rect();

        gridPaint = new Paint() {{
            setAntiAlias(true);
            setDither(true);
            setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeCap(Paint.Cap.ROUND);
            setStrokeWidth(LINE_WIDTH);
        }};

        fill = new int[Constants.GRID_WIDTH][Constants.GRID_HEIGHT];

        topMargin = 200;

        gridPaint.setStyle(Paint.Style.FILL);

        this.mPaint = mPaint;
    }

    /**Re-sizes the bitmap and the canvas if the activity changes size.*/
    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        super.onSizeChanged(w, h, old_w, old_h);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        bitmapW = w;
        bitmapH = h;
    }

    /**Draws the grid, border and user drawing.*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        sideMargin = getWidth() / 10;
        lineSpacing = (getWidth() - sideMargin * 2) / (double) (Constants.GRID_WIDTH);

        if (!advancedMode) { // Simple Mode
            gridPaint.setStyle(Paint.Style.STROKE);

            // Draw the grid border
            gridRect.set(sideMargin, topMargin,
                    getWidth() - sideMargin,
                    (int) (topMargin + lineSpacing * Constants.GRID_HEIGHT));

            canvas.drawRect(gridRect, gridPaint);

            canvas.drawPath(mPath,  mPaint);

            // Draw the final line
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        } else { // Advanced Mode
            gridPaint.setStyle(Paint.Style.FILL);

            // Draw the grid
            for (int x = 0; x <= Constants.GRID_WIDTH; x++) {
                int x1, y1, x2, y2;

                // Vertical Lines
                x1 = (int) (sideMargin + x * lineSpacing);
                y1 = topMargin;
                x2 = x1;
                y2 = (int) (topMargin + Constants.GRID_HEIGHT * lineSpacing);
                canvas.drawLine(x1, y1, x2, y2, gridPaint);

                // Horizontal Lines
                x1 = sideMargin;
                y1 = (int) (x * lineSpacing + topMargin);
                x2 = (int) (sideMargin + Constants.GRID_WIDTH * lineSpacing);
                y2 = y1;
                canvas.drawLine(x1, y1, x2, y2, gridPaint);
            }

            // Draw the square fillings
            for (int x = 0; x < fill.length; x++) {
                for (int y = 0; y < fill[x].length; y++) {
                    if (fill[x][y] == 1) {
                        int left = (int) (sideMargin + x * lineSpacing);
                        int top = (int) (topMargin + y * lineSpacing);
                        int right = (int) (left + lineSpacing);
                        int bottom = (int) (top + lineSpacing);

                        squareFillRect.set(left, top, right, bottom);
                        canvas.drawRect(squareFillRect, gridPaint);
                    }
                }
            }
        }
    }


    /**Resets the drawing path when the user starts drawing.*/
    private void touch_start(float x, float y) {
        if (x < sideMargin ||
                x > getWidth() - sideMargin ||
                y < topMargin ||
                y > topMargin + (lineSpacing + 1) * Constants.GRID_HEIGHT)
            return;
        mPath.reset();
        mPath.moveTo(x, y);
        touchX = x;
        touchY = y;
        validTouch = true;
    }

    /**Handles if the user drags while drawing.*/
    private void touch_move(float x, float y) {
        // Check if drawing is outside of grid
//        if (x < sideMargin ||
//                x > getWidth() - sideMargin ||
//                y < topMargin ||
//                y > topMargin + (lineSpacing + 1) * Constants.GRID_HEIGHT)
//            validTouch = false;

        float dx = Math.abs(x - touchX);
        float dy = Math.abs(y - touchY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if (validTouch)
                mPath.quadTo(touchX, touchY, (x + touchX) / 2, (y + touchY) / 2);
            touchX = x;
            touchY = y;

            if (validTouch) { // WHY SUBTRACT -2 ???????????
                int xPos = (int) ((touchX - lineSpacing) / lineSpacing - 2);
                int yPos = (int) ((touchY - topMargin) / lineSpacing);

                if (yPos >= Constants.GRID_HEIGHT)
                    yPos = Constants.GRID_HEIGHT - 1;

                if (xPos >= 0 && yPos >= 0 && fill[xPos][yPos] == 0) {
                    fill[xPos][yPos] = 1;
                    mainActivity.updateNeuralNetworkOutput(fill);
                }

            }
        }
    }

    /**Switch from simple to advanced modes.*/
    public void changeMode() {
        advancedMode = !advancedMode;
        invalidate();
    }

    /**Handles when the user releases the touch.*/
    private void touch_up() {
        if (validTouch)
            mPath.lineTo(touchX, touchY);

        // commit the path to our offscreen
        if (validTouch)
            mCanvas.drawPath(mPath,  mPaint);

        // kill this so we don't double draw
        mPath.reset();

        validTouch = false;
    }

    /**Resets the drawing to a blank one.*/
    public void clear() {
        fill = new int[Constants.GRID_WIDTH][Constants.GRID_HEIGHT];

        mBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mBitmap);

        invalidate();
    }

    /**Handles touch events on the drawing grid.*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        return true;
    }
}
