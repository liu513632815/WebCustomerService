package com.hg.socket;

/**
 * 应答/广告设置
 * 
 * @author Administrator
 *
 */
public class Setting {

	/**
	 * 无客服在线时，是否开启自动应答。默认开启
	 */
	public static volatile Boolean isAutoReply = true;

	/**
	 * 客户连接时是否推送广告。默认不推送
	 */
	public static volatile Boolean isAdReply = true;

	// 无可客服在线时自动答复内容
	public static String autoReply = "<font color='red'>您好，现在客服正忙</font>，建议您也可以加我们客服群QQ：XXXXXXXXXXX或QQ客服657706446，享受更便捷的服务，或咨询电话热线：26820611。";

	// 推荐/广告
	public static String adReply = "<h2>尊敬的客户：</h2>"
			+ "您好,深圳电信宽带优惠专区欢迎您！装宽带不排队,在线预约，足不出户办宽带，当天申请当天装（支持先装后付款，装好后可关注公众号支付，也可到深圳任何一个中国电信营业厅交现金，可开纸质发票或电子发票）感谢您对中国电信的支持与厚爱，我们将竭诚为您服务!"
			+ "亲，加微信 XXXXXXXXXXX 获取最便宜的套餐资费哦. 特惠赠送：加微信额外送99元微信红包,欲购从速哦！"
			+ " 服务热线：XXXXXXXX，XXXXXXXX宽带新装下单"
			+ "<br><br><a href=''>在线续约下单</a>       <a href=''>在线续约下单</a>      <a href=''>在线续约下单</a>";


}
