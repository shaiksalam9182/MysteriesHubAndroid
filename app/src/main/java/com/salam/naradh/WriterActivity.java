package com.salam.naradh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import top.defaults.colorpicker.ColorPickerPopup;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WriterActivity extends AppCompatActivity {

    Editor editor;
    Bitmap mBitMap;
    ApiService apiService;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    String imageUrl = "";

    android.support.v7.widget.Toolbar toolbar;

    ImageButton btnRender;

    EditText etTitle;

    String title,description,phone,token,extractedimage = "";
    SharedPreferences sd;
    SharedPreferences.Editor edit;
    String[] categories =new String[]{"Posts","Places","Aliens","Movies"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);

        btnRender = (ImageButton)findViewById(R.id.btnRender);
        etTitle = (EditText)findViewById(R.id.et_title);
        editor = (Editor)findViewById(R.id.editor);

        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        edit = sd.edit();

        phone = sd.getString("phone","");
        token =sd.getString("token","");

        btnRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               title = etTitle.getText().toString();
               description = editor.getContentAsHTML();
               if (title.equalsIgnoreCase("")||description.equalsIgnoreCase("")){
                   Toast.makeText(WriterActivity.this,"Fields are empty",Toast.LENGTH_LONG).show();
               }else {

                   AlertDialog.Builder builder = new AlertDialog.Builder(WriterActivity.this);
                   builder.setTitle("Select Category in which you want to post");
                   builder.setSingleChoiceItems(categories, -1, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
//                           Toast.makeText(WriterActivity.this,categories[which],Toast.LENGTH_LONG).show();
                           Pattern pattern = Pattern.compile("(<img .*?>)");
                           Matcher matcher = pattern.matcher(description);
                           if (matcher.find()){
                               extractedimage = pullLinks(matcher.group(1)).get(0);
                           }else {
                               Log.e("foundImages","No images found");
                           }
                           if (categories[which].equalsIgnoreCase("Posts")){
                               new AsyncSendPost().execute();
                           }else if (categories[which].equalsIgnoreCase("Places")){
                               new AsyncSendPlaces().execute();
                           }else if (categories[which].equalsIgnoreCase("Aliens")){
                               new AsyncSendAliens().execute();
                           }else if (categories[which].equalsIgnoreCase("Movies")){
                               new AsyncSendMovies().execute();
                           }
                           dialog.dismiss();

                       }
                   });
                   AlertDialog alert = builder.create();
                   alert.show();

               }
            }
        });

//        toolbar = (Toolbar) findViewById(R.id.tb_write);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);




        setupEditor();

        askPermissions();

    }

    private void askPermissions() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.sendandsave,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_send:
