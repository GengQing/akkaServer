package com.good.akkaserver.event;


/**
 * 客户端发往服务器端的请求
 */
public class ClientEvent {
	
	
	/** 头长度  */
	public static int MESSAGE_HEAD_LEN = 4;		
	
	/** 最大字节数 */
	public final int MAX_SIZE = 1024;	
	
	private int sessionID;
	
	/** 事件发起者的账号id*/
	private long playerid = 0;	
	
	/** 游戏角色ID */
	private int roleid = 0;	
	
	/** 时间片值 */
	private long timestamp = 0;	
	
	/** 指令码*/
	private int command = 0;
   
	/** 信息内容  */
	private byte[] conData = null;

	/** 信息内容数组长度  */
	private int conDataLen;
	
	/** 游标 */
	private int inc = 0; 
	
	
	public ClientEvent(){
		conData = new byte[MAX_SIZE];
	}
	
	public ClientEvent(int roleid,int eventtype,int command,byte[] conData){
		   this.roleid = roleid;
		   this.command = command;
		   this.conData = conData;
	}
	
	public ClientEvent(int connectType,int actorid,int eventtype,int command,byte[] conData){
		   this.roleid = actorid;
		   this.command = command;
		   this.conData = conData;
	}
	
	
	/** 将读取的游标移动到数组头 ：inc = 0*/
	public void reset() {
		inc = 0;
	}
	
	public void putBoolean(boolean vol) {
		conData[inc++] = (byte) (vol ? 1 : 0);// true == 1;false == 0;
		conDataLen=inc;
	}

	public boolean getBoolean() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return conData[inc++] == 1;// true == 1;false == 0;
	}

	public void putByte(byte vol) {
		conData[inc++] = vol;
		conDataLen=inc;
	}

	public byte getByte() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return conData[inc++];
	}

	public void putBytes(byte[] vol) {
		putLength(vol.length);
		for (int i = 0; i < vol.length; i++){
			putByte(vol[i]);
		}
		conDataLen=inc;
	}

	public byte[] getBytes() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		
		byte[] temp = new byte[getLength()];

		for (int i = 0; i < temp.length; i++)
			temp[i] = getByte();

		return temp;
	}

	public void putShort(short vol) {
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
		conDataLen=inc;
	}

	public short getShort() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return (short) (((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF));
	}

	public void putInt(int vol) {
		conData[inc++] = (byte) ((vol >>> 24) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 16) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
		conDataLen=inc;
	}

	public int getInt() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return ((conData[inc++] & 0xFF) << 24)
				+ ((conData[inc++] & 0xFF) << 16)
				+ ((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF);
	}
	
	/**
	 * 
	 * Function name:putLength
	 * Description: 用3个字节代表长度
	 * @param vol 长度
	 */
	private void putLength(int vol) {
		conData[inc++] = (byte) ((vol >>> 16) & 0xFF);
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
	}

	/**
	 * 
	 * Function name:getLength
	 * Description: 用3个字节代表长度
	 * @return 长度
	 */
	private int getLength() {
		return ((conData[inc++] & 0xFF) << 16)
				+ ((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF);
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
		conDataLen=inc;
	}

	public long getLong() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return ((long)(conData[inc++] & 0xFF) << 56)
				+ ((long)(conData[inc++] & 0xFF) << 48)
				+ ((long)(conData[inc++] & 0xFF) << 40)
				+ ((long)(conData[inc++] & 0xFF) << 32)
				+ ((long)(conData[inc++] & 0xFF) << 24)
				+ ((long)(conData[inc++] & 0xFF) << 16)
				+ ((long)(conData[inc++] & 0xFF) << 8) + ((long)conData[inc++] & 0xFF);
	}

	public void putChar(char vol) {
		conData[inc++] = (byte) ((vol >>> 8) & 0xFF);
		conData[inc++] = (byte) (vol & 0xFF);
		conDataLen=inc;
	}
	
	public char getChar() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		return (char) (((conData[inc++] & 0xFF) << 8) + (conData[inc++] & 0xFF));
	}

	public void putString(String vol) {
		if(vol == null)
			vol = "null";
		putLength(vol.length());
		for (int i = 0; i < vol.length(); i++) {
			putChar(vol.charAt(i));
		}
		conDataLen=inc;
	}
	
	public String getString() {
		if(inc>=conDataLen){
			throw new RuntimeException("ServerEvent 获得数据的时候越界了");
		}
		int size = getLength();
		StringBuffer buffer = new StringBuffer(size);
		for (int i = 0; i < size; i++) {
			buffer.append(getChar());
		}
		return buffer.toString();
	}
	
	
	public byte[] encode() {
		byte[] temp = null;
		if(inc > 0){
			temp = new byte[inc + MESSAGE_HEAD_LEN];
			int msglen = temp.length;
			temp[0] = (byte) ((msglen >>> 8) & 0xFF);
			temp[1] = (byte) (msglen & 0xFF);
			temp[2] = (byte) ((command >>> 8) & 0xFF);
			temp[3] = (byte) (command & 0xFF);
			System.arraycopy(conData, 0, temp, MESSAGE_HEAD_LEN, inc);
			return temp;
		}else{
			temp = new byte[inc + MESSAGE_HEAD_LEN];
			int msglen = temp.length;
			temp[0] = (byte) ((msglen >>> 8) & 0xFF);
			temp[1] = (byte) (msglen & 0xFF);
			temp[2] = (byte) ((command >>> 8) & 0xFF);
			temp[3] = (byte) (command & 0xFF);
		}
		return temp;
	}
	
	public boolean decode(byte[] bs){
		//判断头的合法性
		if(bs == null || bs.length<MESSAGE_HEAD_LEN){
			return false;
		}
		
		//获取头
		inc = 0;
		int msglen = ((bs[inc++] & 0xFF) << 8) + (bs[inc++] & 0xFF);
		command = ((bs[inc++] & 0xFF) << 8) + (bs[inc++] & 0xFF);
		
		//判断头的合法性
		if(bs.length!=msglen){
			return false;
		}
		
		//获取信息体
		System.arraycopy(bs,MESSAGE_HEAD_LEN,conData,0,msglen-MESSAGE_HEAD_LEN);
		inc=0;
		//记录长度
		conDataLen = bs.length-MESSAGE_HEAD_LEN;
		return true;
	}
	
	
	//属性get和set方法

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
		if(conData!=null){
			return conData.length;
		}else{
			return 0;
		}
	}

	public void setConDataLen(int conDataLen) {
		this.conDataLen = conDataLen;
	}

	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}


	public long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}


}