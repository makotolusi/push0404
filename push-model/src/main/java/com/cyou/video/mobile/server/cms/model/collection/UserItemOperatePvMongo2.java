package com.cyou.video.mobile.server.cms.model.collection;


public class UserItemOperatePvMongo2  {

	private ClientLogCollection id; // mongo key;

	private Value value; // mongo value

	
	public ClientLogCollection getId() {
		return id;
	}

	public void setId(ClientLogCollection id) {
		this.id = id;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "gamecode =" + value.getGameCode();
	}


}
