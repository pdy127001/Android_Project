package com.example.project4;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OCRHelper {
    private TessBaseAPI tessBaseAPI;
    private static final String TAG = "OCRHelper"; // 로그 태그 추가
    private static final String TESSDATA_SUBFOLDER = "tessdata";
    private static final String LANGUAGE = "eng";  // digits1.traineddata 사용
//OCR Helper
    public OCRHelper(Context context) {
        try {
            tessBaseAPI = new TessBaseAPI();

            String dataPath = context.getFilesDir() + "/";
            Log.d(TAG, "TessData path: " + dataPath + TESSDATA_SUBFOLDER);

            // tessdata 폴더 확인 및 생성
            File tessdataDir = new File(dataPath + TESSDATA_SUBFOLDER);
            if (!tessdataDir.exists()) {
                Log.d(TAG, "Tessdata directory does not exist. Creating it...");
                tessdataDir.mkdirs();
                copyTessDataFiles(context, TESSDATA_SUBFOLDER);
            } else {
                Log.d(TAG, "Tessdata directory exists.");
            }

            // "digits1" 훈련 데이터 사용
            Log.d(TAG, "Initializing Tesseract with language: " + LANGUAGE);
            if (!tessBaseAPI.init(dataPath, LANGUAGE)) {
                Log.e(TAG, "Tesseract initialization failed for language: " + LANGUAGE);
                throw new RuntimeException("Tesseract initialization failed.");
            }

            Log.d(TAG, "Tesseract initialization successful.");

            // 숫자만 허용하도록 설정
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");
        } catch (Exception e) {
            Log.e(TAG, "OCR initialization error: " + e.getMessage(), e);
            throw new RuntimeException("OCR initialization failed: " + e.getMessage());
        }
    }

    public String recognizeText(Bitmap bitmap) {
        if (tessBaseAPI == null) {
            Log.e(TAG, "Tesseract API is not initialized.");
            return null;
        }

        if (bitmap == null) {
            Log.e(TAG, "Input bitmap is null");
            return null;
        }

        try {
            // 전처리 과정 적용
            Bitmap processedBitmap = preprocessImage(bitmap);

            // OCR 수행
            tessBaseAPI.setImage(processedBitmap);
            String result = tessBaseAPI.getUTF8Text();
            tessBaseAPI.clear();
            Log.d(TAG, "OCR Result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error during OCR recognition: " + e.getMessage(), e);
            return null;
        }
    }

    public void close() {
        if (tessBaseAPI != null) {
            Log.d(TAG, "Releasing Tesseract resources.");
            tessBaseAPI.end();
        }
    }

    private void copyTessDataFiles(Context context, String path) {
        try {
            String[] fileList = context.getAssets().list(path);

            for (String fileName : fileList) {
                String pathToDataFile = context.getFilesDir() + "/" + path + "/" + fileName;
                File dataFile = new File(pathToDataFile);

                if (!dataFile.exists()) {
                    Log.d(TAG, "Copying " + fileName + " to " + pathToDataFile);
                    InputStream in = context.getAssets().open(path + "/" + fileName);
                    FileOutputStream out = new FileOutputStream(pathToDataFile);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();
                } else {
                    Log.d(TAG, fileName + " already exists.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error copying tessdata files: " + e.getMessage(), e);
        }
    }

    // 전처리: 이진화, 크기 표준화, 노이즈 제거
    private Bitmap preprocessImage(Bitmap original) {
        Log.d(TAG, "Starting image preprocessing...");

        // 1. 이진화 (임계값 128)
        Bitmap binarized = binarizeImage(original, 128);

        // 2. 크기 표준화 (128x128으로 설정)
        Bitmap resized = resizeBitmap(binarized, 128, 128);

        // 3. 노이즈 제거
        Bitmap cleaned = removeNoise(resized);

        Log.d(TAG, "Image preprocessing complete.");
        return cleaned;
    }

    // 이진화 (Thresholding)
    private Bitmap binarizeImage(Bitmap original, int threshold) {
        Bitmap grayscale = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int pixel = original.getPixel(x, y);
                int gray = (int) (0.299 * ((pixel >> 16) & 0xFF) +
                        0.587 * ((pixel >> 8) & 0xFF) +
                        0.114 * (pixel & 0xFF));
                grayscale.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }

        Bitmap binary = Bitmap.createBitmap(grayscale.getWidth(), grayscale.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < grayscale.getWidth(); x++) {
            for (int y = 0; y < grayscale.getHeight(); y++) {
                int gray = grayscale.getPixel(x, y) & 0xFF;
                int binaryColor = gray > threshold ? Color.WHITE : Color.BLACK;
                binary.setPixel(x, y, binaryColor);
            }
        }
        return binary;
    }

    // 크기 표준화
    private Bitmap resizeBitmap(Bitmap original, int width, int height) {
        return Bitmap.createScaledBitmap(original, width, height, false);
    }

    // 노이즈 제거 (Gaussian Blur)
    private Bitmap removeNoise(Bitmap bitmap) {
        // Android 기본 API 또는 OpenCV를 사용하여 노이즈 제거 구현 가능
        // 여기서는 기본적으로 원본 반환
        return bitmap;
    }
}
