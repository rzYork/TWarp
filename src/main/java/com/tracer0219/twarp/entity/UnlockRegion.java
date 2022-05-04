package com.tracer0219.twarp.entity;

public class UnlockRegion{
    int x,y,z;

    public UnlockRegion(int x, int y, int z) {
        this.x = x<0?0:x;
        this.y = y<0?0:y;
        this.z = z<0?0:z;
    }

    @Override
    public String toString() {
        return "UnlockRegion{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    protected UnlockRegion clone(){
        return new UnlockRegion(this.x,this.y,this.z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}