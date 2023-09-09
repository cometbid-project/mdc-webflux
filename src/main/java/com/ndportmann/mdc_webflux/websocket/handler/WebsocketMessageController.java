/**
 * 
 */
package com.ndportmann.mdc_webflux.websocket.handler;

import java.time.Clock;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ndportmann.mdc_webflux.service.model.InputMessage;
import com.ndportmann.mdc_webflux.service.model.OutputMessage;

import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Controller
public class WebsocketMessageController {
	
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public OutputMessage message(InputMessage message) {
		log.info("Input Message " + message);
		return OutputMessage.builder().time(Instant.now(Clock.systemDefaultZone())).content(message.getContent())
				.build();
	}
}
