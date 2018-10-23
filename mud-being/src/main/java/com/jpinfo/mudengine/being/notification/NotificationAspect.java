package com.jpinfo.mudengine.being.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import lombok.Getter;

@Aspect
@Component
public class NotificationAspect {

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
	

	/**
	 * Local class to hold the notification messages to be send to the notification service
	 * after the entity upate is consolidated.
	 */
	@Getter
	class BeingMessage {
		private Long targetCode;
		private String message;
		private String[] parms;
		
		public BeingMessage(Long targetCode, String message, String... parms) {
			this.targetCode = targetCode;
			this.message = message;
			this.parms = parms;
		}
	}
	
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.save(..)) && args(afterBeing)")
	public Object compareBeing(ProceedingJoinPoint pjp, MudBeing afterBeing) throws Throwable {
		
		// Object returned after save operation
		Object savedBeing;
		
		// Checking if the future state entity has a ID as this method
		// is also used to create entities
		// Additionally, we only throw notification for playable beings.
		if ((afterBeing.getCode()!=null) && (afterBeing.getPlayerId()!=null)) {
			
			List<BeingMessage> notifications = new ArrayList<>();

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
				checkAttrModifiers(beforeBeing, afterBeing, notifications);

				// Check skill modifiers changes
				checkSkillModifiers(beforeBeing, afterBeing, notifications);
				
				// Check slot changes
				checkSlotModifiers(beforeBeing, afterBeing, notifications);
			}
			
			// Execute the save operation
			savedBeing = pjp.proceed();
			
			// Pass through the notification list
			notifications.stream()
				.forEach(curMessage ->
				
					// Send the message
					messageService.putMessage(curMessage.getTargetCode(), 
							curMessage.getMessage(),
							curMessage.getParms())
				);
			

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
		
		
		// If the destroyed being belongs to a player, send a message to the player
		if (destroyedBeing.getPlayerId()!=null) {
			messageService.putMessage(destroyedBeing.getCode(), BeingHelper.BEING_DESTROY_YOURS_MSG);
		}
		
		List<MudBeing> beingListInPlace =
				repository.findByCurWorldAndCurPlaceCode(
						destroyedBeing.getCurWorld(), 
						destroyedBeing.getCurPlaceCode());

		// Send a message to other playable beings in the place
		beingListInPlace.stream()
			// Filtering any playable beings beside the one being destroyed (despite it being playable or not)
			.filter(curBeing -> !curBeing.getCode().equals(destroyedBeing.getCode()))
			// Filtering only playable characters
			.filter(curBeing -> curBeing.getPlayerId()!=null)
			.forEach(curBeing -> 
				messageService.putMessage(curBeing.getCode(), 
						BeingHelper.BEING_DESTROY_ANOTHER_MSG, 
						destroyedBeing.getName()!=null ? destroyedBeing.getName() : destroyedBeing.getBeingClass().getName()
								)
			);
		
	}
	
	private void checkAttrModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<BeingMessage> notifications) {
		
		// Check attr modifiers that was changed/removed
		beforeBeing.getAttrModifiers().stream()
			.forEach(beforeModifier -> {
				
				Optional<MudBeingAttrModifier> optAfterModifier=
					afterBeing.getAttrModifiers().stream()
						.filter(afterModifier -> afterModifier.equals(beforeModifier))
						.findFirst();
				
				if (optAfterModifier.isPresent()) {
					
					// add modifier changed
					MudBeingAttrModifier afterModifier = optAfterModifier.get();
					
					if (beforeModifier.getOffset() < afterModifier.getOffset()) {

						// increase modifier
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_ATTRMOD_INCREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(afterModifier.getOffset() - beforeModifier.getOffset())
										)
								);
						
					} else if (beforeModifier.getOffset() > afterModifier.getOffset()) {

						// decrease modifier
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_ATTRMOD_DECREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(beforeModifier.getOffset() - afterModifier.getOffset())
										)
								);
					}
					
				} else {

					// attr modifier removed
					if (beforeModifier.getOffset()>0.0f) {
						
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_ATTRMOD_DECREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(beforeModifier.getOffset())
										)
								);
					} else {
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_ATTRMOD_INCREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(Math.abs(beforeModifier.getOffset()))
										)
								);
					}
				}
				
				
				
			});
		
		// Check attr modifiers that was added
		afterBeing.getAttrModifiers().stream()
			.filter(afterModifier -> !beforeBeing.getAttrModifiers().contains(afterModifier))
			.forEach(afterModifier -> {
				
				// attr modifier added
				if (afterModifier.getOffset()>0.0f) {
					
					notifications.add(
							new BeingMessage(afterBeing.getCode(), 
									BeingHelper.BEING_ATTRMOD_INCREASE_MSG, 
									afterModifier.getId().getCode(),
									String.valueOf(afterModifier.getOffset())
									)
							);
				} else {
					notifications.add(
							new BeingMessage(afterBeing.getCode(), 
									BeingHelper.BEING_ATTRMOD_DECREASE_MSG, 
									afterModifier.getId().getCode(),
									String.valueOf(Math.abs(afterModifier.getOffset()))
									)
							);
				}
			});
	}
	
	private void checkSkillModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<BeingMessage> notifications) {
		
		// Check skill modifiers that was changed/removed
		beforeBeing.getSkillModifiers().stream()
			.forEach(beforeModifier -> {
				
				Optional<MudBeingSkillModifier> optAfterModifier=
					afterBeing.getSkillModifiers().stream()
						.filter(afterModifier -> afterModifier.equals(beforeModifier))
						.findFirst();
				
				if (optAfterModifier.isPresent()) {
					
					// add modifier changed
					MudBeingSkillModifier afterModifier = optAfterModifier.get();
					
					if (beforeModifier.getOffset() < afterModifier.getOffset()) {

						// increase modifier
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_SKILLMOD_INCREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(afterModifier.getOffset() - beforeModifier.getOffset())
										)
								);
						
					} else if (beforeModifier.getOffset() > afterModifier.getOffset()) {

						// decrease modifier
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_SKILLMOD_DECREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(beforeModifier.getOffset() - afterModifier.getOffset())
										)
								);
					}
					
				} else {

					// skill modifier removed
					if (beforeModifier.getOffset()>0.0f) {
						
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_SKILLMOD_DECREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(beforeModifier.getOffset())
										)
								);
					} else {
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_SKILLMOD_INCREASE_MSG, 
										beforeModifier.getId().getCode(),
										String.valueOf(Math.abs(beforeModifier.getOffset()))
										)
								);
					}
				}
				
				
				
			});
		
		// Check skill modifiers that was added
		afterBeing.getSkillModifiers().stream()
			.filter(afterModifier -> !beforeBeing.getSkillModifiers().contains(afterModifier))
			.forEach(afterModifier -> {
				
				// skill modifier added
				if (afterModifier.getOffset()>0.0f) {
					
					notifications.add(
							new BeingMessage(afterBeing.getCode(), 
									BeingHelper.BEING_SKILLMOD_INCREASE_MSG, 
									afterModifier.getId().getCode(),
									String.valueOf(afterModifier.getOffset())
									)
							);
				} else {
					notifications.add(
							new BeingMessage(afterBeing.getCode(), 
									BeingHelper.BEING_SKILLMOD_DECREASE_MSG, 
									afterModifier.getId().getCode(),
									String.valueOf(Math.abs(afterModifier.getOffset()))
									)
							);
				}
			});		
	}
	
	private void checkSlotModifiers(MudBeing beforeBeing, MudBeing afterBeing, List<BeingMessage> notifications) {
		
		// Checking slots that was changed
		beforeBeing.getSlots().stream()
			.forEach(beforeSlot -> {
				
				Optional<MudBeingSlot> optAfterSlot =
				afterBeing.getSlots().stream()
					.filter(afterSlot -> afterSlot.equals(beforeSlot))
					.findFirst();
				
				if (optAfterSlot.isPresent()) {
					
					MudBeingSlot afterSlot = optAfterSlot.get();
					
					// If the slot changed content
					if ((beforeSlot.getItemCode()==null) && (afterSlot.getItemCode()!=null)) {

						// being.equip
						Item equippedItem = itemService.getItem(afterSlot.getItemCode());
						
						notifications.add(
								new BeingMessage(afterBeing.getCode(), 
										BeingHelper.BEING_EQUIP_MSG, 
										equippedItem.getName())
								);
						
					} else
						if ((beforeSlot.getItemCode()!=null) && (afterSlot.getItemCode()==null)) {

							// being.unequip
							Item unequippedItem = itemService.getItem(beforeSlot.getItemCode());
							
							notifications.add(
									new BeingMessage(afterBeing.getCode(), 
											BeingHelper.BEING_UNEQUIP_MSG, 
											unequippedItem.getName())
									);
							
						} else
							if (beforeSlot.getItemCode().equals(afterSlot.getItemCode())) {
								// being.unequip
								Item unequippedItem = itemService.getItem(beforeSlot.getItemCode());
								
								notifications.add(
										new BeingMessage(afterBeing.getCode(), 
												BeingHelper.BEING_UNEQUIP_MSG, 
												unequippedItem.getName())
										);

								// being.equip
								Item equippedItem = itemService.getItem(afterSlot.getItemCode());
								
								notifications.add(
										new BeingMessage(afterBeing.getCode(), 
												BeingHelper.BEING_EQUIP_MSG, 
												equippedItem.getName())
										);
							}
						
					
				}
			});
		
	}
}
