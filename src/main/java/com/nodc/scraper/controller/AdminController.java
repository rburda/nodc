package com.nodc.scraper.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nodc.scraper.inventory.AdminService;
import com.nodc.scraper.inventory.ContentEditor;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;

@Controller
@RequestMapping("/admin")
public class AdminController
{
	private final AdminService adminService;
	
	@Autowired
	public AdminController(AdminService as)
	{
		this.adminService = as;
	}
	
	@InitBinder
	/**
	 * Spring uses this method as a way of configuring what can be bound to 
	 * objects in the various requestmapping methods. In our case, we have a 
	 * method saveMasterhotels that requires sending in >256 masterHotel objects
	 * in a list. By default the max is 256 so we up that to ensure we do not get
	 * an error.
	 * @param dataBinder
	 */
	public void initBinder(WebDataBinder dataBinder) 
	{
		dataBinder.setAutoGrowCollectionLimit(5000);
	}
	
	@RequestMapping(value="/editMaster", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView getMasterHotels(@ModelAttribute("model") ModelMap model)
	{
		model.put("hotelList",  adminService.getMasterRecords());
		return new ModelAndView("/admin/masterHotelEdit");
	}
	
	@RequestMapping(value="/saveMasterHotel", method=RequestMethod.POST)
	@ResponseBody
	public ModelAndView saveMasterHotels(@ModelAttribute("wrapper") SaveMasterHotelWrapper wrapper)
	{
		adminService.saveMasterRecords(wrapper.getMasterHotels());
		return new ModelAndView(new RedirectView("/admin/editMaster"));
	}
	
	@RequestMapping(value="/deleteMasterHotel", method={RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ModelAndView deleteMasterHotel(@RequestParam("id") String masterHotelId)
	{
		adminService.deleteMasterRecord(masterHotelId);
		return new ModelAndView(new RedirectView("/admin/editMaster"));
	}

	@RequestMapping(value="/viewSourceHotels", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView viewSourceHotels(@ModelAttribute("model") ModelMap map)
	{
		List<SourceHotel> sourceHotels = adminService.getSourceHotels();
		map.put("sourceHotelList",  sourceHotels);
		
		List<MasterHotel> masterHotels = adminService.getMasterRecords();
		Collections.sort(masterHotels, MasterHotel.BY_NAME);
		map.put("masterHotelList", masterHotels);
		return new ModelAndView("/admin/viewSourceHotels", map);
	}
	
	@RequestMapping(value="/editSourceHotel", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView editSourceHotel(
			@RequestParam("sourceHotelId") String sourceHotelId, 
			@RequestParam("inventorySource") InventorySource is,
			@ModelAttribute("model") ModelMap model)
	{
		SourceHotel sh = adminService.getSourceHotel(sourceHotelId, is);
		model.put("sourceHotel",  sh);
		model.put("masterHotelList", adminService.getMasterRecords());
		
		return new ModelAndView("/admin/editSourceHotel", model);
	}
	
	@RequestMapping(value="/updateSourceHotel", method=RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateSourceHotel(HttpServletRequest req, 
			@RequestParam("sourceHotelId")String externalHotelId, 
			@RequestParam("invSource")InventorySource is, 
			@RequestParam("masterHotelName")String masterHotelName)
	{
		adminService.updateSourceHotelName(externalHotelId, is, masterHotelName);
		return new ModelAndView(new RedirectView("/admin/viewSourceHotels"));
	}
	
	@RequestMapping(value="/editContent", method={RequestMethod.POST, RequestMethod.GET})
	@ResponseBody 
	public ModelAndView editContent(
			HttpServletRequest req, 
			@RequestParam("id")String masterHotelId,
			@ModelAttribute("model") ModelMap model)
	{
		model.put("contentEditor", adminService.editHotelContent(masterHotelId));
		return new ModelAndView("/admin/editContent", model);
	}
	
	@RequestMapping(value="/saveContent", method={RequestMethod.POST})
	@ResponseBody
	public ModelAndView saveContent(
			HttpServletRequest req,
			@ModelAttribute("contentEditor") ContentEditor ce)
	{
		adminService.saveHotelContent(ce);
		return new ModelAndView(new RedirectView("/admin/editMaster"));
	}
}