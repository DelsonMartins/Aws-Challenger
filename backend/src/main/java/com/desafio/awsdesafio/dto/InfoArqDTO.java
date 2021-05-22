package com.desafio.awsdesafio.dto;

public class InfoArqDTO {
	
	private String name;
	private String url;
	private String lastModif;
	private String storageClass;
	private long size;

	public InfoArqDTO() {
	}

	public InfoArqDTO(String name, String url, String lastModif, String storageClass, long size) {
		super();
		this.name = name;
		this.url = url;
		this.lastModif = lastModif;
		this.storageClass = storageClass;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLastModif() {
		return lastModif;
	}

	public void setLastModif(String lastModif) {
		this.lastModif = lastModif;
	}

	public String getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
