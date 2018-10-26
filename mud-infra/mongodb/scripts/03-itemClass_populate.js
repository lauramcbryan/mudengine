var db = connect("127.0.0.1:27017/mudenginedb");

db.itemClass.insertMany(
	[
		{ "_id": "SCRAP", "name": "Scrap", "size":1, "weight":1 },
		{ "_id": "METAL", "name": "Metal", "size":0.01, "weight":0.01 },
		{ "_id": "ROCK", "name": "Rock", "size":0.01, "weight":0.01 },
		{ 
			"_id": "STONE", "name": "Stone", "size":1, "weight":1,
			"attrs": [
				{"code":"DUR", "value": 1000},
				{"code":"MAXDUR", "value": 1000}
			]
		},
		{ "_id": "BRICK", "name": "Brick", "size":0.01, "weight":0.01 },
		{ 
			"_id": "TREE", "name": "Tree", "size":100, "weight":100,
			"attrs": [
				{"code":"DUR", "value": 500},
				{"code":"MAXDUR", "value": 500}
			]
		},
		{ "_id": "TRUNK", "name": "Trunk", "size":10, "weight":10 },
		{ 
			"_id": "POWUNIT", "name": "Power Unit", "size":1, "weight":1,
			"attrs": [
				{"code":"DUR", "value": 500},
				{"code":"MAXDUR", "value": 500}
			]
		},
		{ "_id": "SEED", "name": "Seed", "size":0.01, "weight":0.01 },
		{ "_id": "RAWFOOD", "name": "Raw Food", "size":0.01, "weight":0.01 },
		{ "_id": "RATION", "name": "Ration", "size":0.01, "weight":0.01 },
		{ "_id": "PROCFOOD", "name": "Processed Food", "size":0.01, "weight":0.01 },
		{ "_id": "PROTEIN", "name": "Whey Protein", "size":0.01, "weight":0.01 },
		{ 
			"_id": "SCYTHE", "name": "Scythe", "size":0.5, "weight":0.5,
			"attrs": [
				{"code":"DUR", "value": 50},
				{"code":"MAXDUR", "value": 50}
			]
		},
		{ 
			"_id": "DRILL", "name": "Drill", "size":0.5, "weight":0.5,
			"attrs": [
				{"code":"DUR", "value": 50},
				{"code":"MAXDUR", "value": 50}
			]
		},

		{ "_id": "LOG", "name": "Log", "size":0.25, "weight":0.25 },
		{ "_id": "PISTOL", "name": "Pistol", "size":0.25, "weight":0.25 },
	]
);
