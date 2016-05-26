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

import com.ismaeltoe.ocrtest.views.TessScannerView;

public class ScannerActivity extends AppCompatActivity implements TessScannerView.ResultHandler {

    public static final String SCAN_RESULT = "scan_result";

    private TessScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new TessScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(SCAN_RESULT, result.getText());
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }
}
