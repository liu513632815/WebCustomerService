var _websocket = null;
// 当前聊天用户sessionId
var selectedSessionId = null;
// 当前聊天用户数
var consumerlength = 0;

var editor = null;

$(function() {
	customerSocket.init();
})

var customerSocket = {

	init : function(){
		customerSocket.page();
		customerSocket.socket();
	},
	
	page : function(){
		win_width = $(document).width();
		win_height = $(document).height();
		$("#content").height(win_height - 70);
		$("#content_right").width(win_width - $("#content_left").outerWidth());
		$("#content_right_top").height($("#content_right").height()-300);
		
		$(window).resize(function(){
			$("#content").height($(window).height() - 70);
			$("#content_right").width($(document).width() - $("#content_left").outerWidth());
			$("#content_right_top").height($("#content_right").height()-300);
		});
		
		$(document).keydown(function(event) {
			if (event.keyCode == 13) {
				event.preventDefault();
				customerSocket.send();
			}
		});
		
		customerSocket.emotion();
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
		editor.customConfig.emotions = [{
			title: '表情',
			type: 'image',
			content: customerSocket.initEmotion()
		}];
		editor.customConfig.onfocus = function () {
			customerSocket.readMessage();
	    };
        editor.create();
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
	},
	
	socket : function() {
		
		if ('WebSocket' in window) {
			_websocket = new WebSocket("ws://10.101.2.214:8080/websocket/customer/null");
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
			customerSocket.recevie(event.data);
			customerSocket.moveCursorEnd();
		}

		// 连接关闭时回调
		_websocket.onclose = function(event) {
			//alert("连接已关闭")
		}
	},
	moveCursorEnd : function(){// 移动光标到最低端
		$("#content_right_top").children("div:visible").children("div")[[$("#content_right_top").children("div:visible").children("div").length-1]].scrollIntoView();
	},
	recevie : function(data){
		var json = JSON.parse(data);
		if (json.messageType==2) {// 下线消息
			customerSocket.offlineConsumer(json);
		} else {// 输出消息
			customerSocket.addConsumerList(json);
		}
	},
	
	send : function(){
		var sendObj = $('#msgInput').children("div").children("p");
		if(editor.txt.html()!=""){
			customerSocket.showMessageTab(sendObj.html(),selectedSessionId);
			_websocket.send(JSON.stringify({
				"sessionId" : selectedSessionId,
				"message" : sendObj.html()
			}));
			editor.txt.clear();
		}
		customerSocket.moveCursorEnd();
	},
	
	addConsumerList:function(json){
		if (!($("#list_div_" + json.sessionId).length > 0)){//客户列表去重
			consumerlength++;// 记录当前聊天用户数
			if($("#content_left").find("div").length>0){
				$("#content_left").append("<div msg_num=0 id='list_div_"+json.sessionId+"' onmouseover='customerSocket.showRemoveIco(\""+json.sessionId+"\",this);' onmouseout='customerSocket.hideRemoveIco(\""+json.sessionId+"\",this);' onclick='customerSocket.clickConsumer(\""+json.sessionId+"\",this);' style='position: relative; height: 50px; line-height: 3; border-radius: 3px; margin: 4px; cursor: pointer;'><img src='/img/timg.jpg' style='border-radius: 5px; position: absolute; left: 10px; top: 3px;' width='42px' height='42px'></img><span>编号"+json.sessionId+"用户</span><label id='msgTip_"+json.sessionId+"' class='msgTip'>0</label></div>");
			}else{
				$("#content_left").append("<div msg_num=0 id='list_div_"+json.sessionId+"' onmouseover='customerSocket.showRemoveIco(\""+json.sessionId+"\",this);' onmouseout='customerSocket.hideRemoveIco(\""+json.sessionId+"\",this);' onclick='customerSocket.clickConsumer(\""+json.sessionId+"\",this);' class='selected' style='position: relative; height: 50px; line-height: 3; border-radius: 3px; margin: 4px; cursor: pointer;'><img src='/img/timg.jpg' style='border-radius: 5px; position: absolute; left: 10px; top: 3px;' width='42px' height='42px'></img><span>编号"+json.sessionId+"用户</span><label id='msgTip_"+json.sessionId+"' class='msgTip'>0</label></div>");
			}
			$("#list_div_"+json.sessionId).append("<label id='label_remove_"+json.sessionId+"' style='cursor: pointer;display: none;position: absolute;right: 12px;text-align: center;color: white;background: #171616d9;height: 16px;line-height: 1;border-radius: 10px;padding: 3px 6px 0 5px;font-size: 12px;top: 15px;' onclick='customerSocket.deleteConsumerList(\""+json.sessionId+"\",this);'>X</label>");
		}
		customerSocket.recevieMessageTab(json);
	},
	
	recevieMessageTab : function(json){
		var html = "";

		if($("#msg_div_"+json.sessionId).length>0){
			html += "<div style='clear:both'>";
			html += "<div style='float: right;background-color: #ececec;padding: .5em .6em;border-radius: 5px;float: left;margin-right: 30%;margin-top: 10px;margin-bottom: 5px;'>"+json.message+"</div>"
			html += "</div><br/>";
			$("#msg_div_"+json.sessionId).append(html);
		}else{
			if(consumerlength==1){
				html += "<div id='msg_div_"+json.sessionId+"' style='height: 100%; overflow:auto;padding: 0 15px 0 15px;'>";
				selectedSessionId = json.sessionId;
			}else{
				html += "<div id='msg_div_"+json.sessionId+"' style='height: 100%; overflow:auto;padding: 0 15px 0 15px;display: none;'>";
			}
			html += "<div style='clear:both'>";
			html += "<div style='float: right;background-color: #ececec;padding: .5em .6em;border-radius: 5px;float: left;margin-right: 30%;margin-top: 10px;margin-bottom: 5px;'>"+json.message+"</div>"
			html += "</div><br/>";
			html += "</div>";
			$("#content_right_top").append(html);
		}
		
		// 设置信息数和消息提示
		var old_m = $("#list_div_" + json.sessionId).attr("msg_num");
		var new_m = parseInt(old_m) + 1;
		$("#list_div_" + json.sessionId).attr("msg_num",new_m);
		customerSocket.messageTip(json);
	},
	
	//设置消息提示
	messageTip:function(json){
		$("#msgTip_"+json.sessionId).show();
		$("#msgTip_"+json.sessionId).html($("#list_div_" + json.sessionId).attr("msg_num"));
	},
	
	// 在客户列表点击用户
	clickConsumer:function(sessionId,obj){
		selectedSessionId = sessionId;
		// tab页设置
		$("#content_right_top").children("div").hide();
		$("#msg_div_"+sessionId).show();
		// 客户列表设置
		$("#content_left").children("div").removeClass("selected");
		$(obj).addClass("selected");
		
		customerSocket.readMessage(sessionId);
	},
	
	// 鼠标移到列表上，显示删除ICO
	showRemoveIco :function(session_id,obj){
		$("#label_remove_"+session_id).show();
	},
	// 鼠标移到列表上，隐藏删除ICO
	hideRemoveIco :function(session_id,obj){
		$("#label_remove_"+session_id).hide();
	},
	
	// 删除列表用户
	deleteConsumerList : function(session_id,obj){
		$("#list_div_"+session_id).remove();
		$("#msg_div_"+session_id).remove();
		
		$("#content_left").children("div:first").addClass("selected");
		$("#content_right_top").children("div:first").show();
		event.stopPropagation();
	},
	
	// 消息已读，去除红点提示
	readMessage : function(sessionId) {
		if(sessionId){
			$("#msgTip_"+sessionId).hide();
			$("#list_div_" + sessionId).attr("msg_num",0);
		}else{
			$("#msgTip_"+selectedSessionId).hide();
			$("#list_div_" + selectedSessionId).attr("msg_num",0);
		}
	},
	
	// 用户下线消息处理:1.图像置灰 2.在列表中往后排
	offlineConsumer : function(json){
		
	},
	
	// 客服自己发送的消息显示在tab页面上
	showMessageTab : function(message,consumerSessionId){
		var html = "";
		html += "<div style='clear:both'>";
		html += "<div style='float: right;background-color: #89e871;padding: .5em .6em;border-radius: 5px;margin-left: 30%;margin-top: 10px;margin-bottom: 5px;'>"+message+"</div>"
		html += "</div><br/>";
		$("#msg_div_"+consumerSessionId).append(html);
	}

}


//================================================浏览器标题消息提示开始==========================================
var isWindowFocus = true;
function focusin() { isWindowFocus=true;}
function focusout() { isWindowFocus=false;}
//注册焦点变化监听器
if ("onfocusin" in document){//for IE 
    document.onfocusin = focusin;
    document.onfocusout = focusout;
} else {
    window.onblur = focusout;
    window.onfocus= focusin;
}

var flag = true;
function flashTitle(){
	//仅窗口不在焦点时闪烁title，回到焦点时停止闪烁并将title恢复正常
	 if(isWindowFocus){//当前处于焦点
	  document.title="客服";
	  return;//退出循环
	 }
	 
	 if(flag){
		 document.title="【您有新的消息】";
		 flag = false;
	 }else{
		 document.title="【　　　　　　】";
		 flag = true;
	 }
	 setTimeout("flashTitle()",10);  //循环
}
//================================================浏览器标题消息提示结束==========================================


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