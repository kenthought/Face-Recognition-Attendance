package com.example.facerecognitionattendance.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.example.facerecognitionattendance.GraphicOverlay;
import com.example.facerecognitionattendance.R;

import com.example.facerecognitionattendance.classes.Student;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class AddStudentActivity extends AppCompatActivity implements OnItemSelectedListener {

    private static final String TAG = "AddStudentActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    private Uri imageUri;
    private List<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_student);

        Spinner spinner = findViewById(R.id.spinner);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("http://192.168.1.2:8000/getStudents");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        Log.d("HELLO", "1");
                        students = readJsonStream(in);
//                        ArrayAdapter<Student> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, readJsonStream(in));
//                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdow?n_item);
//                        spinner.setAdapter(dataAdapter);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                List<String> options = new ArrayList<>();
                                for (Student student : students) {
                                    options.add(student.first_name + " " + student.middle_name + " " + student.last_name);
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_style, options);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(dataAdapter);
                                spinner.setOnItemSelectedListener(AddStudentActivity.this);
                            }
                        });
                    } catch (Exception e) {
                        Log.d("HELLO", "2");
                        e.printStackTrace();
                    } finally {
                        Log.d("HELLO", "3");
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            public List<Student> readJsonStream(InputStream in) throws IOException {
                JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                try {
                    return readStudentsArray(reader);
                } finally {
                    reader.close();
                }
            }

            public List<Student> readStudentsArray(JsonReader reader) throws IOException {
                List<Student> students = new ArrayList<>();

                reader.beginArray();
                while (reader.hasNext()) {
                    students.add(readStudents(reader));
                }
                reader.endArray();
                return students;
            }

            public Student readStudents(JsonReader reader) throws IOException {
                long id = -1;
                String first_name = null;
                String middle_name = "";
                String last_name = null;
                String birthday = null;

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextLong();
                    } else if (name.equals("first_name")) {
                        first_name = reader.nextString();
                    } else if (name.equals("middle_name") && reader.peek() != JsonToken.NULL) {
                        middle_name = reader.nextString();
                    } else if (name.equals("last_name")) {
                        last_name = reader.nextString();
                    } else if (name.equals("birthday")) {
                        birthday = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }

                reader.endObject();
                Log.d("PARSED", id + first_name + middle_name + last_name + birthday);
                return new Student(id, first_name, middle_name, last_name, birthday);
            }
        });

        thread.start();

        findViewById(R.id.add_photo)
                .setOnClickListener(
                        view -> {
                            // Menu for selecting either: a) take new photo b) select from existing
                            PopupMenu popup = new PopupMenu(AddStudentActivity.this, view);
                            popup.setOnMenuItemClickListener(
                                    menuItem -> {
                                        int itemId = menuItem.getItemId();
                                        if (itemId == R.id.select_images_from_local) {
                                            startChooseImageIntentForResult();
                                            return true;
                                        } else if (itemId == R.id.take_photo_using_camera) {
                                            startCameraIntentForResult();
                                            return true;
                                        }
                                        return false;
                                    });
                            MenuInflater inflater = popup.getMenuInflater();
                            inflater.inflate(R.menu.camera_button_menu, popup.getMenu());
                            popup.show();
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startCameraIntentForResult() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("PICKED", parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
