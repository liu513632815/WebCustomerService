var _websocket = null;
var editor = null;

$(function() {
	consumerSocket.init();
	/*
	 * window.onbeforeunload = onbeforeunload_handler; window.onunload =
	 * onunload_handler; function onbeforeunload_handler(){ var warning="";
	 * return warning; }
	 * 
	 * function onunload_handler(){ var warning=""; alert(warning); }
	 */  
})

var consumerSocket = {
	
	init : function(){
		consumerSocket.page();
		consumerSocket.socket();
		
	},
	
	page : function(){
		var win_width = $(document).width();
		var win_height = $(document).height();
		$("#content").height(win_height - 70);
		$("#content_left_top").height($("#content").height() - 300);
		$("#content_left").width(win_width - $("#content_right").outerWidth());
		$("#content_left_bottom").width(win_width - $("#content_right").outerWidth());
		
		$(window).resize(function(){
			$("#content").height($(window).height() - 70);
			$("#content_left_top").height($("#content").height()-300);
			$("#content_left").width($(document).width() - $("#content_right").outerWidth());
			$("#content_left_bottom").width($(document).width() - $("#content_right").outerWidth());
		});
		
		$(document).keydown(function(event) {
			if (event.keyCode == 13) {
				event.preventDefault();
				consumerSocket.send();
			}
		});
		
		consumerSocket.emotion();
	},
	emotion : function(){
		var E = window.wangEditor
        editor = new E('#input_menu','#msgInput');
		editor.customConfig.menus = [
	        'emoticon',
	        'fontName',
	        'fontSize',
	        'bold',
	        'italic',
	        'underline',
	        'foreColor',
	        'image'
	    ];
		 editor.customConfig.uploadImgShowBase64 = true;
		 // editor.customConfig.uploadImgServer = '/upload';
		 // editor.customConfig.uploadFileName = 'myFileName'
		 editor.customConfig.emotions = [{
			 title: '表情',
			 type: 'image',
			 content: consumerSocket.initEmotion()
		 }]
        editor.create();
	},
	socket:function(){
		
		if ('WebSocket' in window) {
			_websocket = new WebSocket("ws://10.101.2.214:8080/websocket/consumer/null");
		} else {
			alert("当前浏览器不支持WebSocket")
		}

		// 连接发生错误时回调
		_websocket.onerror = function(event) {
			console.log(event.data);
		};

		// 建立连接时回调
		_websocket.onopen = function(event) {
			
		}

		// 接收到消息时回调
		_websocket.onmessage = function(event) {
			flashTitle();
			consumerSocket.receive(event.data);
			consumerSocket.moveCursorEnd();
		}

		// 连接关闭时回调
		_websocket.onclose = function(event) {
			// alert("连接已关闭")
		}
	},
	moveCursorEnd : function(){// 移动光标到最低端
		$("#msgShow").children("div")[$("#msgShow").children("div").length-1].scrollIntoView();
	},
	receive : function(data){
		var json = JSON.parse(data);
		if(json.messageType==0){// 自动回复
			consumerSocket.rebotReply(json.message);
		}else if(json.messageType==3){// 广告回复
			consumerSocket.adReply(json.message);
		}else{
			$("#msgShow").append("<div style='clear:both'><div style='background-color: #ececec;padding: .5em .6em;border-radius: 5px;float: left;margin-right: 30%;margin-top: 5px;margin-bottom: 5px'>"+json.message+"</div><div><br/>")
		}
	},
	
	// 机器自动答复
	rebotReply : function(message){
		$("#msgShow").append("<div style='background-color: #ececec;padding: .5em .6em;border-radius: 5px;margin-top: 40px;margin-bottom: 5px'>"+message+"</div>");
	},
	// 广告回复
	adReply : function(message){
		$("#msgShow").append("<div style='background-color: #ececec;padding: .5em .6em;border-radius: 5px;margin-top: 5px;margin-bottom: 5px'>"+message+"</div>");
		$("#msgShow").append("<div style='text-align: center;'><div style='font-size: 6px; background-color: #dcdada; width: 180px; margin: auto; color: white; border-radius: 1em 1em; padding: 4px;'>"+new Date().Format("yyyy-MM-dd hh:mm:ss")+"开始沟通</div></div>");
	},
	send : function(){
		var sendObj = $('#msgInput').children("div").children("p");
		if(editor.txt.html().trim()!=""){
			$("#msgShow").append("<div style='clear:both'><div style='float: right;background-color: #89e871;padding: .5em .6em;border-radius: 5px;margin-left: 30%;margin-top: 10px;margin-bottom: 5px;'>"+sendObj.html()+"</div></div><br/>")
			_websocket.send(sendObj.html());
			editor.txt.clear();
		}
		consumerSocket.moveCursorEnd();
	},
	
	initEmotion: function(){
		var a = [];
		for (var i = 1; i < 76; i++) {
			var temp = {};
			temp.alt="";
			temp.src="img/emotion/arclist/"+i+".gif";
			a.push(temp);
		}
		return a;
	}
	
}

// ================================================浏览器标题消息提示开始==========================================
var isWindowFocus = true;
function focusin() { isWindowFocus=true;}
function focusout() { isWindowFocus=false;}
// 注册焦点变化监听器
if ("onfocusin" in document){// for IE
    document.onfocusin = focusin;
    document.onfocusout = focusout;
} else {
    window.onblur = focusout;
    window.onfocus= focusin;
}

var flag = true;
function flashTitle(){
	// 仅窗口不在焦点时闪烁title，回到焦点时停止闪烁并将title恢复正常
	 if(isWindowFocus){// 当前处于焦点
	  document.title="在线咨询";
	  return;// 退出循环
	 }
	 
	 if(flag){
		 document.title="【您有新的消息】";
		 flag = false;
	 }else{
		 document.title="【　　　　　　】";
		 flag = true;
	 }
	 setTimeout("flashTitle()",10);  // 循环
}
// ================================================浏览器标题消息提示结束==========================================


Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "h+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds() // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
}
