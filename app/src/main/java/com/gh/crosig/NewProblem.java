package com.gh.crosig;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gh.crosig.model.Problem;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class NewProblem extends ActionBarActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText problemName;
    private EditText problemDesc;
    private Spinner problemType;
    private CheckBox problemFollow;
    private ParseImageView mImageView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_problem);
        getFragmentManager().beginTransaction().add(R.id.new_problem_container, new NewPhoto()).commit();
        takePicture(null);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(getApplicationContext(), "Por favor, tire um foto do problema.",
                    Toast.LENGTH_LONG).show();
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView = (ParseImageView) findViewById(R.id.problem_image);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void next(View view) {
        if (imageBitmap == null) {
            Toast.makeText(getApplicationContext(), "Tire uma foto do problema!", Toast.LENGTH_LONG).show();
            return;
        }
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.addToBackStack(null);
        trans.replace(R.id.new_problem_container, new NewProblemDetails()).commit();
    }

    public void saveProblem(View view) {
        problemName = (EditText) findViewById(R.id.problem_name);
        problemDesc = (EditText) findViewById(R.id.problem_description);
        problemType = (Spinner) findViewById(R.id.problem_type);
        problemFollow = (CheckBox) findViewById(R.id.follow_problem);

        if (problemName.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Informe o título do problema!", Toast.LENGTH_LONG);
            return;
        }

        Location location = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_LOCATION);

        final Problem problem = new Problem();
        problem.setUser(ParseUser.getCurrentUser());
        problem.setName(problemName.getText().toString());
        problem.setDesc(problemDesc.getText().toString());
        problem.setType(problemType.getSelectedItem().toString());
        problem.setParseGeoPoint(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

        Toast.makeText(getApplicationContext(), String.format("Salvando...\n%s", problem.getName()),
                Toast.LENGTH_LONG).show();


        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);


        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(imageBitmap, 0,
                0, imageBitmap.getWidth(), imageBitmap.getHeight(),
                matrix, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();

        final ParseFile parseFile = new ParseFile("test.jpg", data);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                problem.setACL(acl);
                problem.setImage(parseFile);
                problem.saveInBackground();
            }
        });
        finish();
    }


}