//                String data = editor.getContentAsHTML();
//                Log.e("writtendata",data);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void setupEditor() {




        findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H1);
            }
        });

        findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H2);
            }
        });

        findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H3);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(false);
            }
        });

        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(true);
            }
        });

        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertDivider();
            }
        });


        findViewById(R.id.action_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerPopup.Builder(WriterActivity.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(findViewById(android.R.id.content), new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                Toast.makeText(WriterActivity.this, "picked" + colorHex(color), Toast.LENGTH_LONG).show();
                                editor.updateTextColor(colorHex(color));
                            }



                        });


            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertLink();
            }
        });


        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });
        //editor.dividerBackground=R.drawable.divider_background_dark;
        //editor.setFontFace(R.string.fontFamily__serif);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);
        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setEditorImageLayout(R.layout.tmpl_image_view);
        editor.setListItemLayout(R.layout.tmpl_list_item);
        //editor.setNormalTextSize(10);
        // editor.setEditorTextColor("#FF3333");
        //editor.StartEditor();


        editor.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, final String uuid) {
                Toast.makeText(WriterActivity.this, uuid, Toast.LENGTH_LONG).show();
                /**
                 * TODO do your upload here from the bitmap received and all onImageUploadComplete(String url); to insert the result url to
                 * let the editor know the upload has completed
                 */


                initRetrofitClient();
                File filesDir = getApplicationContext().getFilesDir();
                File file = new File(filesDir,"image"+".png");


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG,0,bos);
                byte[] bitmapdata = bos.toByteArray();


                try {
                    FileOutputStream fos =new FileOutputStream(file);

                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();


                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"),file);

                    MultipartBody.Part body = MultipartBody.Part.createFormData("upload",file.getName(),reqFile);

                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"),"upload");


                    Call<ResponseBody> req =apiService.postImage(body,name);
                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.e("ImageUpload",response.message()+"\n"+response.body()+"\n"+response.raw().toString());
//                    String[] data = response.raw().toString().split(",");
//                    String[] imgurl = data[3].split("=");
//                    String finalUrl = imgurl[1].replace("\\}","");
//                    Log.e("finalUrl",finalUrl);
                            String imageurl = pullLinks(response.raw().toString()).get(0);
                            editor.onImageUploadComplete(imageurl,uuid);

//                    Glide.with(WriterActivity.this).asBitmap().load(imageurl).into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            editor.insertImage(resource);
//                        }
//                    });
                            imageUrl = imageurl;
//                    editor.onImageUploadComplete(imageurl,"daifjeli");

                            if (response.code()==200){
                                Toast.makeText(WriterActivity.this,"Uploaded successfully",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(WriterActivity.this, "Request failed0", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(WriterActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }




//                editor.onImageUploadComplete("http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg", uuid);
                // editor.onImageUploadFailed(uuid);
            }

            @Override
            public View onRenderMacro(String name, Map<String, Object> props, int index) {
                View view = getLayoutInflater().inflate(R.layout.layout_authored_by, null);
                return view;
            }

        });


        /**
         * rendering serialized content
         // */
        //  String serialized = "{\"nodes\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003etextline 1 a great time and I will branch office is closed on Sundays\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"H1\"],\"textSettings\":{\"textColor\":\"#c00000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003ethe only one that you have received the stream free and open minded person to discuss a business opportunity to discuss my background.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"it is a great weekend and we will have the same to me that the same a great time\"],\"contentStyles\":[\"BOLD\"],\"textSettings\":{\"textColor\":\"#FF0000\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eI have a place where I have a great time and I will branch manager state to boast a new job in a few weeks and we can host or domain to get to know.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"the stream of water in a few weeks and we can host in the stream free and no ippo\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#5E5E5E\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I can get it done today will online at location and I am not a big difference to me so that we are headed \\u003ca href\\u003d\\\"www.google.com\\\"\\u003ewww.google.com\\u003c/a\\u003e it was the only way I.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not a good day to get the latest version to blame it to the product the.\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"BOLDITALIC\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I can send me your email to you and I am not able a great time and consideration I have to do the needful.\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"INDENT\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eI will be a while ago to a great weekend a great time with the same.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"}]}";
//        String serialized = "{\"nodes\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003e\\u003cspan style\\u003d\\\"color:#000000;\\\"\\u003e\\u003cspan style\\u003d\\\"color:#000000;\\\"\\u003eit is not available beyond that statue in a few days and then we could\\u003c/span\\u003e\\u003c/span\\u003e\\u003c/p\\u003e\\n\"],\"contentStyles\":[\"H1\"],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"author-tag\"],\"macroSettings\":{\"data-author-name\":\"Alex Wong\",\"data-tag\":\"macro\",\"data-date\":\"12 July 2018\"},\"type\":\"macro\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is a free trial to get a great weekend a good day to you u can do that for.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is that I have to do the needful as early in life is not available beyond my imagination to be a good.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"childs\":[{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003e\\u003cb\\u003eit is not available in the next week or two and I have a place where I\\u003c/b\\u003e\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#006AFF\"},\"type\":\"IMG_SUB\"}],\"content\":[\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\"],\"type\":\"img\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not available in the next week to see you tomorrow morning to see you then.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"},{\"content\":[],\"type\":\"hr\"},{\"content\":[\"\\u003cp dir\\u003d\\\"ltr\\\"\\u003eit is not available in the next day delivery to you soon with it and.\\u003c/p\\u003e\\n\"],\"contentStyles\":[],\"textSettings\":{\"textColor\":\"#000000\"},\"type\":\"INPUT\"}]}";
        // EditorContent des = editor.getContentDeserialized(serialized);
        // editor.render(des);

//        Intent intent = new Intent(getApplicationContext(), RenderTestActivity.class);
//        intent.putExtra("content", serialized);
//        startActivity(intent);


        /**
         * Rendering html
         */
        //render();
        //editor.render();  // this method must be called to start the editor
//        String text = "<h1 data-tag=\"input\" style=\"color:#c00000;\"><span style=\"color:#C00000;\">textline 1 a great time and I will branch office is closed on Sundays</span></h1><hr data-tag=\"hr\"/><p data-tag=\"input\" style=\"color:#000000;\">the only one that you have received the stream free and open minded person to discuss a business opportunity to discuss my background.</p><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><p data-tag=\"img-sub\" style=\"color:#FF0000;\" class=\"editor-image-subtitle\"><b>it is a great weekend and we will have the same to me that the same a great time</b></p></div><p data-tag=\"input\" style=\"color:#000000;\">I have a place where I have a great time and I will branch manager state to boast a new job in a few weeks and we can host or domain to get to know.</p><div data-tag=\"img\"><img src=\"http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg\" /><p data-tag=\"img-sub\" style=\"color:#5E5E5E;\" class=\"editor-image-subtitle\">the stream of water in a few weeks and we can host in the stream free and no ippo</p></div><p data-tag=\"input\" style=\"color:#000000;\">it is that I can get it done today will online at location and I am not a big difference to me so that we are headed <a href=\"www.google.com\">www.google.com</a> it was the only way I.</p><blockquote data-tag=\"input\" style=\"color:#000000;\">I have to do the negotiation and a half years old story and I am looking forward in a few days.</blockquote><p data-tag=\"input\" style=\"color:#000000;\">it is not a good day to get the latest version to blame it to the product the.</p><ol data-tag=\"ol\"><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">it is that I can send me your email to you and I am not able a great time and consideration I have to do the needful.</span></li><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">I have to do the needful and send to me and</span></li><li data-tag=\"list-item-ol\"><span style=\"color:#000000;\">I will be a while ago to a great weekend a great time with the same.</span></li></ol><p data-tag=\"input\" style=\"color:#000000;\">it was u can do to make an offer for a good day I u u have been working with a new job to the stream free and no.</p><p data-tag=\"input\" style=\"color:#000000;\">it was u disgraced our new home in time to get the chance I could not find a good idea for you have a great.</p><p data-tag=\"input\" style=\"color:#000000;\">I have to do a lot to do the same a great time and I have a great.</p><p data-tag=\"input\" style=\"color:#000000;\"></p>";
        //editor.render("<p>Hello man, whats up!</p>");
        //String text = "<p data-tag=\"input\" style=\"color:#000000;\">I have to do the needful and send to me and my husband is in a Apple has to offer a variety is not a.</p><p data-tag=\"input\" style=\"color:#000000;\">I have to go with you will be highly grateful if we can get the latest</p><blockquote data-tag=\"input\" style=\"color:#000000;\">I have to do the negotiation and a half years old story and I am looking forward in a few days.</blockquote><p data-tag=\"input\" style=\"color:#000000;\">I have to do the needful at your to the product and the other to a new job is going well and that the same old stuff and a half day city is the stream and a good idea to get onboard the stream.</p>";
//        editor.render(text);
//        findViewById(R.id.btnRender).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /*
//                Retrieve the content as serialized, you could also say getContentAsHTML();
//                */
//                String text = editor.getContentAsSerialized();
//                editor.getContentAsHTML();
//                Intent intent = new Intent(getApplicationContext(), RenderTestActivity.class);
//                intent.putExtra("content", text);
//                startActivity(intent);
//            }
//        });


        /**
         * Since the endusers are typing the content, it's always considered good idea to backup the content every specific interval
         * to be safe.
         *
         private final long backupInterval = 10 * 1000;
         Timer timer = new Timer();
         timer.scheduleAtFixedRate(new TimerTask() {
        @Override public void run() {
        String text = editor.getContentAsSerialized();
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        preferences.putString(String.format("backup-{0}",  new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date())), text);
        preferences.apply();
        }
        }, 0, backupInterval);
         */

    }

    private String colorHex(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "#%02X%02X%02X", r, g, b);
    }


    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/GreycliffCF-Bold.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/GreycliffCF-Bold.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                 mBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                 editor.insertImage(mBitMap);



