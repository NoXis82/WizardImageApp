package com.example.wizardimageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardimageapp.adapter.ImageListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleBiFunction;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private List<Bitmap> images = new ArrayList<>();
    private ImageListAdapter imageListAdapter;
    private ImageView imageView;
    private TextView imageViewText;
    private Button rotateBtn;
    private Button invertBtn;
    private Button mirrorBtn;
    private ListView listItemAction;
    private ProgressBar loadImageBar;
    int angle = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        clickOpenImageFileBtn();
        setRotationActionBtn();
        setInvertActionBtn();
        setMirrorActionBtn();
        clickOnItem();

    }

    private void initView() {
        imageView = findViewById(R.id.imageView);
        loadImageBar = findViewById(R.id.progressBarImage);
        imageViewText = findViewById(R.id.imageViewText);
        rotateBtn = findViewById(R.id.rotateBtn);
        invertBtn = findViewById(R.id.invertBtn);
        mirrorBtn = findViewById(R.id.mirrorBtn);
        listItemAction = findViewById(R.id.list_item_action);
        imageListAdapter = new ImageListAdapter(this, images);
        listItemAction.setAdapter(imageListAdapter);


    }

    private void clickOnItem() {
        listItemAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
                alertBuilder.setTitle("Внимание!");
                alertBuilder.setMessage("Выбираем что делаем");
                alertBuilder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "Удаляем из списка", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

                alertBuilder.setNegativeButton("Установить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "Установить картинку", Toast.LENGTH_LONG).show();
                        dialog.cancel();

                    }
                });
                alertBuilder.setCancelable(false);
                alertBuilder.create().show();
             }

        });
    }

    private void setMirrorActionBtn() {
        mirrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.buildDrawingCache();
                Bitmap imageMirror = imageView.getDrawingCache();
                Matrix matrix = new Matrix();
                matrix.setScale(-1, 1, imageMirror.getWidth()/2f, imageMirror.getHeight()/2f);
                imageMirror = Bitmap.createBitmap(
                        imageMirror,
                        0,
                        0,
                        imageMirror.getWidth(),
                        imageMirror.getHeight(),
                        matrix,
                        true
                        );
                images.add(0, imageMirror);
                imageListAdapter.notifyDataSetChanged();

            }
        });
    }

    private void setRotationActionBtn() {
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle = (angle < 360) ? angle += 90 : 90;
                imageView.buildDrawingCache();
                Bitmap imageRotate = imageView.getDrawingCache();
                Matrix matrix = new Matrix();
                matrix.setRotate(angle);
                imageRotate = Bitmap.createBitmap(
                        imageRotate,
                        0,
                        0,
                        imageRotate.getWidth(),
                        imageRotate.getHeight(),
                        matrix,
                        true);
                images.add(0, imageRotate);
                imageListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setInvertActionBtn() {
        invertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.buildDrawingCache();
                Bitmap imageInvertColor = imageView.getDrawingCache();
                Bitmap resultInvert = Bitmap.createBitmap(
                        imageInvertColor.getWidth(),
                        imageInvertColor.getHeight(),
                        imageInvertColor.getConfig());
                Canvas canvas = new Canvas(resultInvert);
                ColorMatrix matrixColor = new ColorMatrix();
                matrixColor.setSaturation(0);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(matrixColor));
                canvas.drawBitmap(imageInvertColor, 0, 0, paint);
                images.add(0, resultInvert);
                imageListAdapter.notifyDataSetChanged();
            }
        });
    }


    private void clickOpenImageFileBtn() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                imageViewText.setVisibility(View.GONE);
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        switch (requestCode) {
        
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageView.setImageBitmap(bitmap);
                    startProgressLoadImage();
                    rotateBtn.setEnabled(true);
                    invertBtn.setEnabled(true);
                    mirrorBtn.setEnabled(true);
                }
        }
    }

    private void startProgressLoadImage() {
        loadImageBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                loadImageBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }

}