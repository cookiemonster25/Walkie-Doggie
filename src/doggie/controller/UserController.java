package doggie.controller;

import java.io.OutputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import doggie.user.dao.UserDao;
import doggie.user.dao.UserImageDao;
import doggie.user.dao.UserRoleDao;
import doggie.user.model.User;
import doggie.user.model.UserImage;
import doggie.user.model.UserProfile;
import doggie.user.model.UserRole;

@Controller
public class UserController {

	@Autowired
	UserDao userDao;
	
	@Autowired
	UserImageDao userImageDao;
	
	@Autowired
	UserRoleDao userRoleDao;

	@RequestMapping(value = {"/user/profile", "/user/"})
	public String userProfile(Model model, Principal principal) {
		List<User> userOpt = userDao.findByUserName(principal.getName());
		User user = userOpt.get(0);
		
		if (user.getUserProfile() == null) {
			UserImage image = new UserImage();
			UserProfile profile = new UserProfile();
			profile.setImage(image);
			user.setUserProfile(profile);
		}
		
		model.addAttribute("user", user);
		
		return "/user/profile";
	}
	
	@RequestMapping(value = "/user/show")
	public String userProfile(Model model, @RequestParam int id) {
		Optional<User> userOpt = userDao.findById(id);
		User user = userOpt.get();
		
		if (!userOpt.isPresent())
			throw new IllegalArgumentException("No User with id " + id);
		
		if (user.getUserProfile() == null) {
			UserImage image = new UserImage();
			UserProfile profile = new UserProfile();
			profile.setImage(image);
			user.setUserProfile(profile);
		}
		
		model.addAttribute("user", user);
		model.addAttribute("view", true);
		
		return "/user/profile";
	}
	
	@RequestMapping(value = "/user/promote")
	public String promoteUser(Model model, @RequestParam int id) {
		Optional<User> userOpt = userDao.findById(id);
		User user = userOpt.get();
		
		if (!userOpt.isPresent())
			throw new IllegalArgumentException("No User with id " + id);
		
		UserRole role = userRoleDao.findByRole("ROLE_EMPLOYEE");
		
		List<User> userOpti = userDao.findByIdAndUserRoles(user.getId(), role);
		if (CollectionUtils.isEmpty(userOpti)) {
			
		
		user.addUserRole(role);
		userDao.save(user);
		
		model.addAttribute("message", "User " + user.getUserName() + " zu Mitarbeiter erh�ht!");
		} else 
		model.addAttribute("errorMessage", "User " + user.getUserName() + " ist bereits Mitarbeiter!");

		return "forward:/user/listUsers";
	}
	
	@RequestMapping(value = "/user/edit", method = RequestMethod.GET)
	public String showEditProfile(Model model, Principal principal) {
		List<User> userOpt = userDao.findByUserName(principal.getName());
		User user = userOpt.get(0);
		
		if (user.getUserProfile() == null) {
			UserImage image = new UserImage();
			UserProfile profile = new UserProfile();
			profile.setImage(image);
			user.setUserProfile(profile);
		}
		model.addAttribute("user", user);
		
		return "/user/edit";
	}
	
	@RequestMapping(value = { "/user/edit" }, method = RequestMethod.POST)
	public String editProfile(@Valid UserProfile newUserProfile,
			Model model, Principal principal, BindingResult bindingResult,
			@RequestParam("date") String date) {
		
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " ist ung�ltig<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return "forward:/user/profile";
		}
		
		List<User> userOpt = userDao.findByUserName(principal.getName());
		User user = userOpt.get(0);
		
		if (user.getUserProfile() == null) {
			UserImage image = new UserImage();
			UserProfile profile = new UserProfile();
			profile.setImage(image);
			user.setUserProfile(profile);
		}
		
		UserProfile profile = user.getUserProfile();
		profile.setFirstName(newUserProfile.getFirstName());
		profile.setSurName(newUserProfile.getSurName());
		profile.setEmail(newUserProfile.getEmail());
		profile.setZip(newUserProfile.getZip());
		profile.setCity(newUserProfile.getCity());
		profile.setAddress(newUserProfile.getAddress());
		profile.setPhone(newUserProfile.getPhone());
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {				
			model.addAttribute("errorMessage", "Error: " + e.getMessage());
		}
		Date dayOfBirth = cal.getTime();
		profile.setDayOfBirth(dayOfBirth);
		
		userDao.save(user);
		
		model.addAttribute("message", "Profil " + user.getUserName() + " wurde ge�ndert!");
		
		return "forward:/user/profile";
	}
	
	@RequestMapping(value = "/user/reset")
	public String reset(Model model, Principal principal) {
		List<User> userOpt = userDao.findByUserName(principal.getName());
		User user = userOpt.get(0);
		
		user.setPassword("password");
		user.encryptPassword();

		model.addAttribute("errorMessage", "Passwort wurde zur�ckgesetzt!");
		
		return "forward:/user/profile";
	}
	
	@RequestMapping(value = "/user/upload", method = RequestMethod.GET)
	public String showUserUploadForm(Model model, Principal principal) {
		List<User> userOpt = userDao.findByUserName(principal.getName());
		User user = userOpt.get(0);

		model.addAttribute("user", user);
		return "/user/upload";
	}

	@RequestMapping(value = "/user/upload", method = RequestMethod.POST)
	public String uploadUserImage(Model model, Principal principal,
			@RequestParam("myFile") MultipartFile file) {

		try {
			List<User> userOpt = userDao.findByUserName(principal.getName());
			User user = userOpt.get(0);	
			UserProfile profile = user.getUserProfile();
			
			if (profile.getImage() != null) {
				userImageDao.delete(profile.getImage());
				profile.setImage(null);
			}

			UserImage image = new UserImage();
			image.setContent(file.getBytes());
			image.setContentType(file.getContentType());
			image.setFilename(file.getOriginalFilename());
			image.setName(file.getName());
			image.setUserProfile(profile);
			profile.setImage(image);
			user.setUserProfile(profile);
			userDao.save(user);

		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error: " + e.getMessage());
		}

		return "forward:/user/profile";
	}

	@RequestMapping("/user/image")
	public void downloadUser(@RequestParam("id") int imageId, HttpServletResponse response) {

		Optional<User> userOpt = userDao.findById(imageId);
		if (!userOpt.isPresent())
			throw new IllegalArgumentException("No image with id " + imageId);

		User user = userOpt.get();
		UserImage img = userImageDao.findByUserProfile(user.getUserProfile());

		try {
			OutputStream out = response.getOutputStream();
			response.setContentType(img.getContentType());
			out.write(img.getContent());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/user/listUsers")
	public String listUser(Model model) {

		UserRole userRole = userRoleDao.findByRole("ROLE_USER");
		List<User> users = userDao.findAllByUserRoles(userRole);
		
		model.addAttribute("users", users);
		model.addAttribute("actual", "Mitglieder");
		
		return "/user/list";
	}
	
	@RequestMapping("/user/listAdmins")
	public String listAdmins(Model model) {

		UserRole userRole = userRoleDao.findByRole("ROLE_EMPLOYEE");
		List<User> users = userDao.findAllByUserRoles(userRole);
		
		model.addAttribute("users", users);
		model.addAttribute("actual", "Mitarbeiter");
		
		return "/user/list";
	}
	
	
	
	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}
}
