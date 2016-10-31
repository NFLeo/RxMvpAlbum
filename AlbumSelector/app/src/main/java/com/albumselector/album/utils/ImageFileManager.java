package com.albumselector.album.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.albumselector.album.entity.FolderBean;
import com.albumselector.album.entity.ImageBean;
import com.albumselector.album.rxbus.RxBus;
import com.albumselector.album.rxbus.event.ConstantsEvent;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @desc:   本地文件读取管理类
 * @author: Leo
 * @date:   2016/10/27
 */
public class ImageFileManager
{
    private static int ImageNum = -1;
    private static final int mPageSize = 32;

    public static List<FolderBean> loadAllFolder(Context context)
    {
        //相册列表
        List<FolderBean> folderBeanList = new ArrayList<>();
        HashSet<String> albumSet = new HashSet<>();
        File file;

        ContentResolver contentResolver = context.getContentResolver();
        //查询项
        String[] projection = new String[] {
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.ORIENTATION,
            };

        //设置所有相册item
        int allFolderSize = 0;
        FolderBean allFolder = new FolderBean();
        allFolder.setFolderId(String.valueOf(Integer.MIN_VALUE));
        allFolder.setFolderName("所有图片");
        folderBeanList.add(allFolder);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC ");
        } catch (Exception ignored){
        }

