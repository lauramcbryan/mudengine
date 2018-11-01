var db = connect("127.0.0.1:27017/mudenginedb");

db.attribute.insertMany(
	[
		{ "_id": "STR",	"name": "Strength" },
		{ "_id": "DEX",	"name": "Dexterity"	},
		{ "_id": "INT",	"name": "Intelligence"	},
		{ "_id": "CHR",	"name": "Charisma"	},
		{ "_id": "HP",	"name": "HitPoints"	},
		{ "_id": "CGOCAP", "name": "Cargo Capacity"	},
		{ "_id": "SIZCAP", "name": "Size Capacity"	},
		{ "_id": "CGO", "name": "Cargo Carried"	}
	]
);

db.skillCategory.insertMany(
	[
		{ "_id": "ADM",	"name": "Administrator", "attrBased": "CHR" },
		{ "_id": "ENG",	"name": "Engineering", "attrBased": "INT" },
		{ "_id": "FIGHT", "name": "Brawler", "attrBased": "STR" },
		{ "_id": "SHOOT", "name": "Shooter", "attrBased": "DEX" },
		{ "_id": "MIND", "name": "Scholar", "attrBased": "INT" },
	]
);

db.skill.insertMany(
	[
		{ 
			"_id": "FARMER", 
			"category":
			{
				"$ref":"skillCategory", 
				"$id":"ADM"
			},
			"name": "Farmer", 
			"description": "Tender the fields" 
		},
		{ 
			"_id": "BRAWLER", 
			"category":
			{
				"$ref":"skillCategory", 
				"$id":"FIGHT"
			},
			"name": "Fighter", 
			"description": "Fight with base hands" 
		},
		{ 
			"_id": "SHOOTER", 
			"category":
			{
				"$ref":"skillCategory", 
				"$id":"SHOOT"
			},
			"name": "Shooter", 
			"description": "Shoot with projectile gun" 
		},
		{ 
			"_id": "BUILDER", 
			"category":
			{
				"$ref":"skillCategory", 
				"$id":"ENG"
			},
			"name": "Builder", 
			"description": "Construct buildings" 
		},
		{ 
			"_id": "SCHOLAR", 
			"category":
			{
				"$ref":"skillCategory", 
				"$id":"MIND"
			},
			"name": "Scholar", 
			"description": "Improved knowledge" 
		},

	]
);

db.beingClass.insertMany(
	[
		{ 
			"_id": "HUMAN", 
			"name": "Human", 
			"description": "Average human being",
			"size": 1,
			"weightCapacity": 10,
			"attrs": [
				{ "code": "STR", value: 8},
				{ "code": "DEX", value: 8},
				{ "code": "INT", value: 8},
				{ "code": "CHR", value: 8},
				{ "code": "HP", value: 10}
			],
			"skills": [
				{ "code": "FARMER", value: 10},
				{ "code": "BRAWLER", value: 50}
			]
		},
		{ 
			"_id": "CRINOS", 
			"name": "Werewolf", 
			"description": "Werewolf in Crinos form",
			"size": 2,
			"weightCapacity": 25,
			"attrs": [
				{ "code": "STR", value: 12},
				{ "code": "DEX", value: 10},
				{ "code": "INT", value: 6},
				{ "code": "CHR", value: 6},
				{ "code": "HP", value: 15}
			],
			"skills": [
				{ "code": "BRAWLER", value: 80}
			]
		},
		{ 
			"_id": "OX", 
			"name": "Ox", 
			"size": 4,
			"weightCapacity": 50,
			"attrs": [
				{ "code": "STR", value: 15},
				{ "code": "DEX", value: 6},
				{ "code": "INT", value: 6},
				{ "code": "CHR", value: 4},
				{ "code": "HP", value: 20}
			]
		}		
	]
);
