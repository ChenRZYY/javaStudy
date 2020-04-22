package com.sdrfengmi.study._009_enum;

/**
 * @author 陈振东
 * enum 就是提前固定的一些静态类,
 */
public enum InstantiationEnum {

    INIT {
        @Override
        public int getAbstractType() {
            return 0;
        }

        @Override
        public String getAbstractDesc() {
            return null;
        }
    },

    DELETE {
        private int type;
        private String desc;

        @Override
        public int getAbstractType() {
            return 0;
        }

        @Override
        public String getAbstractDesc() {
            return null;
        }
    };

    private String name;
    private int index;

    //默认值
    public String getDesc() {
        return "初始状态";
    }

    //默认值
    public int getType() {
        return -1;
    }

    public abstract int getAbstractType();

    public abstract String getAbstractDesc();
}