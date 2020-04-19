package com.cisc.zzt.msg;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ZztMsg {
	private int action;
	private byte forwardCount;
	private String HandleSerialNo;
	private boolean caseSensitive;
	private Map<String, Object> data;

	public ZztMsg() {
		this(true);
	}

	public ZztMsg(boolean caseSensitive) {
		this.action = 0;
		this.forwardCount = 0;
		this.HandleSerialNo = "";
		this.caseSensitive = true;
		this.data = Maps.newHashMap();
		this.setCaseSensitive(caseSensitive);
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	public void putString(String name, String value) {
		this.data.put(this.formatName(name), value);
	}

	public final void error() {
	}

	public String getString(String name) {
		Object obj = this.data.getOrDefault(this.formatName(name), (Object) null);
		return Objects.toString(obj, "");
	}

	public void putInt(String name, int value) {
		this.data.put(this.formatName(name), value);
	}

	public int getInt(String name) {
		Object obj = this.data.getOrDefault(this.formatName(name), (Object) null);
		return obj instanceof Integer ? (Integer) obj : Ints.tryParse(Objects.toString(obj, ""));
	}

	public void putDouble(String name, double value) {
		this.data.put(this.formatName(name), value);
	}

	public Double getDouble(String name) {
		Object obj = this.data.getOrDefault(this.formatName(name), (Object) null);
		return obj instanceof Double ? (Double) obj : Doubles.tryParse(Objects.toString(obj, ""));
	}

	public void putLong(String name, long value) {
		this.data.put(this.formatName(name), value);
	}

	public long getLong(String name) {
		Object obj = this.data.getOrDefault(this.formatName(name), (Object) null);
		return obj instanceof Long ? (Long) obj : Longs.tryParse(Objects.toString(obj, ""));
	}

	public void putBigDecimal(String name, BigDecimal value) {
		this.data.put(this.formatName(name), value);
	}

	public BigDecimal getBigDecimal(String name) {
		Object obj = this.data.getOrDefault(this.formatName(name), (Object) null);
		return obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(Objects.toString(obj));
	}

	public void putItems(Map<String, Object> items) {
		items.forEach((key, val) -> {
			this.data.put(this.formatName(key), val);
		});
	}

	public void forEach(BiConsumer<String, ? super Object> action) {
		this.data.forEach((key, val) -> {
			action.accept(this.formatName(key), val);
		});
	}

	private String formatName(String name) {
		return this.caseSensitive ? Strings.nullToEmpty(name) : Strings.nullToEmpty(name).toUpperCase();
	}

	public int getAction() {
		return this.action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public byte getForwardCount() {
		return this.forwardCount;
	}

	public void setForwardCount(byte forwardCount) {
		this.forwardCount = forwardCount;
	}

	public String getHandleSerialNo() {
		return this.HandleSerialNo;
	}

	public void setHandleSerialNo(String HandleSerialNo) {
		this.HandleSerialNo = HandleSerialNo;
	}

	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
}