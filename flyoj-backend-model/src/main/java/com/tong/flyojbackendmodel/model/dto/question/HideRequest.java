package com.tong.flyojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author tong
 * 
 */
@Data
public class HideRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}