package com.niezhiliang.luck.draw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LuckRecode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @TableField("userId")
    private Integer userId;

    @TableField("prizeId")
    private Integer prizeId;

    @TableField("createAt")
    private Date createAt;


}
