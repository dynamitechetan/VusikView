package dyanamitechetan.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dyanamitechetan.vusikview.VusikView;

public class MainActivity extends AppCompatActivity {
    VusikView v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = (VusikView) findViewById(R.id.vusik);
        int[]  myImageList = new int[]{R.drawable.note1,R.drawable.note2,R.drawable.note4};
        v.setImages(myImageList).start();
    }
}
