package acore.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import xh.basic.tool.UtilFile;
import xh.basic.tool.UtilImage;
import xh.basic.tool.UtilLog;
import xh.basic.tool.UtilString;
import acore.override.XHApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import aplug.basic.LoadImage;

import com.xiangha.R;

public class ImgManager extends UtilImage {

    // 获取圆角矩形背景图
    public static Drawable getRoundBackground(Context context, String color) {
        float dimenH = 22;
        float dimenW = 80;
        if (!context.getResources().getString(R.dimen.dp_22).equals("22.0dip")) {
            dimenH = (float) 35.3;
            dimenW = 128;
        }
        int height = ToolsDevice.dp2px(context, dimenH), width = ToolsDevice.dp2px(
                context, dimenW), round = ToolsDevice.dp2px(context, 3);
        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, width, height);
        // 产生一个红色的圆角矩形 或者任何有色颜色，不能是透明！
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor(color));
        canvas.drawRoundRect(outerRect, round, round, paint);
        return new BitmapDrawable(context.getResources(), output);
    }

    /**
     * 判断图片是否长宽成比
     *
     * @param path  : 图片路径
     * @param scale : 比例
     *
     * @return
     */
    public static boolean isQualified(String path, int scale) {
        // 配置bitmap，防止内存溢出
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int wi = options.outWidth;
        int hei = options.outHeight;
        // //Log.i("FRJ","wi:" + wi + "  hei:" + hei);
        return !(wi / hei >= scale || hei / wi >= scale);
    }

    /**
     * 删除长期存储图片
     *
     * @param imgUrl
     */
    public static void delImg(String imgUrl) {
        if (imgUrl.length() == 0)
            return;
        String name = UtilString.toMD5(imgUrl, false);
        UtilFile.delDirectoryOrFile(UtilFile.getSDDir() + LoadImage.SAVE_LONG + "/" + name, 0);
    }

    /**
     * 长期存储图片到本地，需在多线程中调用
     *
     * @param imgUrl
     * @param type
     */
    public static void saveImg(String imgUrl, String type) {
        if (TextUtils.isEmpty(imgUrl))
            return;
        String name = UtilString.toMD5(imgUrl, false);
        // 图片不存在则下载
        if (UtilFile.ifFileModifyByCompletePath(UtilFile.getSDDir() + type + "/" + name, -1) == null) {
            LoadImage.with(XHApplication.in())
                    .load(imgUrl)
                    .setSaveType(type)
                    .preload();
        }
    }

    public static void saveImgLong(String imgUrl){
        saveImg(imgUrl,LoadImage.SAVE_LONG);
    }

    public static void loadLongImage(ImageView imageView, String imgUrl){
        loadImage(imageView,imgUrl,LoadImage.SAVE_LONG);
    }

    public static void loadCacheImage(ImageView imageView, String imgUrl){
        loadImage(imageView,imgUrl,LoadImage.SAVE_CACHE);
    }

    public static void loadImage(ImageView imageView, String imgUrl, String type){
        if (TextUtils.isEmpty(imgUrl) || null == imageView)
            return;
        String name = UtilString.toMD5(imgUrl, false);
        final String imagePath = UtilFile.getSDDir() + type + "/" + name;
        if (UtilFile.ifFileModifyByCompletePath(imagePath, -1) == null) {
            LoadImage.with(XHApplication.in())
                    .load(imgUrl)
                    .setSaveType(type)
                    .build()
                    .into(imageView);
        }else{
            try{
                InputStream inputStream = FileManager.loadFile(imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                LoadImage.with(XHApplication.in())
                        .load(imgUrl)
                        .setSaveType(type)
                        .build()
                        .into(imageView);
            }
        }
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bitmap
     *
     * @return
     */
    public static InputStream bitmapToInputStream(Bitmap bitmap, int kb) {
        int options = 99;
        InputStream is = null;
        byte[] theByte = null;
        int num = 1;
        Bitmap oldBitmap = bitmap;
        // 压缩
        while (bitmap != null && options >= 0) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean isOk = bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                while (!isOk && num < 2) {
                    UtilLog.reportError("bitmap.compress error:" + num, null);
                    options = 99;
                    bitmap = oldBitmap;
                    isOk = bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    num++;
                }
                if (!isOk) {
                    oldBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                theByte = baos.toByteArray();
                // LogManager.print("d", bitmap.getWidth() + "*" + bitmap.getHeight() + "正在压缩质量"
                // + options + "结果" + (theByte.length / 1024));
                if (theByte.length / 1024 < kb || kb == 0 || options <= 3 || !isOk) {
                    is = new ByteArrayInputStream(theByte);
                    baos.close();
                    break;
                }
                baos.close();
                options -= 1000 / options;
                if (options <= 0)
                    options = 3;
            } catch (Exception e) {
                UtilLog.reportError("图片压缩", e);
                break;
            }
        }
        return is;
    }

    /**
     * @param bitmap 原图
     * @param coverBitmap 选图
     *
     * @return 根据原图尺寸居中裁剪选图
     */
    public static Bitmap centerScaleBitmap(Bitmap bitmap, Bitmap coverBitmap) {
        if (null == bitmap || coverBitmap == null) {
            return null;
        }
        return scaleBitmap(coverBitmap,bitmap.getWidth(),bitmap.getHeight());
//        //宽高比
////		double imgWhB = bitmap.getWidth() * 1.0 / bitmap.getHeight();
//
//		final int imageWidth = coverBitmap.getWidth();
//		final int imageHieght = coverBitmap.getHeight();
//
//		if(imgWhB > 0 &&  imageHieght * imgWhB != imageWidth) {
//			int newImgW = imageWidth;
//			int newImgH = (int) (imageWidth / imgWhB);
//			if (newImgH > imageHieght) {
//				newImgH = imageHieght;
//				newImgW = (int) (imageHieght * imgWhB);
//				return Bitmap.createBitmap(coverBitmap, (imageWidth - newImgW) / 2, 0, newImgW, newImgH);
//			} else {
//				return Bitmap.createBitmap(coverBitmap, 0, (imageHieght - newImgH) / 2, newImgW, newImgH);
//			}
//		}
//
//		return coverBitmap;
    }

    /**
     * @param coverBitmap 选图
     *
     * @return 16:9裁剪选图
     */
    public static Bitmap defaultScaleBitmap(Bitmap coverBitmap){
        return scaleBitmap(coverBitmap,16.0f,9.0f);
    }

    /**
     * 按狂傲裁剪图片
     * @param coverBitmap 需要裁剪的图片
     * @param widht 宽
     * @param height 高
     * @return 裁剪后的图片
     */
    public static Bitmap scaleBitmap(Bitmap coverBitmap,float widht,float height){
        if(coverBitmap == null || coverBitmap.isRecycled()){
            return null;
        }

        final int imageWidth = coverBitmap.getWidth();
        final int imageHieght = coverBitmap.getHeight();
        Bitmap newBitmap = coverBitmap;
        if(imageWidth * height != imageHieght * widht){
            int newImgW = imageWidth;
            int newImgH = (int) (newImgW * height / widht);
            if (newImgH > imageHieght) {
                newImgH = imageHieght;
                newImgW = (int) (imageHieght * widht / height);
                return Bitmap.createBitmap(newBitmap, (imageWidth - newImgW) / 2, 0, newImgW, newImgH);
            } else {
                return Bitmap.createBitmap(newBitmap, 0, (imageHieght - newImgH) / 2, newImgW, newImgH);
            }
        }
        return coverBitmap;
    }

}
