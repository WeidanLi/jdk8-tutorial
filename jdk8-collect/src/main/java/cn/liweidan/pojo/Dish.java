package cn.liweidan.pojo;

import lombok.*;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午2:51
 * Author : Weidan
 * Version : V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Dish {

    /** 名字 */
    private String name;
    /** 是否素食 */
    private boolean vegetarain;
    /** 卡路里 */
    private int colories;
    /** 类型 */
    private Type type;

    public enum Type {MEAT, FISH, OTHER};

}
