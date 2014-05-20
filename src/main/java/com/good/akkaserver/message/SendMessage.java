package com.good.akkaserver.message;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class SendMessage {

	public static byte MESSAGE_TYPE_REPLY = 1;
	public static byte MESSAGE_TYPE_PUSH = 2;

	public static int MESSAGE_HEAD_LEN = 4;// 头长度

	public static int MESSAGE_MAX_LEN = 65000;// 最大长度

	private int msgLen;// 长度

	private int command;// 指令码

	private long playerid = 0;

	private byte[] conData = null;// 信息内容

	private int conDataLen;// 信息内容数组长度

	private int inc = 0;// 游标

	private ChannelHandlerContext channelHandlerContent;// 事件关联的channelContext


	public SendMessage() {
		conData = new byte[MESSAGE_MAX_LEN];
	}

	public SendMessage(int maxlen) {
		conData = new byte[maxlen];
	}

	public SendMessage(byte[] msg) {
		decode(msg);
	}

	// 用于操作信息数组的方法

	public void reset() {
		inc = 0;
	}

	public void putBoolean(boolean vol) {
		conData[inc++] = (byte) (vol ? 1 : 0);// true == 1;false == 0;
	}

	public boolean getBoolean() {
		return conData[inc++] == 1;// true == 1;false == 0;
	}

	public void putByte(byte vol) {
		conData[inc++] = vol;
	}

	public byte getByte() {
		return conData[inc++];
	}

	public void putBytes(byte[] vol) {
		putLength(vol.length);
		for (int i = 0; i < vol.length; i++)
			putByte(vol[i]);
	}

	public byte[] getBytes() {
		byte[] temp = new byte[getLength()];

		for (int i = 0; i < temp.length; i++)
			temp[i] = getByte();

		return temp;
	}

	public void putShort(short vol) {
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	public short getShort() {
		return (short) (((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF));
	}

	public void putInt(int vol) {
		conData[inc++] = (byte) ((vol >>> 24) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 16) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	public int getInt() {
		return ((conData[inc++] & 0xFF) << 24)
				+ ((conData[inc++] & 0xFF) << 16)
				+ ((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF);
	}

	/**
	 * 
	 * Function name:putLength Description: 用3个字节代表长度
	 * 
	 * @param vol
	 *            长度
	 */
	public void putLength(int vol) {
		conData[inc++] = (byte) ((vol >>> 16) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	/**
	 * 
	 * Function name:getLength Description: 用3个字节代表长度
	 * 
	 * @return 长度
	 */
	public int getLength() {
		return ((conData[inc++] & 0xFF) << 16) + ((conData[inc++] & 0xFF) << 8)
				+ (conData[inc++] & 0xFF);
	}

	public void putLong(long vol) {
		conData[inc++] = (byte) ((vol >>> 56) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 48) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 40) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 32) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 24) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 16) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	public long getLong() {
		return ((long) (conData[inc++] & 0xFF) << 56)
				+ ((long) (conData[inc++] & 0xFF) << 48)
				+ ((long) (conData[inc++] & 0xFF) << 40)
				+ ((long) (conData[inc++] & 0xFF) << 32)
				+ ((long) (conData[inc++] & 0xFF) << 24)
				+ ((long) (conData[inc++] & 0xFF) << 16)
				+ ((long) (conData[inc++] & 0xFF) << 8)
				+ ((long) conData[inc++] & 0xFF);
	}

	public void putChar(char vol) {
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	public char getChar() {
		return (char) (((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF));
	}

	public void putString(String vol) {
		if (vol == null)
			vol = "";
		putLength(vol.length());
		for (int i = 0; i < vol.length(); i++) {
			putChar(vol.charAt(i));
		}
	}

	public String getString() {
		int size = getLength();
		StringBuffer buffer = new StringBuffer(size);
		for (int i = 0; i < size; i++) {
			buffer.append(getChar());
		}
		return buffer.toString();
	}

	public byte[] encode() {
		byte[] temp = null;
		if (inc > 0) {
			temp = new byte[inc + MESSAGE_HEAD_LEN];
			int msglen = temp.length;
			temp[0] = (byte) ((msglen >>> 8) & 0xFF);
			temp[1] = (byte) (msglen & 0xFF);
			temp[2] = (byte) ((command >>> 8) & 0xFF);
			temp[3] = (byte) (command & 0xFF);
			System.arraycopy(conData, 0, temp, MESSAGE_HEAD_LEN, inc);
			
		} else {

			temp = new byte[MESSAGE_HEAD_LEN];
			int msglen = temp.length;
			temp[0] = (byte) ((msglen >>> 8) & 0xFF);
			temp[1] = (byte) (msglen & 0xFF);
			temp[2] = (byte) ((command >>> 8) & 0xFF);
			temp[3] = (byte) (command & 0xFF);
		}
		
		return temp;
	}

	public boolean decode(byte[] bs) {
		// 判断头的合法性
		if (bs.length < MESSAGE_HEAD_LEN) {
			return false;
		}

		// 获取头
		inc = 0;
		msgLen = ((bs[inc++] & 0xFF) << 8) + (bs[inc++] & 0xFF);
		command = ((bs[inc++] & 0xFF) << 8) + (bs[inc++] & 0xFF);

		// 判断头的合法性
		if (bs.length != msgLen) {
			return false;
		}

		// 获取信息体
		System.arraycopy(bs, MESSAGE_HEAD_LEN, conData, 0, msgLen
				- MESSAGE_HEAD_LEN);
		inc = 0;
		return true;
	}

	public static boolean isValidMessage(byte[] bs) {
		// 判断头的合法性
		if (bs.length < MESSAGE_HEAD_LEN) {
			return false;
		}
		// 获取头
		int inc = 0;
		int msglen = ((bs[inc++] & 0xFF) << 8) + (bs[inc++] & 0xFF);

		// 判断头的合法性
		if (bs.length != msglen) {
			return false;
		}
		return true;
	}

	public boolean socketSend() {
		try {
			Channel channel = channelHandlerContent.getChannel();
			byte[] msgbyte = encode();
			ChannelBuffer cb = ChannelBuffers.buffer(msgbyte.length);
			cb.writeBytes(msgbyte);
			channel.write(cb);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public boolean socketSend(ChannelHandlerContext ctx) {
		try {
			Channel channel = ctx.getChannel();
			byte[] msgbyte = encode();
			ChannelBuffer cb = ChannelBuffers.buffer(msgbyte.length);
			cb.writeBytes(msgbyte);
			channel.write(cb);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}


	public void clean() {
		inc = 0;
	}

	public int getMsgLen() {
		return msgLen;
	}

	public void setMsgLen(int msgLen) {
		this.msgLen = msgLen;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public byte[] getConData() {
		return conData;
	}

	public void setConData(byte[] conData) {
		this.conData = conData;
	}

	public int getConDataLen() {
		return conDataLen;
	}

	public void setConDataLen(int conDataLen) {
		this.conDataLen = conDataLen;
	}

	public int getInc() {
		return inc;
	}

	public void setInc(int inc) {
		this.inc = inc;
	}

	public ChannelHandlerContext getChannelHandlerContent() {
		return channelHandlerContent;
	}

	public void setChannelHandlerContent(
			ChannelHandlerContext channelHandlerContent) {
		this.channelHandlerContent = channelHandlerContent;
	}


	public long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}



}