//                 uploadImage();



                // Log.d(TAG, String.valueOf(bitmap));
//                editor.insertImage(bitmap);
//                editor.onImageUploadComplete();

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            // editor.RestoreState();
        }else if (requestCode==ALL_PERMISSIONS_RESULT){
            for (String perms : permissionsToRequest) {
                if (!hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }

            if (permissionsRejected.size() > 0) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                });
                        return;
                    }
                }

            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://206.189.132.139").client(client).build().create(ApiService.class);
    }

    private void uploadImage() {
        initRetrofitClient();
        File filesDir = getApplicationContext().getFilesDir();
        File file = new File(filesDir,"image"+".png");


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitMap.compress(Bitmap.CompressFormat.PNG,0,bos);
        byte[] bitmapdata = bos.toByteArray();


        try {
            FileOutputStream fos =new FileOutputStream(file);

            fos.write(bitmapdata);
            fos.flush();
            fos.close();




            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"),file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("upload",file.getName(),reqFile);

            RequestBody name = RequestBody.create(MediaType.parse("text/plain"),"upload");


            Call<ResponseBody> req =apiService.postImage(body,name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.e("ImageUpload",response.message()+"\n"+response.body()+"\n"+response.raw().toString());
//                    String[] data = response.raw().toString().split(",");
//                    String[] imgurl = data[3].split("=");
//                    String finalUrl = imgurl[1].replace("\\}","");
//                    Log.e("finalUrl",finalUrl);
                    String imageurl = pullLinks(response.raw().toString()).get(0);
//                    editor.insertLink(imageurl);

//                    editor.onImageUploadComplete(imageurl,);


                    Glide.with(WriterActivity.this).asBitmap().load(imageurl).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            editor.insertImage(resource);
                        }
                    });
                    imageUrl = imageurl;
