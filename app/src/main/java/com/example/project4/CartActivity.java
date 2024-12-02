package com.example.project4;

import static java.lang.Math.abs;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity {
    private List<Product> products = new ArrayList<>();
    private int userTotalPrice = 0;
    private int targetPrice;
    private TextView resultText;
    private TextView targetPriceText;
    private TextView timerText;
    private GridLayout gridLayout;
    private Button startButton;
    private Button endButton;
    private Button resultButton;
    private CountDownTimer timer;
    private boolean gameActive = false;
    private boolean gameEnded = false;
    private String name="카트 담기 게임";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        resultText = findViewById(R.id.resultText);
        targetPriceText = findViewById(R.id.targetPriceText);
        timerText = findViewById(R.id.timerText);
        gridLayout = findViewById(R.id.gridLayout);
        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);
        resultButton = findViewById(R.id.resultButton);

        // 게임 시작 전 UI 설정
        endButton.setVisibility(View.GONE);
        resultButton.setVisibility(View.GONE);
        resultText.setVisibility(View.GONE);
        targetPriceText.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
        gridLayout.setVisibility(View.GONE);

        startButton.setOnClickListener(view -> startGame());
        endButton.setOnClickListener(view -> endGame());

        generateProducts();
    }

    private void generateProducts() {
        products.clear(); // 리스트 초기화
        for (int i = 0; i < 9; i++) {
            int price = 1000 + new Random().nextInt(9001); // 1000 ~ 10000
            products.add(new Product("Product " + (i + 1), price, getResources().getIdentifier("product" + (i + 1), "drawable", getPackageName())));
        }
    }


    private void startGame() {
        userTotalPrice = 0;
        resultText.setText("Total: 0원");
        targetPrice = 20000 + new Random().nextInt(80001); // 20000 ~ 100000
        targetPriceText.setText("Target Price: " + targetPrice + "원");

        // Start 버튼 숨기기
        startButton.setVisibility(View.GONE);

        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                endGame();
            }
        }.start();

        displayProducts();
        gameActive = true;

        // 다른 UI 요소 표시
        timerText.setVisibility(View.VISIBLE);
        targetPriceText.setVisibility(View.VISIBLE);
        resultText.setVisibility(View.VISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        resultButton.setVisibility(View.GONE); // 결과 버튼 숨기기
        endButton.setVisibility(View.VISIBLE); // 종료 버튼 보이기
    }

    private void displayProducts() {
        gridLayout.removeAllViews();
        for (Product product : products) {
            View productLayout = getLayoutInflater().inflate(R.layout.product_item, null);
            ImageView imageView = productLayout.findViewById(R.id.productImage);
            TextView priceText = productLayout.findViewById(R.id.productPrice);

            imageView.setImageResource(product.getImageResId());
            priceText.setText(product.getPrice() + "원");

            imageView.setOnClickListener(view -> addProductToCart(product));
            gridLayout.addView(productLayout);
        }
    }

    private void addProductToCart(Product product) {
        if (!gameActive) return;
        userTotalPrice += product.getPrice();
        resultText.setText("Total: " + userTotalPrice + "원");
    }

    private void endGame() {
        if (!gameActive) return;
        if (timer != null) {
            timer.cancel();
        }
        gameActive = false;

        // 컴퓨터의 가격 계산
        int computerPrice = targetPrice + (int) (targetPrice * (0.03 + new Random().nextInt(13) / 100.0));
        int score =abs(targetPrice-computerPrice)-abs(targetPrice-userTotalPrice);
        Intent intent = new Intent(CartActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name", name);  // 게임 이름 전달
        startActivity(intent); // GameOverActivity 시작
        finish(); // 현재 Activity 종료
    }





    public static class Product {
        private String name;
        private int price;
        private int imageResId;

        public Product(String name, int price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getImageResId() {
            return imageResId;
        }
    }
}