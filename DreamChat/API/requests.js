﻿var db = require('./DBconnect.js');


module.exports = {
    sendRequest: function (senderid, receiverid, callback) {
        var code;
        db.query("SELECT COUNT(*) AS reqcount FROM friend_requests WHERE (senderid='" + senderid + "' AND receiverid='" + receiverid + "') OR (senderid='" + receiverid + "' AND receiverid='" + senderid + "')", function (result) {
            var count = result.reqcount;
            db.query("SELECT COUNT(*) AS friendshipscount FROM friendships WHERE (user1='" + senderid + "' AND user2='" + receiverid + "') OR (user1='" + receiverid + "' AND user2='" + senderid + "')", function (result2) {
                if (result2.friendshipscount + count != 0) {
                    if (typeof callback === "function") {
                        callback(false,409);
                    }
                }
                else {
                    db.nonQuery("INSERT INTO friend_requests(sender,receiver) VALUES(" + senderid + ", " + receiverid + ")", function (success) {
                        if (typeof callback === "function") {
                            if (success) {
                                callback(success, 200);
                            }
                            else {
                                callback(success, 500);
                            }
                        }
                    });
                }
            });
        })		
	},

	cancelRequest: function (requestid, callback) {
		db.nonQuery("DELETE FROM friend_requests WHERE requestid="+requestid, function (success) {
			if (typeof callback === "function") {
				callback(success);
			}
		});
	},

	acceptRequest: function (requestid, callback) {
        var overallSuccess = true;
        var code;
        db.query("SELECT * FROM friend_requests  WHERE requestid=" + requestid, function (result) {
            var senderid = result[0].sender;
            var receiverid = result[0].receiver;
            if (senderid != "undefined" && receiverid != "undefined") {
                db.nonQuery("DELETE FROM friendships  WHERE  (user1='" + senderid + "' AND user2='" + receiverid + "') OR (user1='" + receiverid + "' AND user2='" + senderid + "')", function () {
                    db.nonQuery("INSERT INTO friendships(user1,user2) VALUES(" + result[0].sender + ", " + result[0].receiver + ")", function (success) {
                        if (success) {
                            db.nonQuery("DELETE FROM friend_requests WHERE requestid=" + requestid, function (success) {
                                if (success) {
                                    console.log("request accetion successful");
                                    code = 200;
                                }
                                else {
                                    console.log("deletion failed while accepting request");
                                    overallSuccess = false;
                                    code = 200;
                                }
                            });
                        }
                        else {
                            console.log("insertion failed while accepting request");
                            overallSuccess = false;
                            code = 500;
                        }
                    });
                });			
			}
			else {
				console.log("selection failed while accepting request");
                overallSuccess = false;
                code = 400; //bad request
			}					
		});
		if (typeof callback === "function") {
			callback(overallSuccess,code);
		}
	},
	
	declineRequest: function (requestid, callback) {
		db.nonQuery("DELETE FROM friend_requests WHERE requestid=" + requestid, function (success) {
			if (typeof callback === "function") {
				callback(success);
			}
		});
	},

	loadRequests: function (userid,callback) {
		db.query("SELECT userid,name,surname,nickname FROM friend_requests INNER JOIN users ON friend_requests.sender=users.userid WHERE receiver=" + userid, function (results) {
			if (typeof callback === "function") {
				callback(results);
			}
		});
	}
};