var db = connect("127.0.0.1:27017/graylog");

db.createUser(
	{
		user: "mudlog",
		pwd: "mudlog",
		roles: [ "dbOwner" ]
	}
);
