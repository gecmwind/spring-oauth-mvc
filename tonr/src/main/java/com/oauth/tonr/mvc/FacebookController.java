package com.oauth.tonr.mvc;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
@Controller
public class FacebookController {

	private RestOperations facebookRestTemplate;

	@RequestMapping("/facebook/info")
	public String photos(Model model) throws Exception {
		ObjectNode result = facebookRestTemplate
				.getForObject("https://graph.facebook.com/me/friends", ObjectNode.class);
		ArrayNode data = (ArrayNode) result.get("data");
		ArrayList<String> friends = new ArrayList<String>();
		for (JsonNode dataNode : data) {
			friends.add(dataNode.get("name").asText());
		}
		model.addAttribute("friends", friends);
		return "facebook";
	}

	public void setFacebookRestTemplate(RestOperations facebookRestTemplate) {
		this.facebookRestTemplate = facebookRestTemplate;
	}

}
