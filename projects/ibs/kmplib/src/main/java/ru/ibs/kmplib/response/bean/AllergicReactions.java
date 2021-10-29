package ru.ibs.kmplib.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author me
 */
public class AllergicReactions {

	@JsonProperty("Items")
	private List<Items> itemsList;
	@JsonProperty("Messages")
	private List<Messages> messagesList;

	public List<Items> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<Items> itemsList) {
		this.itemsList = itemsList;
	}

	public List<Messages> getMessagesList() {
		return messagesList;
	}

	public void setMessagesList(List<Messages> messagesList) {
		this.messagesList = messagesList;
	}
}
