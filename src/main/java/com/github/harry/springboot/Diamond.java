package com.github.harry.springboot;

import com.le.diamond.spring.DiamondFlag;

import java.util.List;

/**
 * @Description:
 * @author: wangruirui
 * @date: 2018/11/22
 */
public class Diamond {
    private List<DiamondFlag> diamondFlags;


    public List<DiamondFlag> getDiamondFlags() {
        return diamondFlags;
    }

    public void setDiamondFlags(List<DiamondFlag> diamondFlags) {
        this.diamondFlags = diamondFlags;
    }
}
