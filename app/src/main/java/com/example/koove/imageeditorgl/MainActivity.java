package com.example.koove.imageeditorgl;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    SeekBar contrastSeek, brightnessSeek, saturationSeek;
    Button none, lomo, autoFix, grain;
    RelativeLayout seekLayout;
    LinearLayout glSurfaceViewLayout;
    private int REQUEST_CAMERA = 1;
    private int SELECT_FILE = 2;
    private GLSurfaceView mEffectView;
    Bitmap thumbnail;
    String mCurrentPhotoPath = null;
    private int mImageWidth;
    private int mImageHeight;
    private EffectContext mEffectContext;
    private Effect mEffect;
    private boolean mInitialized = false;
    int imageSelected = 0;
    View seekView, glSurfaceView;
    int adjustBrightness = 0, adjustContrast = 0, adjustSharpness = 0, applyFilter = 0, lomofilter = 0;
    int noneFilter =0,autoFixFilter=0,grainFilter=0;

    private int[] mTextures = new int[2];
    private int[] outTexture = new int[2];
    private TextureRenderer mTexRenderer = new TextureRenderer();

    private String TAG = "OpenGL with Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekLayout = (RelativeLayout) findViewById(R.id.seekBarLayout);
        glSurfaceViewLayout = (LinearLayout) findViewById(R.id.glSurfaceViewLayout);
        contrastSeek = (SeekBar) findViewById(R.id.seek_bar_contrast);
        brightnessSeek = (SeekBar) findViewById(R.id.seek_bar_brightness);
        saturationSeek = (SeekBar) findViewById(R.id.seek_bar_saturation);

        seekView = findViewById(R.id.seekBarLayout);
        glSurfaceView = findViewById(R.id.glSurfaceViewLayout);

        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (imageSelected == 1) {

                    adjustBrightness = 1;
                    adjustContrast = 0;
                    adjustSharpness = 0;
                    applyFilter = 1;
                    mEffectView.requestRender();


                } else
                    Toast.makeText(getApplicationContext(),
                            "Please select an image before editing", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contrastSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (imageSelected == 1) {

                    adjustContrast = 1;
                    adjustBrightness = 0;
                    adjustSharpness = 0;
                    applyFilter = 1;
                    mEffectView.requestRender();


                } else
                    Toast.makeText(getApplicationContext(),
                            "Please select an image before editing", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saturationSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (imageSelected == 1) {
                    adjustSharpness = 1;
                    adjustContrast = 0;
                    adjustBrightness = 0;
                    applyFilter = 1;
                    mEffectView.requestRender();


                } else
                    Toast.makeText(getApplicationContext(),
                            "Please select an image before editing", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        none = (Button) findViewById(R.id.noneEffect);
        lomo = (Button) findViewById(R.id.lomo);
        autoFix = (Button) findViewById(R.id.autoFix);
        grain = (Button) findViewById(R.id.grain);

        lomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == 1) {
                    lomofilter = 1;
                    mEffectView.requestRender();
                    applyFilter = 1;
                    noneFilter=0;
                    autoFixFilter=0;
                    grainFilter=0;

                }
            }
        });

        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == 1) {
                    noneFilter = 1;
                    mEffectView.requestRender();
                    applyFilter = 1;
                    grainFilter=0;
                    autoFixFilter=0;
                    lomofilter=0;

                }
            }
        });

        autoFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == 1) {
                    autoFixFilter = 1;
                    mEffectView.requestRender();
                    applyFilter = 1;
                    lomofilter=0;
                    noneFilter=0;
                    grainFilter=0;

                }
            }
        });

        grain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == 1) {
                    grainFilter = 1;
                    mEffectView.requestRender();
                    applyFilter = 1;
                    lomofilter=0;
                    noneFilter=0;
                    autoFixFilter=0;

                }
            }
        });


        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        selectImage();

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(intent, REQUEST_CAMERA);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;

                        String root = Environment.getExternalStorageDirectory().toString();
                        photoFile = new File(root, "koove.jpg");
                        mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
                        if (photoFile.exists())
                            photoFile.delete();
                        if (photoFile != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }

                    }

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                galleryAddPic();
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath());
                File file = new File(directory, "koove.jpg");

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;


                BitmapFactory.decodeFile(file.getAbsolutePath().toString(), bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = Math.min(photoW / mEffectView.getWidth(), photoH / mEffectView.getHeight());

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath().toString(), bmOptions);


            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 700;
                int scale = 1;

                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;

                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);


            }
            imageSelected = 1;
            //mEffectView.requestRender();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {


        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (!mInitialized) {
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        } else {

            if (applyFilter == 0)
                loadTextures();

            if (applyFilter == 1) {

                initEffect();
                applyEffect();
            }

        }
        renderResult();


    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void loadTextures() {
        // Generate textures
        //if (imageSelected == 0)
            GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        if (true) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
            mImageWidth = bitmap.getWidth();
            mImageHeight = bitmap.getHeight();
            mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

            // Upload to texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        } else {

            mImageWidth = thumbnail.getWidth();
            mImageHeight = thumbnail.getHeight();
            mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

            // Upload to texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, thumbnail, 0);

        }

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {

            mEffect.release();

        } else if (adjustBrightness == 1) {

            //adjustBrightness=0;
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 2.0f);

        } else if (adjustContrast == 1) {

            adjustContrast = 0;
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
            mEffect.setParameter("contrast", 2.0f);

        } else if (adjustSharpness == 1) {

            adjustSharpness = 0;
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
            mEffect.setParameter("scale", 2.0f);

        } else if (lomofilter == 1) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);

        }
        else if (grainFilter == 1) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
        }
        else if (autoFixFilter == 1) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
        }
        else if (noneFilter == 1) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
        }
    }

    private void applyEffect() {

        Log.d(TAG,"meffect    "+mEffect.getName().toString());
        Log.d(TAG,"texture0 "+mTextures[0]);
        Log.d(TAG,"texture1 "+mTextures[1]);
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {

        if (applyFilter == 0)
            mTexRenderer.renderTexture(mTextures[0]);
        else
            mTexRenderer.renderTexture(mTextures[1]);


//        if (imageSelected == 0)
//            mTexRenderer.renderTexture(mTextures[0]);
//        else
//            mTexRenderer.renderTexture(mTextures[1]);

    }

}
