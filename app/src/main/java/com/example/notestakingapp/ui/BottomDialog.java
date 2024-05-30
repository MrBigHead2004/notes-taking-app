package com.example.notestakingapp.ui;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.HideKeyBoard;
import com.example.notestakingapp.utils.TextUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BottomDialog {
    private static SQLiteDatabase db;
    static DatabaseHandler databaseHandler;

    public static String selectedNoteColor = "#FFFFFF";
    private static Context mContext;

    public static void setColor(String color) {

        if (mContext instanceof NoteEditActivity) {
            ((NoteEditActivity) mContext).setColorBackgroundNoteEdit(color);
        }
    }

    public static void showToolDialog(Context context) {
        mContext = context;
        Log.d("duyColor", String.valueOf(mContext instanceof NoteEditActivity));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tool_dialog_layout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutFinishedNote = dialog.findViewById(R.id.layout_mask_as_finished);
        LinearLayout linearLayoutDeleteNote = dialog.findViewById(R.id.layout_delete_note);

        final ImageView colorNoteDefault = dialog.findViewById(R.id.color_note_default);
        final ImageView colorNoteVariant1 = dialog.findViewById(R.id.color_note_1);
        final ImageView colorNoteVariant2 = dialog.findViewById(R.id.color_note_2);
        final ImageView colorNoteVariant3 = dialog.findViewById(R.id.color_note_3);
        final ImageView colorNoteVariant4 = dialog.findViewById(R.id.color_note_4);
        final ImageView colorNoteVariant5 = dialog.findViewById(R.id.color_note_5);
        LinearLayout linearLayoutSetRemind = dialog.findViewById(R.id.tool_set_remind);

        linearLayoutSetRemind.setOnClickListener(new View.OnClickListener() {
            final int[] yearPicker = {-1};
            final int[] monthPicker = {-1};
            final int[] dayPicker = {-1};
            final int[] hourPicker = {-1};
            final int[] minutePicker = {-1};

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = calendar.get(Calendar.MINUTE);

            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                        .setTitleText("Select date bae")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Date");
                datePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        calendar.setTimeInMillis(aLong);
                        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
                        builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(currentHour)
                                .setMinute(currentMinutes)
                                .setTitleText("Select Time Bae");
                        MaterialTimePicker timePicker = builder.build();

//                        https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/timepicker/TimePickerMainDemoFragment.java
                        timePicker.addOnPositiveButtonClickListener(dialog -> {

                            //todo: add setTime date todo task
                            int selectedHour = timePicker.getHour();
                            int selectedMinute = timePicker.getMinute();
                            Toast.makeText(context, "Date: " +
                                    calendar.get(Calendar.YEAR) + "-"
                                    + (calendar.get(Calendar.MONTH) + 1) + "-" +
                                    calendar.get(Calendar.DAY_OF_MONTH) +
                                    " Time: " + selectedHour + ":" +
                                    selectedMinute, Toast.LENGTH_SHORT).show();
                        });

                        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time");
                    }
                });
            }
        });
        dialog.findViewById(R.id.view_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFFFFF";
                colorNoteDefault.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7F6D4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDEBAB";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);

            }
        });
        dialog.findViewById(R.id.view_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#DAF6E4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#EFE9F7";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7DEE3";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(R.drawable.round_done_24);
                setColor(selectedNoteColor);

            }
        });

        linearLayoutFinishedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: them note vao db

                dialog.dismiss();
                context.startActivity(new Intent(context, MainActivity.class));

            }
        });
        linearLayoutDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showConfirmDeleteNote(context);
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public static void showConfirmDeleteNote(Context context) {
        final Dialog dialog = new Dialog(context);
        SharedViewModel sharedViewModel = new SharedViewModel();
        if (context instanceof MainActivity) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((MainActivity) context);
            sharedViewModel = viewModelProvider.get(SharedViewModel.class);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_delete_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutRestore = dialog.findViewById(R.id.layout_restore_note);
        LinearLayout linearLayoutConfirmDelete = dialog.findViewById(R.id.layout_confirm_delete_note);

        SharedViewModel finalSharedViewModel = sharedViewModel;

        linearLayoutRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    finalSharedViewModel.setItemLongPressed(false);
                    finalSharedViewModel.triggerClearUiEvent();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    showToolDialog(context);
                }
            }
        });
        linearLayoutConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteTakingDatabaseHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
                db = noteTakingDatabaseHelper.getReadableDatabase();
                databaseHandler = new DatabaseHandler();
                if (context instanceof MainActivity) {
                    if (listNoteIdChecked != null && !listNoteIdChecked.isEmpty()) {
                        for (int i : listNoteIdChecked) {
                            databaseHandler.deleteNote(context, i);
                            if (finalSharedViewModel != null) {
                                finalSharedViewModel.setItemLongPressed(false);
                                finalSharedViewModel.triggerClearUiEvent();
                                finalSharedViewModel.notifyDataChanged();
                            }
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, "No Item Selected!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    int noteId = NoteEditActivity.noteId;
                    //todo: handle delete note

                    databaseHandler.deleteNote(context, noteId);
                    dialog.dismiss();
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity)
                    listNoteIdChecked.clear();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (context instanceof MainActivity && listNoteIdChecked != null)
                    listNoteIdChecked.clear();
            }
        });
    }


    public static void showToDoDiaLog(Context context, @Nullable ToDo todo) {
        SharedViewModel sharedViewModel = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
        SQLiteDatabase db;
        DatabaseHandler databaseHandler;
        NoteTakingDatabaseHelper noteTakingDatabaseHelper;
        noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();
        final int[] yearPicker = {-1};
        final int[] monthPicker = {-1};
        final int[] dayPicker = {-1};
        final int[] hourPicker = {-1};
        final int[] minutePicker = {-1};
        final long[] miLiSecond = {-1};

        int color = ContextCompat.getColor(context, R.color.colorTextHint);
        int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        final String[] content = {""};

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_to_do_edit);
        EditText editText = dialog.findViewById(R.id.edittext_todo);
        TextView textViewDone = dialog.findViewById(R.id.textview_done);

        if (todo != null) {
            editText.setText(todo.getContent());
            textViewDone.setTextColor(colorAccent);
            miLiSecond[0] = todo.getDuration();
        }
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 400);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("duyText", "no focus");
                    // EditText không còn focus, thực hiện xử lý ở đây
                    String text = editText.getText().toString();
                    List<String> urls = new ArrayList<>();
                    urls = TextUtils.linkDetectFromText(text);
                    SpannableString spannableString = new SpannableString(text);

                    for (String url : urls) {
                        int startIndex = text.indexOf(url);
                        while (startIndex != -1) {
                            int endIndex = startIndex + url.length();
                            spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ForegroundColorSpan(colorAccent), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            startIndex = text.indexOf(url, endIndex);
                        }
                    }

                    editText.setText(spannableString);
                }
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().isEmpty()) {
                    Log.d("duyToDo", String.valueOf(color));
                    textViewDone.setTextColor(color);
                } else {
                    content[0] = s.toString();
                    textViewDone.setTextColor(colorAccent);
                    textViewDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (todo != null) {
                                //todo: update todo
                                databaseHandler.updateTodo(context, todo.getId(), editText.getText().toString().trim(), miLiSecond[0], false);
                                dialog.dismiss();
                            } else {
                                //todo: add toDo
                                if (miLiSecond[0] != -1) {
                                    DatabaseHandler.insertTodo(context, content[0], miLiSecond[0]);
                                } else {
                                    DatabaseHandler.insertTodo(context, content[0], null);
                                }
                                dialog.dismiss();

                                Toast.makeText(context, "add task test", Toast.LENGTH_SHORT).show();
                            }
                            sharedViewModel.setIsTodoChange(true);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                String text = s.toString();
//                List<String> urls = new ArrayList<>();
//                urls = TextUtils.linkDetectFromText(text);
//                SpannableString spannableString = new SpannableString(text);
//
//                if(!urls.isEmpty()) {
//                    for (String url: urls) {
//                        int startIndex = text.indexOf(url);
//                        int endIndex = startIndex + url.length();
//                        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//                editText.setText(spannableString);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.y = dpToPx(context, 20); // Chuyển đổi dp thành px
        dialog.getWindow().setAttributes(layoutParams);

        LinearLayout linearLayoutSetRemind = dialog.findViewById(R.id.layout_set_remind);
        linearLayoutSetRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                HideKeyBoard.hideKeyboard((Activity) context);
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                        .setTitleText("Select date bae")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Date");

                datePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        calendar.setTimeInMillis(aLong);
                        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
                        builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(currentHour)
                                .setMinute(currentMinutes)
                                .setTitleText("Select Time Bae");
                        MaterialTimePicker timePicker = builder.build();

//                        https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/timepicker/TimePickerMainDemoFragment.java
                        timePicker.addOnPositiveButtonClickListener(dialog -> {

                            //todo: add setTime date todo task
                            int selectedHour = timePicker.getHour();
                            int selectedMinute = timePicker.getMinute();

                            // Set time in calendar
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            miLiSecond[0] = calendar.getTimeInMillis();
                            Log.d("timePickDuy", String.valueOf(miLiSecond[0]));
                            Log.d("timePickDuy", String.valueOf(System.currentTimeMillis()) + "system");
                            //todo: add OK cho duong lam ham
                        });

                        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time");
                    }
                });
            }
        });

    }

    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
