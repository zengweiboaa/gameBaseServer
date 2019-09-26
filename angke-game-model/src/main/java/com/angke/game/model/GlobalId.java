package com.angke.game.model;

import java.io.Serializable;

/**
 * 全局唯一性的ID
 * 
 * @author LiangShengxian
 *
 */
public interface GlobalId extends Serializable, Comparable<GlobalId> {

	/**
     * 返回数据较短，但不保证全局唯一的字符串表示的{@link GlobalId}.
     */
    String asShortText();

    /**
     * 返回数据较长，但能够保证全球唯一的字符串表示形式的{@link GlobalId}.
     */
    String asLongText();
    
}
