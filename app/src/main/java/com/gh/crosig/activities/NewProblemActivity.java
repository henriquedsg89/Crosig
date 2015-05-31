package com.gh.crosig.activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.gh.crosig.fragments.NewPhoto;
import com.gh.crosig.fragments.NewProblemDetails;
import com.gh.crosig.R;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.utils.ImageUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewProblemActivity extends ActionBarActivity {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            imageBitmap = ImageUtils.rotateImage((Bitmap) extras.get("data"));
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
        problem.setStatus(getResources().getStringArray(R.array.problem_status)[0]);
        problem.setParseGeoPoint(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

        Toast.makeText(getBaseContext(), String.format("Salvando...\n%s", problem.getName()),
                Toast.LENGTH_LONG).show();

        final ParseFile parseFile = new ParseFile("test.jpg",
                ImageUtils.bitmapToByteArray(imageBitmap));
        try {
            parseFile.save();
            problem.setImage(parseFile);
            problem.save();

            ProblemFollow pf = new ProblemFollow();
            pf.setProblem(problem);
            pf.setUser(ParseUser.getCurrentUser());
            pf.saveInBackground();

            finish();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
