package com.bgeiotdev.eval;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class GameView extends View implements GestureDetector.OnGestureListener {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GameBoard gameBoard = GameBoard.getGameBoard(GameLevel.MEDIUM);

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;
    private float buttonWidth;
    private float buttonRadius;
    private float buttonMargin;

    private Bitmap eraserBitmap;
    private Bitmap pencilBitmap;
    private Bitmap littlePencilBitmap;

    private GestureDetector gestureDetector;

    // ... other attributes ...
    public GameView(Context context) {
        super(context);
        this.init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {

        gestureDetector = new GestureDetector(getContext(), this);
    }
    // ... onSizeChanged, onDraw, ...

    // --- Events handlers ---

    // Override from View
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gestureDetector.onTouchEvent(event);
    }

    // Override from OnGestureDectector
    @Override
    public boolean onDown(MotionEvent e) {

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        RectF rectF;

        // --- Check grid cell click ---
        if (e.getY() < gridWidth) {
            int cellX = (int) (e.getX() / cellWidth);
            int cellY = (int) (e.getY() / cellWidth);

            gameBoard.currentCellX = cellX;
            gameBoard.currentCellY = cellY;
            postInvalidate();
            return true;
        }

        float buttonLeft = buttonMargin;
        float buttonTop = 9 * cellWidth + gridSeparatorSize / 2;

        if (gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1) {

            // --- Check number buttons ---
            for (int i = 1; i <= 9; i++) {
                rectF = new RectF(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth);
                if (rectF.contains(e.getX(), e.getY())) {
                    gameBoard.pushValue(i);
                    postInvalidate();
                    return true;
                }

                if (i != 6) {
                    buttonLeft += buttonWidth + buttonMargin;
                } else {
                    buttonLeft = buttonMargin;
                    buttonTop += buttonWidth + buttonMargin;
                }
            }

            // --- eraser button ---
            rectF = new RectF(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth);
            if (rectF.contains(e.getX(), e.getY())) {
                gameBoard.clearCell();
                this.invalidate();
                return true;
            }
            buttonLeft += buttonWidth + buttonMargin;
        }

        // --- pencil button ---
        rectF = new RectF(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth);
        if (rectF.contains(e.getX(), e.getY())) {
            gameBoard.bigNumber = !gameBoard.bigNumber;
            this.invalidate();
            return true;
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.RED);

        // --- Draw cells ---

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellWidth * 0.7f);


        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int backgroundColor = Color.WHITE;

                // Highlight the current row, current column and the current block
                // A value can be appeared only one time into all highlighted cells.
                if (gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1) {
                    if ((x / 3 == gameBoard.currentCellX / 3 && y / 3 == gameBoard.currentCellY / 3) ||
                            (x == gameBoard.currentCellX && y != gameBoard.currentCellY) ||
                            (x != gameBoard.currentCellX && y == gameBoard.currentCellY)) {
                        backgroundColor = 0xFF_FF_F0_F0;
                    }
                }

                // Check if cell is initially proposed: in this case, the background is grey
                if (gameBoard.cells[y][x].isInitial) {
                    if (backgroundColor == 0xFF_FF_F0_F0) {
                        backgroundColor = 0xFF_F4_F0_F0;
                    } else {
                        backgroundColor = 0xFF_F0_F0_F0;
                    }
                }

                // Change the color for the currently selected value
                if (gameBoard.getSelectedValue() > 0 &&
                        gameBoard.cells[y][x].assumedValue == gameBoard.getSelectedValue()) {
                    backgroundColor = 0xFF_C7_DA_F8;
                }

                // Display errors (conflicts) in red color: an error appear if a value is present
                // at least two times in the same line, column or block.
                if (gameBoard.cells[y][x].assumedValue > 0) {
                    for (int tx = 0; tx < 9; tx++) {
                        if (tx != x &&
                                gameBoard.cells[y][tx].assumedValue == gameBoard.cells[y][x].assumedValue) {
                            backgroundColor = 0xFF_FF_00_00;
                            break;
                        }
                    }
                    if (backgroundColor != 0xFF_FF_00_00) {
                        for (int ty = 0; ty < 9; ty++) {
                            if (ty != y &&
                                    gameBoard.cells[ty][x].assumedValue == gameBoard.cells[y][x].assumedValue) {
                                backgroundColor = 0xFF_FF_00_00;
                                break;
                            }
                        }
                    }
                    if (backgroundColor != 0xFF_FF_00_00) {
                        int bx = x / 3;
                        int by = y / 3;
                        for (int dy = 0; dy < 3; dy++) {
                            for (int dx = 0; dx < 3; dx++) {
                                int tx = bx * 3 + dx;
                                int ty = by * 3 + dy;
                                if (tx != x && ty != y &&
                                        gameBoard.cells[ty][tx].assumedValue == gameBoard.cells[y][x].assumedValue) {
                                    backgroundColor = 0xFF_FF_00_00;
                                    break;
                                }
                            }
                        }
                    }
                }

                // Draw the background for the current cell
                paint.setColor(backgroundColor);
                canvas.drawRect(x * cellWidth,
                        y * cellWidth,
                        (x + 1) * cellWidth,
                        (y + 1) * cellWidth,
                        paint);

                if (gameBoard.cells[y][x].assumedValue != 0) {

                    // Draw the assumed value for the cell.
                    paint.setColor(0xFF000000);
                    paint.setTextSize(cellWidth * 0.7f);
                    canvas.drawText("" + gameBoard.cells[y][x].assumedValue,
                            x * cellWidth + cellWidth / 2,
                            y * cellWidth + cellWidth * 0.75f, paint);

                } else {

                    // Draw each mark if exists
                    paint.setTextSize(cellWidth * 0.33f);
                    if (gameBoard.cells[y][x].marks[0]) {
                        paint.setColor(gameBoard.getSelectedValue() == 1 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("1",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[1]) {
                        paint.setColor(gameBoard.getSelectedValue() == 2 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("2",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[2]) {
                        paint.setColor(gameBoard.getSelectedValue() == 3 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("3",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[3]) {
                        paint.setColor(gameBoard.getSelectedValue() == 4 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("4",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[4]) {
                        paint.setColor(gameBoard.getSelectedValue() == 5 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("5",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[5]) {
                        paint.setColor(gameBoard.getSelectedValue() == 6 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("6",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[6]) {
                        paint.setColor(gameBoard.getSelectedValue() == 7 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("7",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[7]) {
                        paint.setColor(gameBoard.getSelectedValue() == 8 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("8",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                    if (gameBoard.cells[y][x].marks[8]) {
                        paint.setColor(gameBoard.getSelectedValue() == 9 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("9",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                }
            }
        }
    }
}

