package com.lfgit.utilites;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.Arrays;

import static com.lfgit.utilites.Logger.LogMsg;

/**
 * Helper class providing URI to storage conversion
 * */
public class UriHelper {

    /** Returns a path to storage from a Uniform Resource Identifier (URI) */
    public static String getStoragePathFromURI(final Context context, final Uri uri) {
        Uri DocUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(uri));
        return getPath(context, DocUri);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":", 2);

                if ("primary".equalsIgnoreCase(split[0])) {
                    if (split[1].equals("")) {
                        return Environment.getExternalStorageDirectory().toString();
                    }
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    String rootStorage = "/storage/" + split[0];
                    if (split[1].equals("")) {
                        return rootStorage;
                    }
                    return rootStorage + "/" + split[1];
                }
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
