package com.cloudpioneer.demo.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudpioneer.demo.base.annotation.Phone;
import com.cloudpioneer.demo.base.validator.Insert;
import com.cloudpioneer.demo.base.validator.Update;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * <p>
 * 
 * </p>
 *
 * @author jiangyunjun
 * @since 2018-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    @Null(groups = Insert.class)
    @NotEmpty(groups = Update.class)
    private String id;

    @NotEmpty(groups = Insert.class)
    private String name;

    private Integer age;

    @NotEmpty(groups = Insert.class)
    @Phone(groups = {Insert.class, Update.class})
    private String phone;

    @NotEmpty(groups = Insert.class)
    @Size(min = 18, max = 18, groups = {Insert.class, Update.class})
    private String idCard;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

//    @TableLogic
    private Boolean isDel;


}
