package com.example.note.view.impl;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.note.AlarmReceiver;
import com.example.note.R;
import com.example.note.adapter.ImageAdapter;
import com.example.note.model.ImagePath;
import com.example.note.model.Note;
import com.example.note.utils.Constant;
import com.example.note.view.AddNotefragmentView;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.AddNotefragmentPresenter;
import com.example.note.injection.AppComponent;
import com.example.note.injection.AddNotefragmentViewModule;
import com.example.note.injection.DaggerAddNotefragmentViewComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;

public final class AddNotefragment extends BaseFragment<AddNotefragmentPresenter, AddNotefragmentView> implements AddNotefragmentView, ImageAdapter.OnImageAdapter, View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 100;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_alarm)
    TextView tvAlarm;
    @BindView(R.id.edt_title)
    EditText edtTitle;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.spn_date)
    Spinner spnDate;
    @BindView(R.id.spn_time)
    Spinner spnTime;
    @BindView(R.id.imv_close)
    ImageView imvClose;
    @BindView(R.id.ln_container)
    LinearLayout lnContainer;
    @BindView(R.id.parent)
    NestedScrollView bgr;
    @BindView(R.id.rcv)
    RecyclerView rcv;
    private Dialog dialog;
    private String currentPhotoPath;
    private ImageAdapter imageAdapter;
    private ArrayAdapter<String> adapterDate;
    private ArrayAdapter<String> adapterTime;
    private Note note = new Note();

    @Inject
    PresenterFactory<AddNotefragmentPresenter> mPresenterFactory;


    public AddNotefragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_notefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerAddNotefragmentViewComponent.builder()
                .appComponent(parentComponent)
                .addNotefragmentViewModule(new AddNotefragmentViewModule())
                .build()
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @NonNull
    @Override
    protected PresenterFactory<AddNotefragmentPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }

    @Override
    public void showImage(List<ImagePath> imagePaths) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DD_MM_YYYY_HH_MM);
        tvDate.setText(simpleDateFormat.format(new Date()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        imageAdapter = new ImageAdapter(imagePaths, getContext(), this);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setAdapter(imageAdapter);
    }

    @Override
    public void showDialogCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission();
        } else {
            uploadPhoto();
        }
    }

    @Override
    public void showDialogColor() {
        dialogColor();
    }

    @Override
    public void showSpinerDate(final String[] strDate) {
        adapterDate = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strDate);
        spnDate.setAdapter(adapterDate);
        spnDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spnDate.getSelectedItem().toString().equals(Constant.OTHER)) {
                    showDatePicker(strDate);
                    return;
                }
                note.setDate(spnDate.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showSpinerTime(final String[] strTime) {
        adapterTime = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strTime);
        spnTime.setAdapter(adapterTime);
        spnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spnTime.getSelectedItem().toString().equals(Constant.OTHER)) {
                    showTimePicker(strTime);
                    return;
                }
                note.setTimeAlarm(spnTime.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDone() {
        getActivity().onBackPressed();
    }

    @Override
    public void onRemove(int pos) {
        imageAdapter.removeItem(pos);
    }

    @Override
    public void onClick(int pos) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageAdapter.getImagePaths().get(pos).getImagePath())));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        MenuItem camera = menu.findItem(R.id.action_camera);
        camera.setVisible(true);
        MenuItem color = menu.findItem(R.id.action_color);
        color.setVisible(true);
        MenuItem done = menu.findItem(R.id.action_done);
        done.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return false;
            case R.id.action_camera:
                mPresenter.onClickCamera();
                return false;
            case R.id.action_color:
                mPresenter.onClickColor();
                return false;
            case R.id.action_done:
                RealmList<ImagePath> mImagePaths = new RealmList<>();
                mImagePaths.addAll(imageAdapter.getImagePaths());
                note.setTitle(edtTitle.getText().toString());
                note.setContent(edtContent.getText().toString());
                note.setDateCreate(tvDate.getText().toString());
                note.setImages(mImagePaths);
                note.setAlarm(true);
                mPresenter.onClickDone(note);
                if (note.getDate().equals(Constant.TODAY)) {
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(new Date()));
                } else if (note.getDate().equals(Constant.TOMORROW)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, 1);
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
                } else if (note.getDate().contains(Constant.NEXT)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, 7);
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
                }
                String mDate = note.getDate() + " " + note.getTimeAlarm();
                if (note.isAlarm()) {
                    alramService(mDate, note.getTitle());
                }
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkPermission() {
        List<String> listPermissionNeed = new ArrayList<>();
        for (String perm : listPermissionNeed) {
            if (ContextCompat.checkSelfPermission(getContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeed.add(perm);
            }
        }

        if (!listPermissionNeed.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionNeed.toArray(new String[listPermissionNeed.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }

    public void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_REQUEST_ACCOUNTS);
            } else {
                uploadPhoto();
            }
        } else {
            uploadPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                HashMap<String, Integer> permissionResults = new HashMap<>();
                int deniedCount = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionResults.put(permissions[i], grantResults[i]);
                        deniedCount++;
                    }
                }
                if (deniedCount == 0) {
                    uploadPhoto();
                } else {
                    for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                        String perName = entry.getKey();
                        int perResult = entry.getValue();
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perName)) {
                            showAlertGotoSetting("", "Need check permission", "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermission();
                                    dialog.dismiss();
                                }
                            }, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },false);
                        }else {
                            showAlertGotoSetting("", "You have denied some permissions", "Go to setting", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            }, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },false);
                        }
                    }
                }
            default:
                break;
        }
    }

    public AlertDialog showAlertGotoSetting(String title, String msg
            , String postilabel, DialogInterface.OnClickListener postiOnClick
            , String negative, DialogInterface.OnClickListener negativeOnClick
            , boolean isCanable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setCancelable(isCanable);
        builder.setMessage(msg);
        builder.setPositiveButton(postilabel, postiOnClick);
        builder.setNegativeButton(negative, negativeOnClick);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public void uploadPhoto() {
        final CharSequence[] options = {Constant.CAMERA, Constant.GALLERY};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(Constant.CAMERA)) {
                    dispatchTakePictureIntent();
                    galleryAddPic();
                } else if (options[item].equals(Constant.GALLERY)) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                }
            }
        });
        builder.show();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), Constant.PROVIDER, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    public void dialogColor() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_color);
        ImageView imvWhite = dialog.findViewById(R.id.imv_white);
        ImageView imvBlue = dialog.findViewById(R.id.imv_blue);
        ImageView imvAccent = dialog.findViewById(R.id.imv_accent);
        ImageView imvPrimary = dialog.findViewById(R.id.imv_primary);
        imvWhite.setOnClickListener(this);
        imvBlue.setOnClickListener(this);
        imvAccent.setOnClickListener(this);
        imvPrimary.setOnClickListener(this);
        dialog.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(Constant.DD_MM_YYYY).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(storageDir.toString() + "/notes");
        myDir.mkdirs();
        File image = new File(myDir, imageFileName);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    File file = new File(currentPhotoPath);
                    Uri uriCamera = Uri.fromFile(file);
                    String uriStrCamera = uriCamera.toString();
                    ImagePath imageCamera = new ImagePath();
                    imageCamera.setImagePath(uriStrCamera);
                    imageAdapter.addItem(imageCamera);
                    break;
                case 1:
                    if (data != null) {
                        final Uri uriGalery = data.getData();
                        String uriStrGalery = uriGalery.toString();
                        ImagePath imageGalery = new ImagePath();
                        imageGalery.setImagePath(uriStrGalery);
                        imageAdapter.addItem(imageGalery);
                    }
                    break;
            }
        }
    }

    @OnClick(R.id.imv_close)
    public void onClose() {
        lnContainer.setVisibility(View.INVISIBLE);
        tvAlarm.setVisibility(View.VISIBLE);
        note.setAlarm(false);
    }

    @OnClick(R.id.tv_alarm)
    public void onContainer() {
        tvAlarm.setVisibility(View.GONE);
        lnContainer.setVisibility(View.VISIBLE);
        note.setAlarm(true);
    }

    public void showTimePicker(final String[] strTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                strTime[strTime.length - 1] = hourOfDay + ":" + minute;
                adapterTime = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strTime);
                spnTime.setAdapter(adapterTime);
                spnTime.setSelection(strTime.length - 1);
                note.setTimeAlarm(spnTime.getSelectedItem().toString());
            }
        }
                , Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                , Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Choose Time");
        timePickerDialog.show();
    }

    public void showDatePicker(final String[] strDate) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                strDate[strDate.length - 1] = dayOfMonth + "/" + month + "/" + year;
                adapterDate = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strDate);
                spnDate.setAdapter(adapterDate);
                spnDate.setSelection(strDate.length - 1);
                note.setDate(spnDate.getSelectedItem().toString());
            }
        }, year, month, day);
        datePickerDialog.setTitle("Choose Date");
        datePickerDialog.show();
    }

    public void alramService(String date, String title) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.DD_MM_YYYY_HH_MM);
        try {
            Date mDate = simpleDateFormat.parse(date);
            Date nDate = new Date();
            long time = mDate.getTime() - nDate.getTime();
            AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra(Constant.TITLE, title);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            time, alarmIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_white:
                note.setColor(Color.parseColor("#FFFFFF"));
                bgr.setBackgroundColor(Color.parseColor("#FFFFFF"));
                dialog.dismiss();
                break;
            case R.id.imv_blue:
                note.setColor(Color.parseColor("#2196F3"));
                bgr.setBackgroundColor(Color.parseColor("#2196F3"));
                dialog.dismiss();
                break;
            case R.id.imv_accent:
                note.setColor(Color.parseColor("#D81B60"));
                bgr.setBackgroundColor(Color.parseColor("#D81B60"));
                dialog.dismiss();
                break;
            case R.id.imv_primary:
                note.setColor(Color.parseColor("#008577"));
                bgr.setBackgroundColor(Color.parseColor("#008577"));
                dialog.dismiss();
                break;
        }
    }
}
