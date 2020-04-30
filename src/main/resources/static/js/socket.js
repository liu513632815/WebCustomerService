/**
 * socket
 */

var _websocket = null;

var _socket = {
	init : function(url) {
		if ('WebSocket' in window) {
			_websocket = new WebSocket(url);
		} else {
			alert("当前浏览器不支持WebSocket")
		}

		// 连接发生错误时回调
		_websocket.onerror = function(event) {

		};

		// 建立连接时回调
		_websocket.onopen = function(event) {

		}

		// 接收到消息时回调
		_websocket.onmessage = function(event) {
			var json = JSON.parse(event.data);
			// 输出event.data信息到页面
			$('#text').append(json.message).append('\n');

			$('#sessionIdList').append(
					"<option value='" + json.sessionId + "'>" + json.sessionId
							+ "</option>");
		}

		// 连接关闭时回调
		_websocket.onclose = function(event) {
			// 调用websocket.close()或关闭页面时
		}
	},
	send : function() {
		_websocket.send($('#text').val());
	}
}