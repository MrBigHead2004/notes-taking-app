package com.example.notestakingapp.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.NoteDetailsAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.ui.DrawingView;
import com.example.notestakingapp.utils.WaitFunc;

import java.io.ByteArrayOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingActivity extends AppCompatActivity {
    public DrawingView dv;
    private int noteId;
    SharedViewModel sharedViewModelDraw;
    TextView backButton;
    ImageView imageChooseColor, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_drawing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dv = (DrawingView) findViewById(R.id.drawing_view);
        dv.mDefaultColor = 0;
        noteId = getIntent().getIntExtra("note_id", -1);
        sharedViewModelDraw = NoteEditActivity.sharedViewModel;
        initUi();
        backButton.setOnClickListener(v-> {
            saveImage();
            getOnBackPressedDispatcher().onBackPressed();
        });
        saveButton.setOnClickListener(mv -> {
            saveImage();
        });
        imageChooseColor.setOnClickListener(v-> {
            onClickChooseColor(v);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickSave(View v) {
        saveImage();
    }
    private void initUi() {
        saveButton = findViewById(R.id.image_save);
        backButton = findViewById(R.id.back_button);
        imageChooseColor = findViewById(R.id.image_choose_color);
    }
    public void onClickChooseColor(View v) {
        openColorPickerDialogue();
    }

    private void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, dv.mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        dv.mDefaultColor = color;
                        dv.changeColor(dv.mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    private void saveImage() {
        if(noteId == -1) {
            finish();
            return;
        }
        Bitmap bitmap = dv.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        int imageId = (int) DatabaseHandler.insertImage(this, noteId, byteArray);
        sharedViewModelDraw.setTest(true);
        sharedViewModelDraw.setImageId(imageId);
        sharedViewModelDraw.setImageData(byteArray);
        Log.d("aaccc", "ok1");
        BottomDialog.showAwaitDiaLog(DrawingActivity.this);
        WaitFunc.showMessageWithDelay(this, 5000);
        finish();
    }

}