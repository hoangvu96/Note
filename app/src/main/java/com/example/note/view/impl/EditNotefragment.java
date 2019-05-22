package com.example.note.view.impl;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.media.MediaScannerConnection;
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
import android.support.v7.widget.PopupMenu;
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
import android.widget.RelativeLayout;
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
import com.example.note.view.EditNotefragmentView;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.EditNotefragmentPresenter;
import com.example.note.injection.AppComponent;
import com.example.note.injection.EditNotefragmentViewModule;
import com.example.note.injection.DaggerEditNotefragmentViewComponent;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;

@SuppressLint("ValidFragment")
public final class EditNotefragment extends BaseFragment<EditNotefragmentPresenter, EditNotefragmentView> implements EditNotefragmentView, ImageAdapter.OnImageAdapter, View.OnClickListener {
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
    @BindView(R.id.imv_left)
    ImageView imvLeft;
    @BindView(R.id.imv_right)
    ImageView imvRight;
    @BindView(R.id.ln_container)
    LinearLayout lnContainer;
    @BindView(R.id.parent)
    RelativeLayout bgr;
    @BindView(R.id.rl_left)
    RelativeLayout rlLeft;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.rl_delete)
    RelativeLayout rlDelete;
    @BindView(R.id.rl_right)
    RelativeLayout rlRight;
    @BindView(R.id.rcv)
    RecyclerView rcv;
    private Dialog dialog;
    private String currentPhotoPath;
    private boolean isFirt = true;
    private ImageAdapter imageAdapter;
    private ArrayAdapter<String> adapterDate;
    private ArrayAdapter<String> adapterTime;
    private Note mNote = new Note();
    private Note tempNote = new Note();
    private int id;

    @Inject
    PresenterFactory<EditNotefragmentPresenter> mPresenterFactory;

    // Your presenter is available using the mPresenter variable

    public EditNotefragment(int id) {
        // Required empty public constructor
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_notefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Your code here
        // Do not call mPresenter from here, it will be null! Wait for onStart
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirt) {
            if (mPresenter != null) {
                mPresenter.initData(id);
                mPresenter.showSpinerDate();
                mPresenter.showSpinerTime();
                mPresenter.checkIsFirt(id);
                mPresenter.checkIsLast(id);
            }
            isFirt = false;
        }
    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerEditNotefragmentViewComponent.builder()
                .appComponent(parentComponent)
                .editNotefragmentViewModule(new EditNotefragmentViewModule())
                .build()
                .inject(this);
    }

    @NonNull
    @Override
    protected PresenterFactory<EditNotefragmentPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }

    @Override
    public void showImage(List<ImagePath> imagePaths) {

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
    public void onDone() {
        getActivity().onBackPressed();
    }


    @Override
    public void initData(Note note) {
        this.mNote = note;
        initView(note);
    }

    @Override
    public void deleteNote() {
        getActivity().onBackPressed();
    }

    @Override
    public void share(Note note) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, note.getContent());
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    @Override
    public void moveLeft(Note note) {
        this.mNote = note;
        id = note.getId();
        initView(note);
        if (mPresenter != null) {
            mPresenter.checkIsFirt(id);
        }
    }

    @Override
    public void moveRight(Note note) {
        this.mNote = note;
        id = note.getId();
        initView(note);
        if (mPresenter != null) {
            mPresenter.checkIsLast(id);
        }
    }

    @Override
    public void showLeft() {
        imvLeft.setImageResource(R.drawable.ic_back);
    }

    @Override
    public void showRight() {
        imvRight.setImageResource(R.drawable.ic_chevron_right_black_24dp);
    }

    @Override
    public void hideLeft() {
        imvLeft.setImageResource(R.drawable.ic_left_gray);
    }

    @Override
    public void hideRight() {
        imvRight.setImageResource(R.drawable.ic_right_gray);
    }

    @Override
    public void addNewNote() {
        changeFragment(getClass().getSimpleName(),new AddNotefragment());
    }

    @Override
    public void onRemove(int pos) {
        imageAdapter.removeItem(pos);
    }

    @Override
    public void onClick(int pos) {
        Uri uri = Uri.parse(imageAdapter.getImagePaths().get(pos).getImagePath());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void showDialogColor() {
        dialogColor();
    }

    @Override
    public void showSpinerDate(final String[] strDate) {
        strDate[strDate.length-1]= mNote.getDate();
        adapterDate = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strDate);
        spnDate.setAdapter(adapterDate);
        spnDate.setSelection(strDate.length-1);
        spnDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spnDate.getSelectedItem().toString().equals(Constant.OTHER)) {
                    showDatePicker(strDate);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showSpinerTime(final String[] strTime) {
        strTime[strTime.length-1]= mNote.getTimeAlarm();
        adapterTime = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strTime);
        spnTime.setAdapter(adapterTime);
        spnTime.setSelection(strTime.length-1);
        spnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spnTime.getSelectedItem().toString().equals(Constant.OTHER)) {
                    showTimePicker(strTime);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @OnClick(R.id.imv_close)
    public void onClose() {
        lnContainer.setVisibility(View.INVISIBLE);
        tvAlarm.setVisibility(View.VISIBLE);
        tempNote.setAlarm(false);
    }

    @OnClick(R.id.tv_alarm)
    public void onContainer() {
        tvAlarm.setVisibility(View.GONE);
        lnContainer.setVisibility(View.VISIBLE);
        tempNote.setAlarm(true);
    }

    @OnClick(R.id.rl_left)
    public void onLeft() {
        if (mPresenter != null) {
            mPresenter.left(id);
            mPresenter.checkIsFirt(id);
            mPresenter.checkIsLast(id);
        }
    }

    @OnClick(R.id.rl_share)
    public void onShare() {
        if (mPresenter != null) {
            mPresenter.share(id);
        }
    }

    @OnClick(R.id.rl_delete)
    public void onDelete() {
        if (mPresenter != null) {
            mPresenter.deleteNote(id);
        }
    }

    @OnClick(R.id.rl_right)
    public void onRight() {
        if (mPresenter != null) {
            mPresenter.right(id);
            mPresenter.checkIsLast(id);
            mPresenter.checkIsFirt(id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_white:
                tempNote.setColor(Color.parseColor("#FFFFFF"));
                bgr.setBackgroundColor(Color.parseColor("#FFFFFF"));
                dialog.dismiss();
                break;
            case R.id.imv_blue:
                tempNote.setColor(Color.parseColor("#2196F3"));
                bgr.setBackgroundColor(Color.parseColor("#2196F3"));
                dialog.dismiss();
                break;
            case R.id.imv_accent:
                tempNote.setColor(Color.parseColor("#D81B60"));
                bgr.setBackgroundColor(Color.parseColor("#D81B60"));
                dialog.dismiss();
                break;
            case R.id.imv_primary:
                tempNote.setColor(Color.parseColor("#008577"));
                bgr.setBackgroundColor(Color.parseColor("#008577"));
                dialog.dismiss();
                break;
        }
    }

    public void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
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
                            showAlertGotoSetting("Permission", "Need check permission", "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    askForPermission();
                                    dialog.dismiss();
                                }
                            }, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, false);
                            break;
                        } else {
                            showAlertGotoSetting("Permission", "You have denied some permissions", "Go to setting", new DialogInterface.OnClickListener() {
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
                            }, false);
                            break;
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
                } else if (options[item].equals( Constant.GALLERY)) {
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.note.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Random random = new Random();
        int i = random.nextInt(1000);
        String imageFileName = "JPEG_" + timeStamp + "_" + i + "_" + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(storageDir.toString() + "/vuhtnotes");
        myDir.mkdirs();
        File image = new File(myDir, imageFileName);
        MediaScannerConnection.scanFile(getContext(), new String[]{image.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        currentPhotoPath = image.getAbsolutePath();
        return image;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    File file = new File(currentPhotoPath);
                    Uri uriCamera = FileProvider.getUriForFile(getContext(), "com.example.note.fileprovider", file);
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

    public void initView(Note note) {
        tvDate.setText(note.getDateCreate());
        if (mNote.isAlarm()) {
            lnContainer.setVisibility(View.VISIBLE);
            tvAlarm.setVisibility(View.GONE);
        }
        edtTitle.setText(note.getTitle());
        edtContent.setText(note.getContent());
        bgr.setBackgroundColor(note.getColor());
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(note.getTitle());
        }
        List<ImagePath> imagePathList = note.getImages();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        imageAdapter = new ImageAdapter(imagePathList, getContext(), this);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setAdapter(imageAdapter);
    }


    public void showTimePicker(final String[] strTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_DARK
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                strTime[strTime.length - 1] = hourOfDay + ":" + minute;
                adapterTime = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, strTime);
                spnTime.setAdapter(adapterTime);
                spnTime.setSelection(strTime.length - 1);
                mNote.setTimeAlarm(spnTime.getSelectedItem().toString());
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
                mNote.setDate(spnDate.getSelectedItem().toString());
            }
        }, year, month, day);
        datePickerDialog.setTitle("Choose Date");
        datePickerDialog.show();
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
        MenuItem setting = menu.findItem(R.id.action_settings);
        setting.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return false;
            case R.id.action_camera:
                if (mPresenter != null) {
                    mPresenter.onClickCamera();
                }
                return false;
            case R.id.action_color:
                if (mPresenter != null) {
                    mPresenter.onClickColor();
                }
                return false;
            case R.id.action_done:
                RealmList<ImagePath> mImagePaths = new RealmList<>();
                mImagePaths.addAll(imageAdapter.getImagePaths());
                Note eNote = new Note();
                eNote.setId(mNote.getId());
                eNote.setColor(tempNote.getColor());
                eNote.setTitle(edtTitle.getText().toString());
                eNote.setContent(edtContent.getText().toString());
                eNote.setDateCreate(tvDate.getText().toString());
                eNote.setImages(mImagePaths);
                eNote.setDate(spnDate.getSelectedItem().toString());
                eNote.setTimeAlarm(spnTime.getSelectedItem().toString());
                eNote.setAlarm(tempNote.isAlarm());
                if (eNote.getDate().equals(Constant.TODAY)){
                    eNote.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(new Date()));
                }else if (eNote.getDate().equals(Constant.TOMORROW)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE,1);
                    eNote.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
                }else if (eNote.getDate().contains(Constant.NEXT)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE,7);
                    eNote.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
                }
                String mDate = eNote.getDate() + " " + eNote.getTimeAlarm();
                if (eNote.isAlarm()) {
                    alramService(mDate, eNote.getTitle());
                }
                if (mPresenter != null) {
                    mPresenter.onClickDone(eNote);
                }
                return false;
            case R.id.action_settings:
                View vItem = getActivity().findViewById(R.id.action_settings);
                PopupMenu popMenu = new PopupMenu(getActivity(), vItem);
                popMenu.inflate(R.menu.menu_add);
                popMenu.show();
                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mPresenter.addNewNote();
                        return true;
                    }
                });

                return false;
        }

        return super.onOptionsItemSelected(item);
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
}
