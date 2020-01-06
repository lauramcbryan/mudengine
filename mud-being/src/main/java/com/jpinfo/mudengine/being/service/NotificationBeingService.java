package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

@Service
public class NotificationBeingService {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationBeingService.class);

	@Autowired
	private MessageServiceClient messageService;
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Value("${being.topic:disabled}")
	private String beingTopicName;
	
	private ActiveMQTopic beingTopic;
	
	@PostConstruct
	public void setup() {
		beingTopic = new ActiveMQTopic(beingTopicName);
	}
	
	public void handleDestroyedBeing(MudBeing destroyedBeing) {
		
		// Notify other modules about this
		sendDestroyedBeingNotifications(destroyedBeing);
		
		// Notify other beings about this
		sendDestroyedBeingMessages(destroyedBeing);
		
	}
	
	private void sendDestroyedBeingMessages(MudBeing destroyedBeing) {

		// Collect ALL playable beings in the place		
		repository.findPlayableInThisPlace(
				destroyedBeing.getCurWorld(), 
				destroyedBeing.getCurPlaceCode())
		.stream()
		.forEach(curBeing -> {

			// Prepare the message
			MessageRequest beingDestroyedMessage = new MessageRequest();
			beingDestroyedMessage.addChangedEntity(EnumEntityType.BEING, destroyedBeing.getCode());
			beingDestroyedMessage.setArgs( new String[] {
					destroyedBeing.getName()!=null ? destroyedBeing.getName() : destroyedBeing.getBeingClass().getName()
			});
			
			// Change the message if the current being is the one being destroyed
			// (sending message to its player)
			if (curBeing.getCode().equals(destroyedBeing.getCode())) {
				beingDestroyedMessage.setMessageKey(BeingHelper.BEING_DESTROY_YOURS_MSG);
			} else {
				beingDestroyedMessage.setMessageKey(BeingHelper.BEING_DESTROY_ANOTHER_MSG);
			}
					
			// Send the message
			messageService.putMessage(curBeing.getCode(), beingDestroyedMessage);
					
			log.info("world: {}, entityId: {}, message: {} ",
					destroyedBeing.getCurWorld(),
					curBeing.getCode(),
					beingDestroyedMessage.getMessageKey()
					);
					
		});
	}
	
	public List<MessageRequest> handleBeingChange(MudBeing beforeBeing, MudBeing afterBeing) {
		
		List<MessageRequest> msgList = new ArrayList<>();
		
		// Check attr modifiers changes
		checkChangedAttrModifiers(beforeBeing, afterBeing, msgList);
		checkAddedAttrModifiers(beforeBeing, afterBeing, msgList);

		// Check skill modifiers changes
		checkChangedSkillModifiers(beforeBeing, afterBeing, msgList);
		checkAddedSkillModifiers(beforeBeing, afterBeing, msgList);
		
		return msgList;
	}
	
	
	private void checkChangedAttrModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<MessageRequest> msgList) {
		
		// Check attr modifiers that was changed/removed
		beforeBeing.getAttrModifiers().stream()
			.forEach(beforeModifier -> {
				
				Optional<MudBeingAttrModifier> optAfterModifier=
					afterBeing.getAttrModifiers().stream()
						.filter(afterModifier -> afterModifier.equals(beforeModifier))
						.findFirst();

				String modifierCode = beforeModifier.getId().getCode();
				Float modifierValue = null;
				
				if (optAfterModifier.isPresent()) {
					
					// if the modifier is present, calculate value changed
					modifierValue = optAfterModifier.get().getOffset() - beforeModifier.getOffset();
					
				} else {
					
					// The modifier is being removed
					// That can be a good thing (beforeModifier was a penalty) or bad thing (losing a bonus)

					// If the previous modifier was a bonus
					if (beforeModifier.getOffset()>0.0f) {

						// The change is bad
						modifierValue = -beforeModifier.getOffset();

					} else {
						
						// The change is good
						modifierValue = Math.abs(beforeModifier.getOffset());
					}
				}
				
				// If we have a value changed...
				if ((modifierValue!=null) && (modifierValue!=0.0f)) {

					// Preparing the message request
					MessageRequest mRequest = new MessageRequest();
					mRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
					
					// set the appropriate message
					mRequest.setMessageKey(modifierValue > 0 ?  		
										BeingHelper.BEING_ATTRMOD_INCREASE_MSG:
										BeingHelper.BEING_ATTRMOD_DECREASE_MSG
										);

					// Put the values in the arguments
					mRequest.setArgs(new String[] {
							modifierCode,
							String.valueOf(Math.abs(modifierValue))
					});

					// Add in the message list
					msgList.add(mRequest);
				}
			});
	}
	
	private void checkAddedAttrModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<MessageRequest> msgList) {

		// Check attr modifiers that was added
		afterBeing.getAttrModifiers().stream()
			.filter(afterModifier -> !beforeBeing.getAttrModifiers().contains(afterModifier))
			.forEach(afterModifier -> {

				// Preparing the message request
				MessageRequest mRequest = new MessageRequest();
				mRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
				
				
				// We are adding a modifier here
				// It can be a bonus (positive modifier) or a penalty (negative modifier)
				
				// attr modifier added
				if (afterModifier.getOffset()>0.0f) {
					
					// The logic is straightforward here
					
					mRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
					mRequest.setArgs(new String[] {
							afterModifier.getId().getCode(),
							String.valueOf(afterModifier.getOffset())
					});
					
					msgList.add(mRequest);
					
				} else {
					
					mRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
					mRequest.setArgs(new String[] {
							afterModifier.getId().getCode(),
							String.valueOf(Math.abs(afterModifier.getOffset()))
					});
					
					msgList.add(mRequest);
				}
			});
	}
	
	private void checkChangedSkillModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<MessageRequest> msgList) {
		
		// Check skill modifiers that was changed/removed
		beforeBeing.getSkillModifiers().stream()
			.forEach(beforeModifier -> {
			
				Optional<MudBeingSkillModifier> optAfterModifier=
					afterBeing.getSkillModifiers().stream()
						.filter(afterModifier -> afterModifier.equals(beforeModifier))
						.findFirst();

				String modifierCode = beforeModifier.getId().getCode();
				Float modifierValue = null;
				
				if (optAfterModifier.isPresent()) {
					
					// if the modifier is present, calculate value changed
					modifierValue = optAfterModifier.get().getOffset() - beforeModifier.getOffset();
					
				} else {
					
					// The modifier is being removed
					// That can be a good thing (beforeModifier was a penalty) or bad thing (losing a bonus)

					// If the previous modifier was a bonus
					if (beforeModifier.getOffset()>0.0f) {

						// The change is bad
						modifierValue = -beforeModifier.getOffset();

					} else {
						
						// The change is good
						modifierValue = Math.abs(beforeModifier.getOffset());
					}
				}
				
				// If we have a value changed...
				if ((modifierValue!=null) && (modifierValue!=0.0f)) {

					// Preparing the message request
					MessageRequest mRequest = new MessageRequest();
					mRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
					
					// set the appropriate message
					mRequest.setMessageKey(modifierValue > 0 ?  		
										BeingHelper.BEING_SKILLMOD_INCREASE_MSG:
										BeingHelper.BEING_SKILLMOD_DECREASE_MSG
										);

					// Put the values in the arguments
					mRequest.setArgs(new String[] {
							modifierCode,
							String.valueOf(Math.abs(modifierValue))
					});

					// Add in the message list
					msgList.add(mRequest);
				}
				
				
				
			});
	}
	
	private void checkAddedSkillModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<MessageRequest> msgList) {

		// Check attr modifiers that was added
		afterBeing.getSkillModifiers().stream()
			.filter(afterModifier -> !beforeBeing.getSkillModifiers().contains(afterModifier))
			.forEach(afterModifier -> {

				// Preparing the message request
				MessageRequest mRequest = new MessageRequest();
				mRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
				
				
				// We are adding a modifier here
				// It can be a bonus (positive modifier) or a penalty (negative modifier)
				
				// attr modifier added
				if (afterModifier.getOffset()>0.0f) {
					
					// The logic is straightforward here
					
					mRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
					mRequest.setArgs(new String[] {
							afterModifier.getId().getCode(),
							String.valueOf(afterModifier.getOffset())
					});
					
					msgList.add(mRequest);
					
				} else {
					
					mRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
					mRequest.setArgs(new String[] {
							afterModifier.getId().getCode(),
							String.valueOf(Math.abs(afterModifier.getOffset()))
					});
					
					msgList.add(mRequest);
				}
			});
	}
	

	public void dispatchMessages(List<MessageRequest> msgList, MudBeing afterBeing) {
		
		// Pass through the notification list
		msgList.stream()
			.forEach(curMessage -> {
			
				// Send the message
				messageService.putMessage(afterBeing.getCode(), 
						curMessage);
				
				log.info("world: {}, entityId: {}, message: {}",
						afterBeing.getCurWorld(),
						afterBeing.getCode(),
						curMessage.getMessageKey()
						);
			});

	}
	
	private void sendDestroyedBeingNotifications(MudBeing destroyedBeing) {

		// Prepare a notification for this change
		NotificationMessage beingNotification = NotificationMessage.builder()
				// Who?
				.entity(NotificationMessage.EnumEntity.BEING)
				.entityId(destroyedBeing.getCode())
				// What happened?
				.event(EnumNotificationEvent.BEING_DESTROY)
				// Spread the news!
				.messageKey(BeingHelper.BEING_DESTROY_ANOTHER_MSG)
				.args(new String[] {
						destroyedBeing.getName()!=null ? destroyedBeing.getName() : destroyedBeing.getBeingClass().getName()
						})
				// For this guys (the place and the items owned by this being will take interest on that)
				.targetEntity(NotificationMessage.EnumEntity.PLACE)
				.targetEntityId(destroyedBeing.getCurPlaceCode().longValue())
				.worldName(destroyedBeing.getCurWorld())
			.build();
		
		// Send Notification
		jmsTemplate.convertAndSend(beingTopic, beingNotification, m-> {
			
			m.setObjectProperty(CommonConstants.AUTH_TOKEN_HEADER, 
					SecurityContextHolder.getContext().getAuthentication().getCredentials()
					);
			
			return m;
		});
		
	}
	
}
