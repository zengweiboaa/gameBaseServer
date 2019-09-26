/**
 * 
 */
var socket;
if (!window.WebSocket) {
	window.WebSocket = window.MozWebSocket;
}

function sendHeartMsg() {
	var timestamp = (new Date()).valueOf();
	var m5 = timestamp % 100000;
	var hMsg = "{'t':" + m5 + "}";
	socket.send(hMsg);
}

var heartOp = false;

var heartCheck = {
	    timeout: null,//ms毫秒间隔
	    timeoutStr: null,
	    timeoutObj: null,
	    reset:function(){
	    	clearInterval(heartCheck.timeoutObj);
	    	//setTimeout(this.start,this.timeout);
			heartCheck.start();
		},
		start:function(){
			heartCheck.timeoutObj = window.setInterval(sendHeartMsg,heartCheck.timeout);
		},
		stop:function(){
	    	heartCheck.timeoutObj = null;
			clearInterval(heartCheck.timeoutObj);
		}
}

if (window.WebSocket) {
	var host  = window.location.hostname;
	
	var url = "ws://" + host + ":9998/ws";
	//var url = "ws://192.168.1.231:9998/ws";
	//socket = new WebSocket("ws://DESKTOP-GeTOUO:9998/ws");
	socket = new WebSocket(url);
	
	socket.onmessage = function(event) {
		console.log("-------------------start------------------");
		console.log("on message ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		var dat = event.data
		console.log(dat);
		try {
			eval('var onopen = (' + dat + ')');
		
			if(onopen.timeout && onopen.timeoutCount){
				
				console.log("heartCheck.timeout  " + heartCheck.timeout);
				try {
					heartCheck.timeout = onopen.timeout;
				} catch (e) {
					console.log(e);
				}
				console.log("heartCheck.timeout  " + heartCheck.timeout);
				//heartCheck.timeoutStr = obj.heartContexts[1];
				heartCheck.start();
				return;
			}
			
			if(obj.t) {
				var timestamp = (new Date()).valueOf();
				var ms = timestamp % 100000 - obj.t;
				console.log("当前系统延迟:" + ms + " ms");
				return;
			}
			
		} catch (e) {
			//alert(e.name + ": " + e.message);
		}
		console.log("11111111111111111111111");
		console.log("1" + dat);
		onlogicmessage(dat);
		console.log("2" + dat);
		console.log("11111111111111111111111");
		
		
		
		
		//var ta = document.getElementById('responseText');
		//ta.value = ta.value + '---->> 收到服务器消息:' + event.data + "\n";
		heartCheck.reset();
		
		
		
		//var obj = JSON.parse(event.data);
		//test(dat);
		console.log("--------------------end-----------------");
	};
	socket.onopen = function(event) {
		//heartCheck.start();
		//socket.send("onopen");
		//var ta = document.getElementById('notice');
		
		var noticeDiv = createNoticeDiv("连接开启", "WebSocket连接开启!");
		var old = document.getElementById("notice").innerHTML;
		document.getElementById("notice").innerHTML = old + noticeDiv;
		//ta.value = "连接开启!\n";
		
	};
	socket.onclose = function(event) {
		heartCheck.stop();
		var ta = document.getElementById('notice');
		var noticeDiv = createNoticeDiv("连接通知", "连接被关闭");
		//ta.value = ta.value + "连接被关闭\n";
		var old = document.getElementById("notice").innerHTML;
		document.getElementById("notice").innerHTML = old + noticeDiv;
		//reconnect;
	};
} else {
	alert("你的浏览器不支持 WebSocket！");
}

function createNoticeDiv(typeContext, showContext){
	var div = "";
	div = "<div align='center' style='border:1px solid #FFFFFF;'>"
		+ "<table border=1 style='table-layout:fixed;'> "
		+ "<COL><COL WIDTH=100><COL WIDTH=100>"
			+ "<tr>" 
				+ "<td><div style='width: 50px'> " 
				+ "<div style='color: #FFFFFF;background-color: rgb(233, 104, 107); border:5px solid rgb(233, 104, 107); border-radius: 5px;'>" 
				+ typeContext + " : </div> " 
				+ "</div></td><td align='left'><div style='color: rgb(233, 104, 107);'>"+ Date() +"</div></td>" 
			+ "</tr>" 
			+ "<tr>" 
				+ "<td></td>" 
				+ "<td width=100px><div style='width: 400px;word-break: break-all;word-wrap: break-word;'> " 
				+ "<div style='color: #FFFFFF; background-color: rgb(233, 104, 107); border:5px solid rgb(233, 104, 107); border-radius: 5px;'>" + showContext + "</div></div></td>" 
			+ "</tr>" 
		+ "</table>"
		+ "</div>";
		return div;
}


function send(message) {

	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		socket.send(message);
	} else {
		alert("连接没有开启.");
	}
}

function showcontent(language){
    $('#content').html('Introduction to ' + language + ' language');
}