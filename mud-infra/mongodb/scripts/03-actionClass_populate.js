var db = connect("127.0.0.1:27017/mudenginedb");

db.actionClass.insertMany(
	[
		{
			"_id": 1,
			"class": "WALK",
			"locale": "en-US",
			"verb": "walk",
			"category": "GAME",
			"description": "Move to another place",
			"usage": "walk <direction>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "direction",
					"inputMessage": "Please enter the direction",
					"type": "DIRECTION",
					"required": true
				}
			]
		},
		{
			"_id": 2,
			"class": "LOOKP",
			"locale": "en-US",
			"verb": "examine",
			"category": "GAME",
			"description": "Get details from a place",
			"usage": "look <PLACE>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the place to look at",
					"type": "PLACE",
					"required": true
				}
			]
		},
		{
			"_id": 3,
			"class": "LOOKB",
			"locale": "en-US",
			"verb": "examine",
			"category": "GAME",
			"description": "Get details from a being",
			"usage": "look <being>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the being to look at",
					"type": "BEING",
					"required": true
				}
			]
		},
		{
			"_id": 4,
			"class": "LOOKI",
			"locale": "en-US",
			"verb": "examine",
			"category": "GAME",
			"description": "Get details from an item",
			"usage": "look <item>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the item to look at",
					"type": "ITEM",
					"required": true
				}
			]
		},
		{
			"_id": 5,
			"class": "TAKE",
			"locale": "en-US",
			"verb": "take",
			"category": "GAME",
			"description": "Take an item",
			"usage": "take <item>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the item",
					"type": "ITEM",
					"required": true
				}
			]
		},
		{
			"_id": 6,
			"class": "DROP",
			"locale": "en-US",
			"verb": "drop",
			"category": "GAME",
			"description": "Drop an item",
			"usage": "drop <item>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the item",
					"type": "ITEM",
					"required": true
				}
			]
		},
		{
			"_id": 7,
			"class": "TALKTO",
			"locale": "en-US",
			"verb": "talk to",
			"category": "GAME",
			"description": "Talk to another being",
			"usage": "talk to <item>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the being",
					"type": "BEING",
					"required": true
				},
				{
					"name": "targetMessage",
					"inputMessage": "Please input the message",
					"type": "ANY_STRING",
					"required": true
				}
			]
		},
		{
			"_id": 8,
			"class": "SHOUT",
			"locale": "en-US",
			"verb": "shout to",
			"category": "GAME",
			"description": "Shout at a place",
			"usage": "shout at <place>",
			"runType": "SIMPLE",
			"parameters": [
				{
					"name": "targetCode",
					"inputMessage": "Please input the place",
					"type": "PLACE",
					"required": true
				},
				{
					"name": "targetMessage",
					"inputMessage": "Please input the message",
					"type": "ANY_STRING",
					"required": true
				}
			]
		}
	]
);
