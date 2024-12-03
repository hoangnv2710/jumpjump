package com.example.test;

import android.os.SystemClock;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.Random;

public class Monster {
    private Bitmap[] dragonImages; // Array chứa dragon1, dragon2, dragon3
    private int x, y;              // Vị trí của monster
    private float velocityY;       // Vận tốc thay đổi theo trục Y
    private int screenHeight;      // Chiều cao màn hình
    private int screenWidth;       // Chiều rộng màn hình
    private int state;             // Trạng thái của monster (0: dragon1, 1: dragon2, 2: dragon3)
    private int monsterWidth;      // Chiều rộng của monster
    private int monsterHeight;     // Chiều cao của monster
    private long lastStateChangeTime; // Thời gian của lần thay đổi trạng thái cuối cùng
    private static final long STATE_DURATION = 200; // Thời gian tồn tại của mỗi trạng thái (0.1s)
    private int[] stateSequence = {0, 1, 2, 1, 0}; // Thứ tự các trạng thái
    private int sequenceIndex = 0; // Chỉ số của trạng thái hiện tại trong stateSequence

    // Constructor để khởi tạo quái vật
    public Monster(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Load hình ảnh từ R.drawable
        Bitmap dragon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon1);
        Bitmap dragon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon2);
        Bitmap dragon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.dragon3);

        // Resize hình ảnh để phù hợp với màn hình
        this.monsterWidth = screenWidth / 8;
        this.monsterHeight = screenHeight / 12;
        dragon1 = Bitmap.createScaledBitmap(dragon1, monsterWidth, monsterHeight, false);
        dragon2 = Bitmap.createScaledBitmap(dragon2, monsterWidth, monsterHeight, false);
        dragon3 = Bitmap.createScaledBitmap(dragon3, monsterWidth, monsterHeight, false);

        // Lưu các hình ảnh vào array
        dragonImages = new Bitmap[]{dragon1, dragon2, dragon3};

        // Khởi tạo vị trí và trạng thái
        this.x = 0;  // Sẽ được thay đổi bởi createMonster
        this.y = -monsterHeight; // Bắt đầu từ ngoài màn hình trên
        this.velocityY = 0; // Vận tốc ban đầu là 0
        this.state = 0; // Mặc định là dragon1
        this.lastStateChangeTime = SystemClock.elapsedRealtime(); // Khởi tạo thời gian thay đổi trạng thái
    }

    // Hàm tạo ngẫu nhiên giá trị X cho Monster
    public void createMonster() {
        Random random = new Random();
        // Tạo giá trị X ngẫu nhiên từ 0 đến screenWidth - monsterWidth
        this.x = random.nextInt(screenWidth - monsterWidth);  // X là một giá trị ngẫu nhiên trong phạm vi chiều rộng màn hình
    }

    // Hàm thay đổi trạng thái của Monster (0: dragon1, 1: dragon2, 2: dragon3)
    public void setState(int state) {
        if (state >= 0 && state < dragonImages.length) {
            this.state = state;
        }
    }

    // Hàm lấy trạng thái hiện tại của Monster
    public int getState() {
        return state;
    }

    // Cập nhật vị trí của monster và trả về true nếu monster ra khỏi màn hình
    public boolean updateM() {
        // Cập nhật vận tốc theo một số giá trị
        velocityY += 0.1f; // Gia tốc rơi (tăng dần mỗi lần cập nhật)

        // Di chuyển monster theo vận tốc Y
        y += velocityY;

        // Nếu monster ra khỏi màn hình, trả về true
        if (y > screenHeight) {
            return true;
        }
        return false;
    }

    // Vẽ monster lên màn hình
    public void draw(Canvas canvas) {
        // Thay đổi trạng thái nếu đã đủ thời gian
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastStateChangeTime > STATE_DURATION) {
            sequenceIndex = (sequenceIndex + 1) % stateSequence.length;
            state = stateSequence[sequenceIndex];
            lastStateChangeTime = currentTime;
        }

        // Vẽ hình monster tương ứng với trạng thái hiện tại
        canvas.drawBitmap(dragonImages[state], x, y, null);
    }

    // Hàm lấy Bound để kiểm tra va chạm
    public Rect getBounds() {
        return new Rect(x, y, x + dragonImages[state].getWidth(), y + dragonImages[state].getHeight());
    }
}