        //循环查找图片
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToLast()) {
            do {
                String folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String folderCover = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                //添加封面图片
                if(TextUtils.isEmpty(allFolder.getFolderCover())) {
                    allFolder.setFolderCover(folderCover);
                }

                file = new File(folderCover);
                if (file.exists() && !albumSet.contains(folderName)) {

                    //获取数量
                    String[] array = file.getParentFile().list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg")
                                    || filename.endsWith(".PNG")
                                    || filename.endsWith(".JPG")
                                    || filename.endsWith(".JPEG"))
                                return true;
                            return false;
                        }
                    });

                    int folderSize = array == null ? 0 : array.length;

                    allFolder.setFolderSize(allFolder.getFolderSize() + folderSize);

                    folderBeanList.add(new FolderBean(folderId, folderName, folderCover, folderSize));
                    albumSet.add(folderName);
                }

            } while (cursor.moveToPrevious());
        }
        if (cursor != null) {
            cursor.close();
        }

        return folderBeanList;
    }

    /**
     * 分页查询相册照片
     * @param context     上下文
     * @param folderId    相册Id
     * @param PageIndex   分页序号
     * @param PageSize    分页大小
     * @return            照片列表
     */
    public static List<ImageBean> loadFolderImage(Context context, String folderId, int PageIndex, int PageSize)
    {
        int offset = (PageIndex -1) * PageSize;
        List<ImageBean> imageBeanList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Images.Media._ID);
        projection.add(MediaStore.Images.Media.TITLE);
        projection.add(MediaStore.Images.Media.DATA);
        projection.add(MediaStore.Images.Media.BUCKET_ID);
        projection.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        projection.add(MediaStore.Images.Media.MIME_TYPE);
        projection.add(MediaStore.Images.Media.DATE_ADDED);
        projection.add(MediaStore.Images.Media.LATITUDE);
        projection.add(MediaStore.Images.Media.LONGITUDE);
        projection.add(MediaStore.Images.Media.SIZE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Images.Media.HEIGHT);
        }

        String selection = null;
        String[] selectionArgs = null;
        if(!TextUtils.equals(folderId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{folderId};
        }
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection.toArray(new String[projection.size()]), selection,
                selectionArgs, MediaStore.Images.Media.DATE_ADDED +" DESC LIMIT " + PageSize +" OFFSET " + offset);

        if(cursor != null) {
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                do {
                    ImageBean imageBean = parseImageCursor(cursor);
                    imageBeanList.add(imageBean);
                } while (cursor.moveToNext());
            }
        }

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        return imageBeanList;
    }

    /**
     * 解析图片cursor
     * @param cursor cursor
     * @return 照片实体类
     */
    private static ImageBean parseImageCursor(Cursor cursor)
    {
        ImageBean imageBean = new ImageBean();
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        imageBean.setImageId(id);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
        imageBean.setImageTitle(title);
        String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        imageBean.setImagePath(originalPath);
        String folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        imageBean.setImageFolderId(folderId);
        String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        imageBean.setImageFolderName(bucketDisplayName);
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        imageBean.setMimeType(mimeType);
        long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        imageBean.setCreateDate(createDate);
        
        int width = 0, height = 0;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
        } else {
            try {
                ExifInterface exifInterface = new ExifInterface(originalPath);
                width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            } catch (IOException ignored) {
            }
        }
        imageBean.setWidth(width);
        imageBean.setHeight(height);
        double latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
        imageBean.setLatitude(latitude);
        double longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
        imageBean.setLongitude(longitude);
        long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
        imageBean.setImageSize(length);

        return imageBean;
    }

    public static List<ImageBean> loadAllImage(Context context, String folderId, int PageIndex, int PageSize)
    {
        ImageNum = 0;                  //初始化照片数量
        int curPageIndex = PageIndex;
        List<ImageBean> imageBeanList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        List<String> projection = new ArrayList<>();
        projection.add(MediaStore.Images.Media._ID);
        projection.add(MediaStore.Images.Media.TITLE);
        projection.add(MediaStore.Images.Media.DATA);
        projection.add(MediaStore.Images.Media.BUCKET_ID);
        projection.add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        projection.add(MediaStore.Images.Media.MIME_TYPE);
        projection.add(MediaStore.Images.Media.DATE_ADDED);
        projection.add(MediaStore.Images.Media.LATITUDE);
        projection.add(MediaStore.Images.Media.LONGITUDE);
        projection.add(MediaStore.Images.Media.SIZE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Images.Media.WIDTH);
            projection.add(MediaStore.Images.Media.HEIGHT);
        }

        do {
            imageBeanList.addAll(loadCursorImage(contentResolver, folderId, curPageIndex, mPageSize, projection));

            //当相册中图片数量少于分页固定值时跳出循环
            //此时说明相册中图片已经读取完
            if (ImageNum < mPageSize)
                break;

            curPageIndex ++;
        } while (imageBeanList.size() < mPageSize);

        System.out.println("ALBUM_ImageNum" + ImageNum + "||ALBUM_ImageList" + imageBeanList.size());

        ConstantsEvent.ImageRefreshIndex = curPageIndex - 1;
        RxBus.getDefault().post(ConstantsEvent.ImageRefreshIndex);

        return imageBeanList;
    }

    private static List<ImageBean> loadCursorImage(ContentResolver contentResolver, String folderId, int PageIndex, int PageSize, List<String> projection)
    {
        int offset = (PageIndex -1) * PageSize;
        List<ImageBean> imageBeanList = new ArrayList<>();
        Cursor cursor;

        String selection = null;
        String[] selectionArgs = null;
        if(!TextUtils.equals(folderId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{folderId};
        }

        File file;
        HashSet<Long> selectedImages = new HashSet<>();
        ImageBean imageBean;
        for (int i = 0, l = imageBeanList.size(); i < l; i++) {
            imageBean = imageBeanList.get(i);
            file = new File(imageBean.getImagePath());
            if (file.exists() && imageBean.isSelected()) {
                selectedImages.add(imageBean.getImageId());
            }
        }

        cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection.toArray(new String[projection.size()]),
                selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + PageSize +" OFFSET " + offset);

        if (cursor == null)
            return null;

        ImageNum = cursor.getCount();
                    /*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             */
        int tempCountSelected = 0;
        if (cursor.moveToLast()) {
            do {

                long id = cursor.getLong(cursor.getColumnIndex(projection.get(0)));
                String path = cursor.getString(cursor.getColumnIndex(projection.get(2)));
                boolean isSelected = selectedImages.contains(id);
                if (isSelected) {
                    tempCountSelected++;
                }

                file = new File(path);
                if (file.exists()) {
                    imageBeanList.add(parseImageCursor(cursor));
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return imageBeanList;
    }
}
