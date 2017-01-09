package dyanamitechetan.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dyanamitechetan.vusikview.VusikView;

public class MainActivity extends AppCompatActivity {
    VusikView v;
    int x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = (VusikView) findViewById(R.id.vusik);
        int[]  myImageList = new int[]{R.drawable.note1,R.drawable.note2,R.drawable.note4};
        v.setImages(myImageList).start();
        v.startNotesFall();



//        x=-1;
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(x<0){
//                    v.stopNotesFall();
//                }
//                else
//                    v.startNotesFall();
//                x=x*-1;
//            }
//        });
    }
}
