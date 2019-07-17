package com.byted.camp.todolist.debug;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DebugActivity extends AppCompatActivity {

    private static int REQUEST_CODE_STORAGE_PERMISSION = 1001;
    private TextView showFile;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        setTitle(R.string.action_debug);
        content="";

        final Button printBtn = findViewById(R.id.btn_print_path);
        final TextView pathText = findViewById(R.id.text_path);
        showFile=findViewById(R.id.text_show_file);

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(" Internal Private \n").append(getInternalPath())
                        .append(" External Private \n").append(getExternalPrivatePath())
                        .append(" External Public \n").append(getExternalPublicPath());
                pathText.setText(stringBuilder);
            }
        });

        final Button permissionBtn = findViewById(R.id.btn_request_permission);
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = ActivityCompat.checkSelfPermission(DebugActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (state == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DebugActivity.this, "already granted",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityCompat.requestPermissions(DebugActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            }
        });


/*        final Button overwriteBtn=findViewById(R.id.btn_overwrite);
        overwriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadInput(true);
            }
        });*/

        final Button appendBtn=findViewById(R.id.btn_add_content);
        appendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadInput(false);
            }
        });

        final Button showFilebtn=findViewById(R.id.btn_show_file);
        showFilebtn.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                new AsyncTask<Object, Integer, String>() {

                    @Override
                    protected String doInBackground(Object... objects) {
                        return ShowFile();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Log.d("debug", "as" + s);
                        showFile.setText(s);
                    }
                }.execute();
            }
        });
    }

    private void WriteFile(boolean isOverWrite){
        File s=getFilesDir();
        String file=s+"/test.txt";
        File f=new File(file);
        try{
            if(!f.createNewFile()){
                Log.d("debug","file exists");
                Log.d("debug","1"+content);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;

        try {
            Log.d("debug","2"+content);

            if(isOverWrite){
                fileWriter = new FileWriter(f);
            }
            else{
                fileWriter = new FileWriter(f,true);
            }
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileWriter != null){
                try {
                    fileWriter.close();
                    Toast.makeText(this, "Successfull", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void ReadInput(final boolean isOverwrite) {
        View dialogView=View.inflate(this,R.layout.input_file_content,null);

        final EditText editText=(EditText)dialogView.findViewById(R.id.edit_input_content);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Content");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                     content =editText.getText().toString();
                     if(content.equals("")){
                         Toast.makeText(DebugActivity.this,"Conten cannot be null!", Toast.LENGTH_SHORT).show();
                     }
                     else{
                         WriteFile(isOverwrite);
                         dialog.dismiss();
                     }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                content="";
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog= builder.create();
        alertDialog.setView(dialogView);
        alertDialog.show();
        Log.d("Debug","Dialog");

    }


    private String ShowFile(){
        File s = getFilesDir();
        String file = s + "/test.txt";
        File f = new File(file);

        try {
            if(!f.createNewFile())
                Log.d("debug", "onClick: file exists"  );
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        String line, text = "";
        try {
            fileReader = new FileReader(f);
            bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null)
                text += (line + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            int state = grantResults[0];
            if (state == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DebugActivity.this, "Granted",
                        Toast.LENGTH_SHORT).show();
            } else if (state == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(DebugActivity.this, "Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String getCanonicalPath(Map<String, File> dirMap) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String name : dirMap.keySet()) {
                sb.append(name)
                        .append(": ")
                        .append(dirMap.get(name).getCanonicalPath())
                        .append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getInternalPath() {
        Map<String, File> dirMap = new LinkedHashMap<>();
        dirMap.put("cacheDir", getCacheDir());
        dirMap.put("filesDir", getFilesDir());
        dirMap.put("customDir", getDir("custom", MODE_PRIVATE));
        return getCanonicalPath(dirMap);
    }

    private String getExternalPrivatePath() {
        Map<String, File> dirMap = new LinkedHashMap<>();
        dirMap.put("cacheDir", getExternalCacheDir());
        dirMap.put("filesDir", getExternalFilesDir(null));
        dirMap.put("picturesDir", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        return getCanonicalPath(dirMap);
    }

    private String getExternalPublicPath() {
        Map<String, File> dirMap = new LinkedHashMap<>();
        dirMap.put("rootDir", Environment.getExternalStorageDirectory());
        dirMap.put("picturesDir",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        return getCanonicalPath(dirMap);
    }


}
