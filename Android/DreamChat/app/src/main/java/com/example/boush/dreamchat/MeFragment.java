package com.example.boush.dreamchat;



import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    private static int RESULT_LOAD_IMAGE = 1;
    private Button Select;
    private ImageView imageView;
    Context context;

    private String username;
    private String email;
    private String password;
    private Button submit;

    private ImageButton rotatePicture;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        Select = (Button) view.findViewById(R.id.selectPhoto);
        imageView = (ImageView) view.findViewById(R.id.ImageView);
        rotatePicture = (ImageButton) view.findViewById(R.id.imageButton);
        Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        rotatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate();
            }
        });

        context= getActivity();

        return view;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data)
        {
            Uri selectedImg = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImg,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            imageView .setImageBitmap(flip(BitmapFactory.decodeFile(picturePath), Direction.HORIZONTAL));

            cursor.close();
        }
    }

    public enum Direction { VERTICAL, HORIZONTAL };
    public static Bitmap flip(Bitmap src, Direction type) {
        Matrix matrix = new Matrix();

        if(type == Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);

        }
        else if(type == Direction.HORIZONTAL) {
                matrix.setRotate(240);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public void rotate(){
        Matrix matrix = new Matrix();
        matrix.setRotate(180);
    }

    public boolean updateUser(){
       return  false;
    }
}



