package com.cj.android.touchpull2;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 方向（是下拉刷新还是上拉加载）
 * Created by jian.cao on 2016/1/29.
 */
public final class Direction {
    public static final int NONE = 0;
    public static final int UP_PULL = 1;//上拉加载
    public static final int DOWN_PULL = 2;//下拉刷新
    public static final int BOTH_PULL = 3;//两者

    /**
     * android不推荐枚举enum,但是我们可以这样使用。
     *
     * @hide
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, UP_PULL, DOWN_PULL, BOTH_PULL})
    public @interface DirectionType {
    }

    @DirectionType
    int type;

    public Direction() {
    }

    public Direction(@DirectionType int type) {
        this.type = type;
    }

    /**
     * 配置方向（注：this为NONE时取该方向）
     *
     * @param type
     * @return true:this和该方向有交集或者this方向为NONE
     */
    public boolean configDirection(int type) {
        if (containsDirection(type) || this.type == Direction.NONE) {
            this.type = type;
            return true;
        } else if (type == Direction.BOTH_PULL) {
            return true;
        } else {
            this.type = Direction.NONE;
            return false;
        }
    }

    /**
     * 判断this包含该方向
     *
     * @param type
     * @return
     */
    public boolean containsDirection(int type) {
        if (this.type == Direction.BOTH_PULL) {
            return true;
        } else if (this.type == type) {
            return true;
        }
        return false;
    }

    /**
     * 添加方向
     *
     * @param type
     */
    public void addDirection(int type) {
        if (type == Direction.NONE ||
                this.type == Direction.BOTH_PULL) {//已经为BOTH_PULL了，不需要再添加
            return;
        }
        if (type == Direction.BOTH_PULL) {
            this.type = Direction.BOTH_PULL;
            return;
        }
        if (type == Direction.DOWN_PULL) {
            if (this.type == Direction.UP_PULL) {
                this.type = Direction.BOTH_PULL;
                return;
            }
            this.type = Direction.DOWN_PULL;
            return;
        }
        if (type == Direction.UP_PULL) {
            if (this.type == Direction.DOWN_PULL) {
                this.type = Direction.BOTH_PULL;
                return;
            }
            this.type = Direction.UP_PULL;
            return;
        }
    }

    /**
     * 取交集
     *
     * @param direction
     */
    public void takeIntersection(Direction direction) {
        if (direction.type == BOTH_PULL || direction.type == this.type) {
            return;
        }
        if (this.type == BOTH_PULL) {
            this.type = direction.type;
            return;
        }
        this.type = NONE;
    }
}
