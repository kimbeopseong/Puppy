package com.example.puppy.ui.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.puppy.ResultActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import xyz.hasnat.sweettoast.SweetToast;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final String TAG = "cameraPreview";
    private Context mContext;

    private SurfaceHolder mHolder;
    private int mCameraID;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    private int mDisplayOrientation;

    private StorageReference mStorageRef;
    private String currentUserID;
    private FirebaseAuth mAuth;

    private String imageFilePath;
    private Uri photoUri;

    FirebaseFirestore db;
    Intent intent;

    String poopy_uri;
    private String date, stat, lv, currentPID;

    private Mat image_input, image_output;

    static{
        System.loadLibrary("opencv_java4");
        System.loadLibrary("imageprocessing");
    }

    public CameraPreview(Context context, int cameraId) {
        super(context);
        this.mContext = context;
        Log.d(TAG, "MyCameraPreview cameraId: " + cameraId);

        mCameraID = cameraId;

        try{
            mCamera = Camera.open(mCameraID);
        } catch (Exception e){
            Log.d(TAG, "Camera is not available");
        }

        mHolder = getHolder();
        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mDisplayOrientation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = simpleDateFormat.format(mDate);

        intent= CameraFragment.intent;
        currentPID = intent.getStringExtra("pid");
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        mCameraInfo = cameraInfo;

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e){
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged");

        if (mHolder.getSurface() == null){
            Log.e(TAG, "preview surface does not exist");
            return;
        }

        try {
            mCamera.stopPreview();
            Log.d(TAG, "preview stopped.");
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            Log.d(TAG, "Camera preview started");
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    public int calculatePreviewOrientation(Camera.CameraInfo info, int rotation){
        int degrees = 0;

        switch (rotation){
            case Surface
                    .ROTATION_0:
                degrees = 0;
                break;
            case Surface
                    .ROTATION_90:
                degrees = 90;
                break;
            case Surface
                    .ROTATION_180:
                degrees = 180;
                break;
            case Surface
                    .ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    public void takePicture() {
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            int w = camera.getParameters().getPictureSize().width;
            int h = camera.getParameters().getPictureSize().height;
            int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap resizingImage = null;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            resizingImage = Bitmap.createScaledBitmap(bitmap, 255, 255, true);

            //OpenCV imageprocessing(bitmapToMat => matToBitmap)
            Bitmap tmp = resizingImage.copy(Bitmap.Config.ARGB_8888, true);
            image_input = new Mat();
            Utils.bitmapToMat(tmp, image_input);
            //poop photo's foreground
            Bitmap foreground = imageprocess_and_save();
            if(foreground != null)
                Log.d(TAG, "foreground is set ");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ByteArrayOutputStream resizeStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            resizingImage.compress(Bitmap.CompressFormat.PNG, 100, resizeStream);
            byte[] currentData = stream.toByteArray();

            new CameraPreview.SaveImageTask().execute(currentData);
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... bytes) {
            FileOutputStream outputStream = null;

            try {
                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/poopy");
                if (!path.exists()){
                    path.mkdirs();
                }

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outputFile = new File(path, fileName);
                Uri uri = Uri.fromFile(outputFile);

                outputStream = new FileOutputStream(outputFile);
                outputStream.write(bytes[0]);
                outputStream.flush();
                outputStream.close();

                final StorageReference riversRef = mStorageRef.child("Feeds").child(currentUserID).child(intent.getExtras().get("pid").toString()).child(date+".jpg");
                UploadTask uploadTask=riversRef.putFile(uri);
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            SweetToast.error(getContext(), "Poopy Photo Error: " + task.getException().getMessage());
                        }
                        poopy_uri=riversRef.getDownloadUrl().toString();
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            poopy_uri=task.getResult().toString();
                            stat = "this is stat";
                            lv = "1";

                            final HashMap<String, Object> update_poopy_data=new HashMap<>();
                            update_poopy_data.put("poopy_uri",poopy_uri);
                            update_poopy_data.put("uid",currentUserID);
                            update_poopy_data.put("date",date);
                            update_poopy_data.put("stat",stat);
                            update_poopy_data.put("lv",lv);


                            db.collection("Pet").document(intent.getExtras().get("pid").toString()).collection("PoopData").document().set(update_poopy_data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent goResult = callResult(update_poopy_data);
                                            mContext.startActivity(goResult);
                                            CameraFragment cameraFragment = (CameraFragment) CameraFragment.cameraFragment;
                                            cameraFragment.finish();
                                        }
                                    });
                        }
                    }
                });

                Log.d(TAG, "onPictureTaken-wrote bytes: " + bytes.length + " to " + outputFile.getAbsolutePath());

                mCamera.startPreview();


//              갤러리에 반영
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(uri);
                getContext().sendBroadcast(mediaScanIntent);

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                    Log.d(TAG, "Camera preview started");
                } catch (Exception e){
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }


            } catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }


            return null;
        }
    }

    private Intent callResult(HashMap<String, Object> map){
        Intent result = new Intent(this.getContext(), ResultActivity.class);
        result.putExtra("uri", poopy_uri);
        result.putExtra("date",date);
        result.putExtra("pid", currentPID);
        return result;
    }

    public native void imageprocessing(long input_image, long ouput_image);

    //call imageprocessing JNI function
    public Bitmap imageprocess_and_save() {
        if (image_output == null)
            image_output = new Mat();
        imageprocessing(image_input.getNativeObjAddr(), image_output.getNativeObjAddr());
        Bitmap bitmapOutput = Bitmap.createBitmap(image_output.cols(), image_output.rows(), Bitmap.Config.ARGB_8888);
        //image_output to Bitmap
        Utils.matToBitmap(image_output, bitmapOutput);
        return bitmapOutput;
    }

}
