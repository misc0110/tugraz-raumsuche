package com.team4win.tugroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {

    // matrices for zooming and dragging
    Matrix matrix = new Matrix();
    Matrix saved_matrix = new Matrix();

    // states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // points for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float old_dist = 1f;

    int display_width = 0;
    int display_height = 0;

    Bitmap bmp = null;
    PointF img_pos = new PointF();

    Context context;

    public ZoomImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
	super.setClickable(true);
	this.context = context;

	matrix.setTranslate(1f, 1f);
	setImageMatrix(matrix);
	setScaleType(ScaleType.MATRIX);
	img_pos.set(0, 0);

	setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		    saved_matrix.set(matrix);
		    start.set(event.getX(), event.getY());
		    mode = DRAG;
		    break;
		case MotionEvent.ACTION_POINTER_DOWN:
		    old_dist = spacing(event);
		    if (old_dist > 10f) {
			saved_matrix.set(matrix);
			midPoint(mid, event);
			mode = ZOOM;
		    }
		    break;
		case MotionEvent.ACTION_UP:
		    int x_diff = (int) Math.abs(event.getX() - start.x);
		    int y_diff = (int) Math.abs(event.getY() - start.y);
		    if (x_diff < 8 && y_diff < 8) {
			performClick();
		    }
		case MotionEvent.ACTION_POINTER_UP:
		    mode = NONE;
		    break;
		case MotionEvent.ACTION_MOVE:
		    if (mode == DRAG) {
			matrix.set(saved_matrix);
			matrix.postTranslate(event.getX() - start.x,
				event.getY() - start.y);
			img_pos.set(img_pos.x + event.getX() - start.x,
				img_pos.y + event.getY() - start.y);
		    } else if (mode == ZOOM) {
			float new_dist = spacing(event);
			if (new_dist > 10f) {
			    matrix.set(saved_matrix);
			    float scale = new_dist / old_dist;
			    matrix.postScale(scale, scale, mid.x, mid.y);
			}
		    }
		    break;
		}

		setImageMatrix(matrix);
		return true;
	    }

	});

    }

    public void setImage(Bitmap bitmap) {
	super.setImageBitmap(bitmap);
	bmp = bitmap;
	if (bitmap == null)
	    return;

	fitToScreen(bitmap.getWidth(), bitmap.getHeight());
    }

    private void fitToScreen(int width, int height) {
	// fit to screen
	float scale;
	if ((display_height / height) >= (display_width / width)) {
	    scale = (float) display_width / (float) width;
	} else {
	    scale = (float) display_height / (float) height;
	}

	// reset matrix
	matrix.set(null);
	// reset mid point
	mid.set(0, 0);

	// scale image
	saved_matrix.set(matrix);
	matrix.set(saved_matrix);
	matrix.postScale(scale, scale, mid.x, mid.y);
	setImageMatrix(matrix);

	// center the image
	float remaining_y_space = (float) display_height
		- (scale * (float) height);
	float remaining_x_space = (float) display_width
		- (scale * (float) width);

	remaining_y_space /= 2.f;
	remaining_x_space /= 2.f;

	// translate image
	saved_matrix.set(matrix);
	matrix.set(saved_matrix);
	matrix.postTranslate(remaining_x_space, remaining_y_space);
	setImageMatrix(matrix);
	img_pos.set(remaining_x_space, remaining_y_space);
    }

    public float getImageX() {
	return img_pos.x;
    }

    public float getImageY() {
	return img_pos.y;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// reset bitmap
	boolean reset_bmp = false;
	if ((display_width == 0 || display_height == 0) && bmp != null)
	    reset_bmp = true;

	display_width = MeasureSpec.getSize(widthMeasureSpec);
	display_height = MeasureSpec.getSize(heightMeasureSpec);

	if (reset_bmp)
	    setImage(bmp);
    }

    private float spacing(MotionEvent event) {
	float x = event.getX(0) - event.getX(1);
	float y = event.getY(0) - event.getY(1);
	return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
	float x = event.getX(0) + event.getX(1);
	float y = event.getY(0) + event.getY(1);
	point.set(x / 2, y / 2);
    }

}
