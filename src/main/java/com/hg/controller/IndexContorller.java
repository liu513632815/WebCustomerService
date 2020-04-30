package com.hg.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.hg.service.IndexService;
import com.hg.socket.Setting;

@Controller
public class IndexContorller {

	@Autowired
	private IndexService service;

	/**
	 * 控制台
	 * 
	 * @return
	 */
	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("data", service.getIndexPageInfo());
		return "index";
	}

	/**
	 * 客服接口
	 * 
	 * @return
	 */
	@RequestMapping("/customer")
	public String customerSocket() {
		return "customerSocket";
	}

	/**
	 * 客户接口
	 * 
	 * @return
	 */
	@RequestMapping("/consumer")
	public String consumerSocket() {
		return "consumerSocket";
	}

	/**
	 * 编辑广告接口
	 * 
	 * @return
	 */
	@RequestMapping("/editAd")
	public String editAd(Model model) {
		model.addAttribute("old_ad", Setting.adReply);
		return "editAd";
	}

	@ResponseBody
	@RequestMapping("/saveAd")
	public String saveAd(@ModelAttribute("context") String context) {
		Setting.adReply = context;
		return new Gson().toJson("保存成功！");
	}

	/**
	 * 编辑回复接口
	 * 
	 * @return
	 */
	@RequestMapping("/editReply")
	public String editReply(Model model) {
		model.addAttribute("old_reply", Setting.autoReply);
		return "editReply";
	}

	@ResponseBody
	@RequestMapping("/saveReply")
	public String saveReply(@ModelAttribute("context") String context) {
		Setting.autoReply = context;
		return new Gson().toJson("保存成功！");
	}

	/**
	 * 文件/图片上传接口
	 * 
	 * @param myFileName
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public String fileUpload(MultipartFile myFileName) throws IOException {
		return null;
	}

	/**
	 * 广告推送open/close
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/contorAd")
	public String contorAd() {
		return service.contorAd();
	}

	/**
	 * 自动回复open/close
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/contorAuto")
	public String contorAuto() {
		return service.contorAuto();
	}
}
