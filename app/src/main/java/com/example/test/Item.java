package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.List;
import java.util.Random;

public class Item {
    private Bitmap[] itemImages;
    private int item1Width;
    private int item1Height;
    private int item2Width;
    private int item2Height;
    private int x;
    private int y;
    private float velocityY;
    private int screenHeight;
    private int screenWidth;
    private int itemId;
    private int typeDrop; // 1: sinh ra trên platform, 2: rơi từ trên màn hình xuống
    private Platform associatedPlatform;

    public Item(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        Bitmap item1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        Bitmap item2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.x2);
        this.item1Width = screenWidth / 16;
        this.item1Height = screenHeight / 24;
        this.item2Width = screenWidth / 16;
        this.item2Height = screenHeight / 24;
        item1 = Bitmap.createScaledBitmap(item1, item1Width, item1Height, false);
        item2 = Bitmap.createScaledBitmap(item2, item2Width, item2Height, false);
        itemImages = new Bitmap[]{item1, item2};
    }

    public void createItem_1(List<Platform> platforms, int platformWidth, int platformHeight) {
        if (platforms.isEmpty()) {
            return;
        }
        Platform lastPlatform = platforms.get(platforms.size() - 1);

        Random random = new Random();
        this.itemId = random.nextInt(2);
        this.typeDrop = 1;
        int platformX = lastPlatform.getX();
        int platformY = lastPlatform.getY();

        // Determine the position and type of the item (Heart or x2)
        if (itemId == 1) {
            this.x = platformX + random.nextInt(platformWidth - item1Width);
            this.y = platformY - item1Height;  // Position item above the platform
        } else {
            this.x = platformX + random.nextInt(platformWidth - item2Width);
            this.y = platformY - item2Height;  // Position item above the platform
        }

        // Mark the platform as having an item
        lastPlatform.setHasItem(true);

        // Associate the item with the platform
        this.setAssociatedPlatform(lastPlatform);
    }




    public void createItem_2(){
        this.typeDrop = 2;
        Random random = new Random();
        this.itemId = random.nextInt(2);
        if(itemId == 0){
            this.x = random.nextInt(screenWidth - item1Width);
        }
        else if(itemId == 1){
            this.x = random.nextInt(screenWidth - item2Width);
        }
        this.y = -itemImages[itemId].getHeight();

    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(itemImages[itemId], x, y, null);
    }

    public Rect getBounds() {
        return new Rect(x, y, x + itemImages[itemId].getWidth(), y + itemImages[itemId].getHeight());
    }

    public boolean updateI() {
        if (typeDrop == 2) {
            // If the item is falling from the top
            velocityY += 0.1f; // Apply gravity
            y += velocityY;    // Update the Y position

            if (y > screenHeight) {
                return true; // Item falls off the screen
            }
            return false; // Item is still visible
        }

        // If the item is spawned on a platform and moves with the platform
        if (typeDrop == 1 && associatedPlatform != null) {
            // Update the Y position of the item according to the platform's position
            this.y = associatedPlatform.getY() - itemImages[itemId].getHeight();
        }

        return false; // Items spawned on platforms do not go off the screen by themselves
    }



    public void setAssociatedPlatform(Platform platform) {
        this.associatedPlatform = platform;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTypeDrop() {
        return typeDrop;
    }

    public void setTypeDrop(int typeDrop) {
        this.typeDrop = typeDrop;
    }
}
