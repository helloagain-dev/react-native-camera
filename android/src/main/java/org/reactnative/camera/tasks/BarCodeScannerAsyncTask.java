package org.reactnative.camera.tasks;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.reactnative.facedetector.RNFrameFactory;

public class BarCodeScannerAsyncTask extends android.os.AsyncTask<Void, Void, Barcode> {
  private final BarcodeDetector mDetector;
  private byte[] mImageData;
  private int mWidth;
  private int mHeight;
  private BarCodeScannerAsyncTaskDelegate mDelegate;

  //  note(sjchmiela): From my short research it's ok to ignore rotation of the image.
  public BarCodeScannerAsyncTask(
      BarCodeScannerAsyncTaskDelegate delegate,
      BarcodeDetector detector,
      byte[] imageData,
      int width,
      int height
  ) {
    mImageData = imageData;
    mWidth = width;
    mHeight = height;
    mDelegate = delegate;
    mDetector = detector;
  }

  @Override
  protected Barcode doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null) {
      return null;
    }

    SparseArray<Barcode> result = mDetector.detect(RNFrameFactory.buildFrame(mImageData, mWidth, mHeight, 0).getFrame());

    if (result.size() > 0) {
      return result.valueAt(0);
    } else {
      return null;
    }
  }

  @Override
  protected void onPostExecute(Barcode result) {
    super.onPostExecute(result);
    if (result != null) {
      mDelegate.onBarCodeRead(result);
    }
    mDelegate.onBarCodeScanningTaskCompleted();
  }
}
