package com.ihuntto.motioneventwriter;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MotionEventWriter implements Handler.Callback {
    private static final String TAG = MotionEventWriter.class.getSimpleName();
    private static final String SEPARATOR = ",";
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final String mFileName;
    private final String mFolderName;
    private File mFile;

    public MotionEventWriter(String folderName, String fileName) {
        mHandlerThread = new HandlerThread(MotionEventWriter.class.getSimpleName());
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), this);
        mFolderName = folderName;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.UK);
        mFileName = fileName + "-" + format.format(date);
    }

    public void write(MotionEvent event) {
        Message msg = mHandler.obtainMessage();
        msg.obj = event;
        mHandler.sendMessage(msg);
    }

    public void close() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
    }

    private void write(FileWriter fileWriter, MotionEvent event) throws IOException {
        int action = event.getActionMasked();
        StringBuilder sb = new StringBuilder();
        if (action == MotionEvent.ACTION_DOWN) {
            appendHeader(sb);
        }
        MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
        for (int i = 0; i < event.getPointerCount(); i++) {
            int id = event.getPointerId(i);
            for (int pos = 0; pos < event.getHistorySize(); pos++) {
                event.getHistoricalPointerCoords(i, pos, pointerCoords);
                long time = event.getHistoricalEventTime(pos);
                appendData(sb, id, time, pointerCoords);
            }
            event.getPointerCoords(i, pointerCoords);
            long time = event.getEventTime();
            appendData(sb, id, time, pointerCoords);
        }
        fileWriter.write(sb.toString());
    }

    private void appendData(StringBuilder sb, int id, long time, MotionEvent.PointerCoords pointerCoords) {
        sb.append(id).append(SEPARATOR)
                .append(time).append(SEPARATOR)
                .append(pointerCoords.x).append(SEPARATOR)
                .append(pointerCoords.y).append(SEPARATOR)
                .append(pointerCoords.pressure).append(SEPARATOR)
                .append(pointerCoords.touchMajor).append(SEPARATOR)
                .append(pointerCoords.touchMinor).append(NEW_LINE);
    }

    private void appendHeader(StringBuilder sb) {
        sb.append("id").append(SEPARATOR)
                .append("time").append(SEPARATOR)
                .append("x").append(SEPARATOR)
                .append("y").append(SEPARATOR)
                .append("pressure").append(SEPARATOR)
                .append("major").append(SEPARATOR)
                .append("minor").append(NEW_LINE);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        MotionEvent event = (MotionEvent) msg.obj;
        FileWriter fileWriter = null;
        try {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                mFile = getFile(mFolderName, mFileName);
                Log.d(TAG, mFile.getPath());
            }
            fileWriter = new FileWriter(mFile, true);

            write(fileWriter, event);

            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e1) { /* fail silently */ }
            }
            return false;
        }
    }

    @NonNull
    static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    private File getFile(@NonNull String folderName, @NonNull String fileName) {
        checkNotNull(folderName);
        checkNotNull(fileName);

        File folder = new File(folderName);
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs();
        }

        int newFileCount = 0;
        File newFile;

        newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
        while (newFile.exists()) {
            newFileCount++;
            newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
        }

        return newFile;
    }
}
