package com.example.sphinx.labyrinthe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sphinx.labyrinthe.adapter.GridPicListAdapter;
import com.example.sphinx.labyrinthe.util.SchemeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页,选择目标图片
 * Created by Sphinx on 2017/2/14.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 返回码：系统图库
    private static final int RESULT_IMAGE = 100;
    // 返回码：相机
    private static final int RESULT_CAMERA = 200;
    // IMAGE TYPE
    private static final String IMAGE_TYPE = "image/*";
    // 权限
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;
    // Temp照片路径
    public static String TEMP_IMAGE_PATH;
    // GridView 显示图片
    private GridView mGvPicList;
    private List<Bitmap> mPicList;
    // 主页图片资源ID
    private int[] mResPicId;

    private LayoutInflater mLayoutInflater;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private TextView mTvType2;
    private TextView mTvType3;
    private TextView mTvType4;

    // 游戏类型N*N
    private int mType = 2;
    // 本地图册、相机选择
    private String[] mCustomItems = new String[]{"本地图册", "相机拍照"};
    private TextView mTvSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        TEMP_IMAGE_PATH =
                Environment.getExternalStorageDirectory().getPath() + "/temp.png";
        mResPicId = new int[]{
                R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap.pic4, R.mipmap.pic5, R.mipmap.pic6
                , R.mipmap.pic7, R.mipmap.pic8, R.mipmap.pic9, R.mipmap.pic10, R.mipmap.pic11, R.mipmap.pic12
                , R.mipmap.pic13, R.mipmap.pic14, R.mipmap.pic15, R.mipmap.plus
        };

        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        mPicList = new ArrayList<>();
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mResPicId[i]);
            mPicList.add(bitmaps[i]);
        }

    }

    private void initView() {
        mTvSelected = (TextView) findViewById(R.id.tv_puzzle_main_type_selected);
        mGvPicList = (GridView) findViewById(R.id.gv_xpuzzle_main_pic_list);
        mGvPicList.setAdapter(new GridPicListAdapter(MainActivity.this, mPicList));
        mLayoutInflater = (LayoutInflater) getSystemService(
                LAYOUT_INFLATER_SERVICE);
        // mType view
        mPopupView = mLayoutInflater.inflate(
                R.layout.xpuzzle_main_type_selected, null);
        mTvType2 = (TextView) mPopupView.findViewById(R.id.tv_main_type_2);
        mTvType3 = (TextView) mPopupView.findViewById(R.id.tv_main_type_3);
        mTvType4 = (TextView) mPopupView.findViewById(R.id.tv_main_type_4);

        initListener();
    }

    private void initListener() {
        mTvType2.setOnClickListener(this);
        mTvType3.setOnClickListener(this);
        mTvType4.setOnClickListener(this);

        mTvSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevel(v);
            }
        }/*this*/);

        mGvPicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mResPicId.length - 1) {
                    //更多选择,调用本地
                    showLocalPhoto();
                } else {
                    //跳转到游戏
                    Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                    intent.putExtra("mType", mType);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    startActivity(intent);
                }
            }
        });

    }

    private void showLocalPhoto() {
        // 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择图片来源: ");
        builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
                    startActivityForResult(intent, RESULT_IMAGE);
                } else if (1 == which) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri pathUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, pathUri);
                    startActivityForResult(intent, RESULT_CAMERA);
                }
            }
        });

        builder.create().show();
    }

    private void showLevel(View view) {
        int density = (int) SchemeUtils.getDensity(this);
        mPopupWindow = new PopupWindow(mPopupView, 200 * density, 50 * density);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        Drawable transparent = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(transparent);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(
                view,
                Gravity.NO_GRAVITY,
                location[0] - 40 * density,
                location[1] + 30 * density);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Type
            case R.id.tv_main_type_2:
                mType = 2;
                mTvSelected.setText(" 2 X 2 ");
                break;
            case R.id.tv_main_type_3:
                mType = 3;
                mTvSelected.setText(" 3 X 3 ");
                break;
            case R.id.tv_main_type_4:
                mType = 4;
                mTvSelected.setText(" 4 X 4 ");
                break;
            case R.id.tv_puzzle_main_type_selected:
//                showLevel(v);
//                使用这种方式时，难度选择框无法弹出
                break;
            default:
                break;
        }
        mPopupWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_CAMERA /*&& data != null*/) {
                // 相机
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("mType", mType);
                intent.putExtra("mPicPath", TEMP_IMAGE_PATH);
                startActivity(intent);
            } else if (requestCode == RESULT_IMAGE && data != null) {
                // 相册
                Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("mType", mType);
                intent.putExtra("mPicPath", imagePath);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "权限拒绝,无法使用部分功能", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
