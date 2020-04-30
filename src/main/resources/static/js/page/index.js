$(function() {

})

var _index = {

	CloseAd : function() {// 关闭广告推送
		$.ajax({
			url : "/contorAd",
			dataType : "json",
			success : function(data) {
				if (JSON.parse(data)) {
					$("#ad").text("close Ad");
				} else {
					$("#ad").text("open Ad");
				}
			}
		})
	},
	CloseAuto : function() {// 关闭自动回复
		$.ajax({
			url : "/contorAuto",
			dataType : "json",
			success : function(data) {
				if (JSON.parse(data)) {
					$("#auto").text("close Reply");
				} else {
					$("#auto").text("open Reply");
				}
			}
		})
	},
	setInfo : function(type) {// 设置信息：自动回复/广告
		if (type == 1) {// 广告
			window.location.href = "/editAd";
		} else {// 自动回复
			window.location.href = "/editReply";
		}
	},
}