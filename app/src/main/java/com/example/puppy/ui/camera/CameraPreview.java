package com.example.puppy.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import xyz.hasnat.sweettoast.SweetToast;

public class CameraPreview extends Thread {

    private final String TAG = "cameraPreview";
    private Context mContext;
    private Size previewSize;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private TextureView mPreview;
    private Button capture;
    private StreamConfigurationMap map;
    private CallbackInterface callbackInterface;

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

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static{
        System.loadLibrary("opencv_java4");
        System.loadLibrary("imageprocessing");
    }

    private Mat image_input, image_output;

    public CameraPreview(Context context, TextureView textureView, Button button) {
        mContext = context;
        mPreview = textureView;
        capture = button;
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

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

    public void setOnCallbackListener(CallbackInterface callbackInterface){
        this.callbackInterface = callbackInterface;
    }

    private String getBackFacingCameraId(CameraManager cameraManager){
        try{
            for (final String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int cameraOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cameraOrientation == CameraCharacteristics.LENS_FACING_BACK) return cameraId;
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    public void openCamera(){
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera");
        try {
            String cameraId = getBackFacingCameraId(manager);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            int permissionCamera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            int permissionStorage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCamera == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, CameraFragment.REQUEST_CAM);
            } else if (permissionStorage == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CameraFragment.REQUEST_STORAGE);
            } else {
                manager.openCamera(cameraId, mStateCallback, null);
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera: End");
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureAvailable: width = " + i + ", height = " + i1);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Log.e(TAG, "onOpened");
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Log.e(TAG, "onError");
        }
    };

    protected void startPreview(){
        if (null == mCameraDevice || !mPreview.isAvailable() || null == previewSize){
            Log.e(TAG, "startPreview: fail. return");
        }

        SurfaceTexture texture = mPreview.getSurfaceTexture();
        if (null == texture){
            Log.e(TAG, "Texture is null. return.");
            return;
        }

        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(texture);

        try{
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try{
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(mContext, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e){
            e.getMessage();
        }
    }

    protected void updatePreview(){
        if (null == mCameraDevice){
            Log.e(TAG, "updatePreview error. return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try{
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e){
            e.getMessage();
        }
    }

    public void takePicture() {
        if (null == mCameraDevice){
            Log.e(TAG, "CameraDevice is null. return");
            return;
        }

        try{
            int width = 480;
            int height = 640;
//            Size[] jpegSizes = null;
//            if (map != null){
//                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
//            }
//            if (jpegSizes != null && 0 < jpegSizes.length){
//                width = jpegSizes[0].getWidth();
//                height = jpegSizes[0].getHeight();
//            }

            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mPreview.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            mDisplayOrientation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(mDisplayOrientation));

        } catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    public void setSurfaceTextureListener(){
        mPreview.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void onResume(){
        Log.d(TAG, "onResume");
        setSurfaceTextureListener();
    }

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public void onPause(){
        Log.d(TAG, "onPause");

        try{
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice){
                mCameraDevice.close();
                mCameraDevice = null;
                Log.d(TAG, "CameraDevice Closed !!!");
            }
        } catch (InterruptedException e){
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }


//    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] bytes, Camera camera) {
//            int w = camera.getParameters().getPictureSize().width;
//            int h = camera.getParameters().getPictureSize().height;
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap resizingImage = null;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);
//            resizingImage = Bitmap.createScaledBitmap(bitmap, 255, 255, true);
//
//            //OpenCV imageprocessing(bitmapToMat => matToBitmap)
//            Bitmap tmp = resizingImage.copy(Bitmap.Config.ARGB_8888, true);
//            image_input = new Mat();
//            Utils.bitmapToMat(tmp, image_input);
//            //poop photo's foreground
//            Bitmap foreground = imageprocess_and_save();
//            if(foreground != null)
//                Log.d(TAG, "foreground is set");
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            ByteArrayOutputStream resizeStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            resizingImage.compress(Bitmap.CompressFormat.PNG, 100, resizeStream);
//            byte[] currentData = stream.toByteArray();
//
//            new CameraPreview.SaveImageTask().execute(currentData);
//        }
//    };
//
//    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
//
//        @Override
//        protected Void doInBackground(byte[]... bytes) {
//            FileOutputStream outputStream = null;
//
//            try {
//                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/poopy");
//                if (!path.exists()){
//                    path.mkdirs();
//                }
//
//                String fileName = String.format("%d.jpg", System.currentTimeMillis());
//                File outputFile = new File(path, fileName);
//                Uri uri = Uri.fromFile(outputFile);
//
//                outputStream = new FileOutputStream(outputFile);
//                outputStream.write(bytes[0]);
//                outputStream.flush();
//                outputStream.close();
//
//                final StorageReference riversRef = mStorageRef.child("Feeds").child(currentUserID).child(intent.getExtras().get("pid").toString()).child(date+".jpg");
//                UploadTask uploadTask=riversRef.putFile(uri);
//                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if(!task.isSuccessful()){
//                            SweetToast.error(getContext(), "Poopy Photo Error: " + task.getException().getMessage());
//                        }
//                        poopy_uri=riversRef.getDownloadUrl().toString();
//                        return riversRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if(task.isSuccessful()){
//                            poopy_uri=task.getResult().toString();
//                            stat = "this is stat";
//                            lv = "1";
//
//                            final HashMap<String, Object> update_poopy_data=new HashMap<>();
//                            update_poopy_data.put("poopy_uri",poopy_uri);
//                            update_poopy_data.put("uid",currentUserID);
//                            update_poopy_data.put("date",date);
//                            update_poopy_data.put("stat",stat);
//                            update_poopy_data.put("lv",lv);
//
//
//                            db.collection("Pet").document(intent.getExtras().get("pid").toString()).collection("PoopData").document().set(update_poopy_data, SetOptions.merge())
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Intent goResult = callResult(update_poopy_data);
//                                            mContext.startActivity(goResult);
//                                            CameraFragment cameraFragment = (CameraFragment) CameraFragment.cameraFragment;
//                                            cameraFragment.finish();
//                                        }
//                                    });
//                        }
//                    }
//                });
//
//                Log.d(TAG, "onPictureTaken-wrote bytes: " + bytes.length + " to " + outputFile.getAbsolutePath());
//
//                mCamera.startPreview();
//
//
////              갤러리에 반영
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaScanIntent.setData(uri);
//                getContext().sendBroadcast(mediaScanIntent);
//
//                try {
//                    mCamera.setPreviewDisplay(mHolder);
//                    mCamera.startPreview();
//                    Log.d(TAG, "Camera preview started");
//                } catch (Exception e){
//                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
//                }
//
//
//            } catch (FileNotFoundException e){
//                e.printStackTrace();
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//    }
//
//    private Intent callResult(HashMap<String, Object> map){
//        Intent result = new Intent(this.getContext(), ResultActivity.class);
//        result.putExtra("uri", poopy_uri);
//        result.putExtra("date",date);
//        result.putExtra("pid", currentPID);
//        return result;
//    }

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
