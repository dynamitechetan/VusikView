package dyanamitechetan.vusikview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;


public class VusikView extends View {

    private FallNotesThread mFallNotesThread;
    private Notes[] mNotes;
    private int mNotesIconHeight;
    private int mNotesViewWidth;
    private int mNotesViewHeight;
    private Drawable[] images;
    private Random mRandom;
    private int[] myImageList;
    private float mFallSpeed;
    private int mNotesCount;

    private enum FallNotesState {START, PAUSE, RUNNING, STOP}

    private FallNotesState mFallNotesState;


    public VusikView(Context context) {
        this(context, null);
    }

    public VusikView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VusikView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initRandom();
    }
    public VusikView setImages(int[] imageArray){
        myImageList = imageArray;
        if (myImageList != null) {
            images = new Drawable[myImageList.length];
            for (int i = 0; i < myImageList.length; i++) {
                images[i] = resize(ContextCompat.getDrawable(getContext(), myImageList[i]));
            }
        }
    return this;
    }

    public VusikView start(){

            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFallNotesState = FallNotesState.START;

                    if (mFallNotesThread == null) {
                        mFallNotesThread = new FallNotesThread();
                    }

                    mNotes = new Notes[mNotesCount];
                    for (int i = 0; i < mNotes.length; i++) {
                        mNotes[i] = new Notes();
                    }

                    if (mFallNotesState == FallNotesState.START) {
                        if(!mFallNotesThread.isAlive()) {
                            mFallNotesThread.start();
                        }
                        mFallNotesState = FallNotesState.RUNNING;

                    }
                }
            }, 1000);
        return this;
    }
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VusikAttr);
        mFallSpeed = typedArray.getFloat(R.styleable.VusikAttr_fallSpeed, (float) 0.1);
        mNotesCount = typedArray.getInteger(R.styleable.VusikAttr_fallCount, 25);
        typedArray.recycle();
        checkAttrs();
    }

    private void checkAttrs() {
        if (mNotesCount <= 0) {
            throw new RuntimeException("notes count must be > 0");
        }

        if (mFallSpeed <= 0) {
            throw new RuntimeException("fall speed must be > 0");
        }
    }

    public void initRandom() {
        mRandom = new Random();
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mNotesViewWidth = r - l;
        mNotesViewHeight = b - t;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mNotes == null) {
            return;
        }

        for (Notes notes : mNotes) {
            canvas.save();
            notes.mNotesIconDrawable.setBounds((int) notes.X, (int) notes.Y, (int) (notes.X + mNotesIconHeight * notes.scale), (int) (notes.Y + mNotesIconHeight * notes.scale));
            notes.mNotesIconDrawable.setAlpha(notes.alpha);
            notes.mNotesIconDrawable.draw(canvas);
            canvas.restore();
        }

    }


    private void calculateNotesNextAttr() {
        for (Notes notes : mNotes) {
            notes.calculateNextStep();
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

     /**
        Resize every drawable to same size, else the size wil flicker randomly
     */
    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }


    private class Notes {
        public float scale;
        public int alpha; 
        public float X;
        public float Y;
        private Drawable mNotesIconDrawable;

        public Notes() {
            init();
        }

        private void init() {
            
            /**
            TO INITIATE myImageList WITH SOME VALUES IF NONE WAS PROVIDED
            */
            if(myImageList==null){
                         myImageList = new int[]{R.drawable.note1, R.drawable.note2};
                images = new Drawable[myImageList.length];

                for(int i=0;i<myImageList.length;i++){

                            images[i] = resize(ContextCompat.getDrawable(getContext(), myImageList[i]));
                        }

            }

            alpha = mRandom.nextInt(200) + 55; //get a random Aplha 55~255
            scale = (mRandom.nextFloat() + 1) / 2;  //random Sizes 0.5~1.5
            X = mRandom.nextInt(mNotesViewWidth);
            Y = randInt(-mNotesIconHeight-2000,0);
            
            if(images.length==1){
                mNotesIconDrawable = images[0];
            }
            else if(images.length==2) {
                if (randInt(1, 100) % 2 == 0) {
                    mNotesIconDrawable = images[0];

                } else
                    mNotesIconDrawable = images[1];
            }
            else {
                mNotesIconDrawable = images[randInt(0,images.length-1)];
            }
            mNotesIconHeight = mNotesIconDrawable.getIntrinsicHeight();
        }

        public void calculateNextStep() {
            if (Y > mNotesViewHeight / scale) {
                init();
            } else {
                Y = Y + scale * mNotesIconHeight * mFallSpeed;
               
            }
        }
        public void fadeAlpha(int alpha){
                //for now not used, I was planning something else previously
                alpha --;
        }
    }


    public void startNotesFall() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFallNotesState = FallNotesState.START;

                if (mFallNotesThread == null) {
                    mFallNotesThread = new FallNotesThread();
                }

                mNotes = new Notes[mNotesCount];
                for (int i = 0; i < mNotes.length; i++) {
                    mNotes[i] = new Notes();
                }

                if (mFallNotesState == FallNotesState.START) {

                        mFallNotesState = FallNotesState.RUNNING;

                }
            }
        }, 1000);

    }
    //Use this to Pause the Animation
    public void pauseNotesFall() {
        if (mFallNotesState == FallNotesState.RUNNING) {
            mFallNotesState = FallNotesState.PAUSE;
        }
    }
    //Use this to Resume the Animation
    public void resumeNotesFall() {
        if (mFallNotesState == FallNotesState.PAUSE) {
            mFallNotesState = FallNotesState.RUNNING;
        }
    }
    //Stop the animation
    public void stopNotesFall() {
        mFallNotesState = FallNotesState.STOP;
        mFallNotesThread.interrupt();
//        FallNotesThread.s
    }

    private class FallNotesThread extends Thread {
        @Override
        public void run() {
            while (true) {
                switch (mFallNotesState) {
                    case RUNNING:
                        calculateNotesNextAttr();
                        postInvalidate();
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case PAUSE:
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STOP:
                        Thread.interrupted();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
