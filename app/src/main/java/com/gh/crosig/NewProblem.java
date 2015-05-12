package com.gh.crosig;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gh.crosig.model.Problem;
import com.gh.crosig.services.provider.ProblemProvider;

public class NewProblem extends ActionBarActivity
    implements NewPhoto.OnFragmentInteractionListener, NewProblemDetails.OnFragmentInteractionListener {

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
        getFragmentManager().beginTransaction().add(R.id.new_problem_container, new NewProblemDetails()).commit();
//        mImageView = (ImageView) findViewById(R.id.problem_image);

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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
        ContentValues values = new ContentValues();

        problemName = (EditText) findViewById(R.id.problem_name);
        problemDesc = (EditText) findViewById(R.id.problem_description);
        problemType = (Spinner) findViewById(R.id.problem_type);
        problemFollow = (CheckBox) findViewById(R.id.follow_problem);

        values.put(ProblemProvider.NAME, problemName.getText().toString());
        values.put(ProblemProvider.DESC, problemDesc.getText().toString());
        values.put(ProblemProvider.TYPE, problemType.getSelectedItem().toString());
        values.put(ProblemProvider.LONG, getIntent().getDoubleExtra("long", 0d));
        values.put(ProblemProvider.LAT, getIntent().getDoubleExtra("lat", 0d));

        Uri uri = getContentResolver().insert(ProblemProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), String.format("Salvando...\n%s", uri.toString()),
                Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
