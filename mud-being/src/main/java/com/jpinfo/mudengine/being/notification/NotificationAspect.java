package com.jpinfo.mudengine.being.notification;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

import org.apache.commons.lang.SerializationUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.being.client.ItemServiceClient;
import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

@Aspect
@Component
public class NotificationAspect {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationAspect.class);

	@Autowired
	private RabbitTemplate rabbit;
	
	@Value("${being.exchange}")
	private String beingExchange;
	
	@Autowired
	private BeingRepository repository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MessageServiceClient messageService;
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.save(..)) && args(afterBeing)")
	public Object compareBeing(ProceedingJoinPoint pjp, MudBeing afterBeing) throws Throwable {
		
		// Object returned after save operation
		Object savedBeing;
		
		// Checking if the future state entity has a ID as this method
		// is also used to create entities
		// Additionally, we only throw notification for playable beings.
		if ((afterBeing.getCode()!=null) && (afterBeing.getPlayerId()!=null)) {
			
			List<MessageRequest> msgList = new ArrayList<>();

			// This operation is important as the entity at this time will be in managed state,
			// all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudBeing from persistenceContext in order to force it to retrieve another.
			em.detach(afterBeing);
	
			// Getting the 'before' entity
			Optional<MudBeing> optBeforeBeing= repository.findById(afterBeing.getCode());
			
			if (optBeforeBeing.isPresent()) {
				
				// Isolating the before entity to help in further comparisons
				MudBeing beforeBeing = optBeforeBeing.get();
			
				// Comparing before and after beings
				
				// Check attr modifiers changes
				checkChangedAttrModifiers(beforeBeing, afterBeing, msgList);
				checkAddedAttrModifiers(beforeBeing, afterBeing, msgList);

				// Check skill modifiers changes
				checkChangedSkillModifiers(beforeBeing, afterBeing, msgList);
				checkAddedSkillModifiers(beforeBeing, afterBeing, msgList);
				
				// Check slot changes
				checkSlotModifiers(beforeBeing, afterBeing, msgList);
			}
			
			// Execute the save operation
			savedBeing = pjp.proceed();
			
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
			

		} else {
			// In this case a place is being created, just proceed
			// (or it's a non playable being)
			savedBeing = pjp.proceed();
		}
		
		return savedBeing;
	}
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.delete(..)) && args(destroyedBeing)")
	public void sendDestroyNotification(ProceedingJoinPoint pjp, MudBeing destroyedBeing) throws Throwable {

		// Perform the delete operation
		pjp.proceed();
		
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
		rabbit.convertAndSend(beingExchange, "", beingNotification);
		
		// Preparing message request
		MessageRequest yoursDestroyedMessage = new MessageRequest();

		// Add the destroyed being to the list of changed entities
		yoursDestroyedMessage.addChangedEntity(EnumEntityType.BEING, destroyedBeing.getCode());
		
		// If the destroyed being belongs to a player, send a message to the player
		if (destroyedBeing.getPlayerId()!=null) {
			
			yoursDestroyedMessage.setMessageKey(BeingHelper.BEING_DESTROY_YOURS_MSG);
			
			messageService.putMessage(destroyedBeing.getCode(), yoursDestroyedMessage);
			
			log.info("world: {}, entityId: {}, message: {}",
					destroyedBeing.getCurWorld(),
					destroyedBeing.getCode(),
					yoursDestroyedMessage.getMessageKey()
					);
		}
		
		List<MudBeing> beingListInPlace =
				repository.findByCurWorldAndCurPlaceCode(
						destroyedBeing.getCurWorld(), 
						destroyedBeing.getCurPlaceCode());
		
		// Preparing the message request
		MessageRequest anotherDestroyedMessage = (MessageRequest)SerializationUtils.clone(yoursDestroyedMessage);
		anotherDestroyedMessage.setMessageKey(BeingHelper.BEING_DESTROY_ANOTHER_MSG);
		anotherDestroyedMessage.setArgs( new String[] {
				destroyedBeing.getName()!=null ? destroyedBeing.getName() : destroyedBeing.getBeingClass().getName()
		});

		// Send a message to other playable beings in the place
		beingListInPlace.stream()
			// Filtering any playable beings beside the one being destroyed (despite it being playable or not)
			.filter(curBeing -> !curBeing.getCode().equals(destroyedBeing.getCode()))
			// Filtering only playable characters
			.filter(curBeing -> curBeing.getPlayerId()!=null)
			.forEach(curBeing -> {
				
				// Send the message
				messageService.putMessage(curBeing.getCode(), anotherDestroyedMessage);
				
				log.info("world: {}, entityId: {}, message: {} ",
						destroyedBeing.getCurWorld(),
						curBeing.getCode(),
						anotherDestroyedMessage.getMessageKey()
						);
				
			});
		
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
	
	
	private void checkSlotModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<MessageRequest> msgList) {
		
		// Checking slots
		beforeBeing.getSlots().stream()
			.forEach(beforeSlot -> {
				
				// Gathering the same slot after change
				Optional<MudBeingSlot> optAfterSlot =
				afterBeing.getSlots().stream()
					.filter(afterSlot -> afterSlot.equals(beforeSlot))
					.findFirst();
				
				optAfterSlot.ifPresent(afterSlot -> {
					
					if ((afterSlot.getItemCode()!=null) &&   // we have an item in future slot
							(beforeSlot.getItemCode()==null ||  // we didn't have an item in previous slot
							(!beforeSlot.getItemCode().equals(afterSlot.getItemCode())))) { // or we had a different item
						
						msgList.add(
								buildSlotItemMessage(afterBeing.getCode(), 
										BeingHelper.BEING_EQUIP_MSG, 
										afterSlot.getItemCode())
							);
					}
					
					if ((beforeSlot.getItemCode()!=null) &&   // we had an item in previous slot
						(afterSlot.getItemCode()==null ||     // we don't have any item in future slot
						(!afterSlot.getItemCode().equals(beforeSlot.getItemCode())))) {  // or we have a different item

						msgList.add(
								buildSlotItemMessage(afterBeing.getCode(), 
										BeingHelper.BEING_UNEQUIP_MSG, 
										beforeSlot.getItemCode())
							);
					}
				});
			});
	}
	
	private MessageRequest buildSlotItemMessage(Long beingCode, String messageKey, Long itemCode) {
		
		// Preparing the message request
		MessageRequest mRequest = new MessageRequest();
		mRequest.addChangedEntity(EnumEntityType.BEING, beingCode);
		
		// Calling the item service to get the itemName
		Item item = itemService.getItem(itemCode);
		
		mRequest.setMessageKey(messageKey);
		mRequest.setArgs(new String[] {
				item.getName()
		});

		return mRequest;
	}
}