//                    editor.onImageUploadComplete(imageurl,"daifjeli");

                    if (response.code()==200){
                        Toast.makeText(WriterActivity.this,"Uploaded successfully",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(WriterActivity.this, "Request failed0", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(WriterActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<String> pullLinks(String text)
    {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while(m.find())
        {
            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }

            links.add(urlStr);
        }

        return links;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Editor?")
                .setMessage("Are you sure you want to exit the editor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        setGhost((Button) findViewById(R.id.btnRender));
    }

    private class AsyncSendPost extends AsyncTask<Void,Void,JSONObject> {

        ProgressDialog pdLoading = new ProgressDialog(WriterActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Sending your post..");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("title",title);
                data.put("description",description);
                data.put("device_type","Android");
                data.put("user_published","1");
                data.put("published","0");
                data.put("image",extractedimage);
                PostHelper postHelper = new PostHelper(WriterActivity.this);
                return postHelper.Post(URLUtils.sendPost,data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdLoading.dismiss();
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(WriterActivity.this,"Successfully posted",Toast.LENGTH_LONG).show();
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(WriterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private class AsyncSendPlaces extends AsyncTask<Void,Void,JSONObject>{

        ProgressDialog pdLoading = new ProgressDialog(WriterActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Sending your post...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {


            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("title",title);
                data.put("description",description);
                data.put("device_type","Android");
                data.put("user_published","1");
                data.put("published","0");
                data.put("image",extractedimage);
                PostHelper postHelper = new PostHelper(WriterActivity.this);
                return postHelper.Post(URLUtils.sendPlace,data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdLoading.dismiss();
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(WriterActivity.this,"Successfully posted",Toast.LENGTH_LONG).show();
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(WriterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncSendAliens extends AsyncTask<Void,Void,JSONObject>{

        ProgressDialog pdLoading = new ProgressDialog(WriterActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Sending your post...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("title",title);
                data.put("description",description);
                data.put("device_type","Android");
                data.put("user_published","1");
                data.put("published","0");
                data.put("image",extractedimage);
                PostHelper postHelper = new PostHelper(WriterActivity.this);
                return postHelper.Post(URLUtils.sendAlien,data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdLoading.dismiss();
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(WriterActivity.this,"Successfully posted",Toast.LENGTH_LONG).show();
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(WriterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncSendMovies extends AsyncTask<Void,Void,JSONObject>{
        ProgressDialog pdLoading = new ProgressDialog(WriterActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Sending your post...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("title",title);
                data.put("description",description);
                data.put("device_type","Android");
                data.put("user_published","1");
                data.put("published","0");
                data.put("image",extractedimage);
                PostHelper postHelper = new PostHelper(WriterActivity.this);
                return postHelper.Post(URLUtils.sendMovie,data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdLoading.dismiss();
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(WriterActivity.this,"Successfully posted",Toast.LENGTH_LONG).show();
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(WriterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
