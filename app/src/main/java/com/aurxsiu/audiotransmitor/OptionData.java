package com.aurxsiu.audiotransmitor;

import java.util.Objects;

public class OptionData {
    public String content;
    public String remark;

    public OptionData(){}

    public OptionData(String content, String remark) {
        this.content = content;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return remark == null || remark.isEmpty() ? content : content + "（" + remark + "）";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionData that = (OptionData) o;
        return Objects.equals(content, that.content) && Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, remark);
    }
}
