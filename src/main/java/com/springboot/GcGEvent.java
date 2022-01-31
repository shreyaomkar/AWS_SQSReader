package com.springboot;

import java.util.Map;

import org.joda.time.DateTime;


public class GcGEvent implements Comparable<GcGEvent>{

	private String gcgMessageId;
	private String timestamp;
	private String counterPartyId;
	private String originSystem;
	private String currentSystem;
	private String destinationSystem;
	private String s3Link;
	private String contentType;
	private Map<String, String> messageAttributes;
	private String gcgPayLoad;
	
	



	/**
     * Represents an SNS message attribute
     *
     */
    public static class MessageAttribute {
        private String type;
        private String value;

        /**
         * Gets the attribute type
         * 
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the attribute value
         * 
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the attribute type
         * @param type A string representing the attribute type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Sets the attribute value
         * @param value A string containing the attribute value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
	
	public String getGcgMessageId() {
		return gcgMessageId;
	}



	public void setGcgMessageId(String gcgMessageId) {
		this.gcgMessageId = gcgMessageId;
	}



	public String getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}



	public String getCounterPartyId() {
		return counterPartyId;
	}



	public void setCounterPartyId(String counterPartyId) {
		this.counterPartyId = counterPartyId;
	}



	public String getContentType() {
		return contentType;
	}



	public void setContentType(String contentType) {
		this.contentType = contentType;
	}



	public String getS3Link() {
		return s3Link;
	}



	public void setS3Link(String s3Link) {
		this.s3Link = s3Link;
	}



	public String getOriginSystem() {
		return originSystem;
	}



	public void setOriginSystem(String originSystem) {
		this.originSystem = originSystem;
	}



	public String getCurrentSystem() {
		return currentSystem;
	}



	public void setCurrentSystem(String currentSystem) {
		this.currentSystem = currentSystem;
	}



	public String getDestinationSystem() {
		return destinationSystem;
	}



	public void setDestinationSystem(String destinationSystem) {
		this.destinationSystem = destinationSystem;
	}



	public String getGcgPayLoad() {
		return gcgPayLoad;
	}



	public void setGcgPayLoad(String gcgPayLoad) {
		this.gcgPayLoad = gcgPayLoad;
	}



	public Map<String, String> getMessageAttributes() {
		return messageAttributes;
	}



	public void setMessageAttributes(Map<String, String> messageAttributes) {
		this.messageAttributes = messageAttributes;
	}



	@Override
	public int compareTo(GcGEvent o) {
		if (Long.valueOf(this.timestamp) < Long.valueOf(o.getTimestamp())) {
            return -1;
        }
        if (Long.valueOf(this.timestamp) == Long.valueOf(o.getTimestamp())) {
            return 0;
        }
        return 1;
	}



	



	


	
}
