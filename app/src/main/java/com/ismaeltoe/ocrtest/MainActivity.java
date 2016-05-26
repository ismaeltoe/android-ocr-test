/*
 * Copyright 2016 Ismael Toé
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ismaeltoe.ocrtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private ProgressBar mProgressBarView;
    private TextView mErrorView;
    private TextView mResultView;

    private final String mSavePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator
            + "tessdata" + File.separator;
    private final String mBaseUrl = "https://raw.githubusercontent.com/tesseract-ocr/tessdata/master/";

    private static final int SCAN_REQUEST = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST && resultCode == Activity.RESULT_OK) {
            mResultView.setText(data.getStringExtra(ScannerActivity.SCAN_RESULT));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mProgressBarView = (ProgressBar) findViewById(R.id.progressBar);
        mErrorView = (TextView) findViewById(R.id.error);
        mResultView = (TextView) findViewById(R.id.result);
    }

    public void onClickDownload(final View view) {
        final FileDownloadListener downloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                mErrorView.setText("");
                mProgressBarView.setIndeterminate(true);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                mErrorView.setText("");
                mProgressBarView.setIndeterminate(false);
                mProgressBarView.setMax(totalBytes);
                mProgressBarView.setProgress(soFarBytes);
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                mErrorView.setText("");
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                mErrorView.setText("");
                mProgressBarView.setIndeterminate(false);
                mProgressBarView.setProgress(task.getSmallFileTotalBytes());
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                mErrorView.setText("");
                mProgressBarView.setIndeterminate(false);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                mErrorView.setText(e.getMessage());
                mProgressBarView.setIndeterminate(false);
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                mErrorView.setText("");
                mProgressBarView.setIndeterminate(false);
            }
        };

        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);

        final List<BaseDownloadTask> tasks = new ArrayList<>();

        String[] files = {
                "fra.cube.bigrams",
                "fra.cube.fold",
                "fra.cube.lm",
                "fra.cube.nn",
                "fra.cube.params",
                "fra.cube.size",
                "fra.cube.word-freq",
                "fra.tesseract_cube.nn",
                "fra.traineddata"
        };

        for (int i = 0, count = files.length; i < count; i++) {
            tasks.add(FileDownloader.getImpl().create(mBaseUrl + files[i])
                    .setPath(mSavePath + files[i])
                    .setTag(i + 1));
        }

        queueSet.disableCallbackProgressTimes();

        // Each task will auto retry 1 time if download fail.
        queueSet.setAutoRetryTimes(1);

        queueSet.downloadTogether(tasks);

        queueSet.start();
    }

    public void onClickScan(final View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, SCAN_REQUEST);
    }

}
