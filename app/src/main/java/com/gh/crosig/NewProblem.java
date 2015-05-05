package com.gh.crosig;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gh.crosig.model.Problem;

public class NewProblem extends ActionBarActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText problemName;
    private EditText problemDesc;
    private Spinner problemType;
    private CheckBox problemFollow;
    private ImageView mImageView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_problem);
        mImageView = (ImageView) findViewById(R.id.problem_image);
        problemName = (EditText) findViewById(R.id.problem_name);
        problemDesc = (EditText) findViewById(R.id.problem_description);
        problemType = (Spinner) findViewById(R.id.problem_type);
        problemFollow = (CheckBox) findViewById(R.id.follow_problem);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void saveProblem(View view) {
        Problem problem = new Problem(problemName.getText().toString(),
                problemDesc.getText().toString(),
                problemType.getSelectedItem().toString(),
                imageBitmap,
                getIntent().getDoubleExtra("long", 0d),
                getIntent().getDoubleExtra("lat", 0d));
        Toast.makeText(getApplicationContext(), String.format("Salvando...\n%s", problem.toString()),
                Toast.LENGTH_LONG).show();
    }
}
